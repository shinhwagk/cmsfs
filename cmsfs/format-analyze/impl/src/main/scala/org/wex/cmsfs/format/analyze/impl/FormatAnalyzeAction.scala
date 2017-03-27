package org.wex.cmsfs.format.analyze.impl

import java.io.{File, PrintWriter}
import java.util.Date
import java.util.concurrent.ThreadLocalRandom

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import org.apache.commons.io.FileUtils
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.common.{CmsfsAkkaStream, CmsfsPlayJson}
import org.wex.cmsfs.elasticsearch.api.ElasticsearchService
import org.wex.cmsfs.format.analyze.api.FormatAnalyzeItem
import org.wex.cmsfs.fotmer.core.FormatCore
import org.wex.cmsfs.monitor.status.impl.{CoreMonitorStatusAnalyze, MonitorStatusService}
import play.api.Configuration
import play.api.libs.json._

import scala.concurrent.Future
import scala.io.Source

class FormatAnalyzeAction(topic: FormatAnalyzeTopic,
                          override val config: Configuration,
                          mss: MonitorStatusService,
                          es: ElasticsearchService,
                          system: ActorSystem)(implicit mat: Materializer)
  extends CmsfsAkkaStream with CmsfsPlayJson with FormatCore {

  override val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private implicit val executionContext = system.dispatcher

  private val subscriber = topic.formatTopic.subscriber

  logger.info(s"${this.getClass.getName} start.")

  subscriber
    .map(elem => loggerFlow(elem, s"start format analyze ${elem._metric}"))
    .mapAsync(10)(actionFormat).withAttributes(supervisionStrategy((x) => x + " xxxx"))
    .map(elem => loggerFlow(elem, s"send format analyze ${elem._metric}"))
    .mapConcat(fai => splitAnalyzeResult(fai).toList)
    .mapAsync(10) { case (_index, _type, row) => es.pushElasticsearchItem(_index, _type).invoke(row) }.withAttributes(supervisionStrategy((x) => x + " xxxx"))
    .runWith(Sink.ignore)

  def splitAnalyzeResult(fai: FormatAnalyzeItem): Seq[(String, String, String)] = {
    try {
      val formatResult: String = fai.formatResult.get
      val _type = fai._type
      val _index = fai._index
      val _metric = fai._metric
      val utcDate = fai.utcDate
      val arr: Seq[JsValue] = Json.parse(formatResult).as[JsArray].value
      arr.map(jsonObjectAddField(_, "@timestamp", utcDate))
        .map(jsonObjectAddField(_, "@metric", _metric))
        .map(row => (_index, _type, row.toString))
    } catch {
      case ex: Exception => {
        logger.error("splitAnalyzeResult " + ex.getMessage)
        Seq()
      }
    }
  }

  def actionFormat(fai: FormatAnalyzeItem): Future[FormatAnalyzeItem] = Future {
    try {
      val url: String = genUrl(fai.path)
      val workDirName = executeFormatBefore(url, fai.collectResult, fai.args)
      val rs: String = execScript(workDirName)
      executeFormatAfter(workDirName)
      mss.putCoreMonitorStatusAnalyze(fai.id).invoke(CoreMonitorStatusAnalyze(true, new Date().toString, None))
      fai.copy(formatResult = Some(rs))
    } catch {
      case ex: Exception =>
        mss.putCoreMonitorStatusAnalyze(fai.id).invoke(CoreMonitorStatusAnalyze(false, new Date().toString, Some(ex.getMessage)))
        throw new Exception(ex.getMessage)
    }
  }

  def executeFormatBefore(url: String, data: String, args: String): String = {
    val workDirName: String = createWorkDir
    downAndWriteScript(url, workDirName)
    writeData(data, workDirName)
    writeArgs(args, workDirName)
    workDirName
  }

  def createWorkDir: String = {
    val dirName = s"workspace/${ThreadLocalRandom.current.nextLong(100000000).toString}"
    val file = new File(dirName)
    file.exists() match {
      case false => file.mkdirs()
      case true => throw new Exception(s"dirName:${dirName} exists.")
    }
    dirName
  }

  def writeData(data: String, dirPath: String): Unit = {
    writeFile(s"${dirPath}/data.json", data)
  }

  def writeArgs(args: String, dirPath: String): Unit = {
    writeFile(s"${dirPath}/args.json", args)
  }

  def downAndWriteScript(url: String, dirPath: String): Unit = {
    val script = Source.fromURL(url, "UTF-8").mkString
    writeFile(s"${dirPath}/analyze.py", script)
  }

  def writeFile(fileName: String, content: String) = {
    val file = new File(fileName)
    file.createNewFile()
    val writer = new PrintWriter(file)
    writer.write(content)
    writer.close()
  }

  def executeFormatAfter(dirPath: String) = {
    FileUtils.deleteDirectory(new File(dirPath))
  }

  import sys.process._

  def execScript(workDirName: String): String = {
    try {
      Seq("python", s"${workDirName}/analyze.py", s"${workDirName}/data.json", s"${workDirName}/args.json").!!.trim
    } catch {
      case e: Exception => {
        logger.error(e.getMessage)
        "[]"
      }
    }
  }
}