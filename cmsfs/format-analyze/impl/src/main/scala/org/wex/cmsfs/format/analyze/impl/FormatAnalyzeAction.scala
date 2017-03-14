package org.wex.cmsfs.format.analyze.impl

import java.io.{File, PrintWriter}
import java.util.concurrent.ThreadLocalRandom

import akka.stream.{ActorAttributes, Materializer, Supervision}
import akka.stream.scaladsl.Sink
import org.apache.commons.io.FileUtils
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.elasticsearch.api.ElasticsearchService
import org.wex.cmsfs.format.analyze.api.FormatAnalyzeItem
import play.api.Configuration
import play.api.libs.json.{JsArray, JsValue, Json}

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source

class FormatAnalyzeAction(topic: FormatAnalyzeTopic,
                          config: Configuration,
                          es: ElasticsearchService)(implicit ec: ExecutionContext, mi: Materializer) {

  private implicit val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private val formatUrl: String = config.getString("format.url").get

  private val subscriber = topic.formatTopic.subscriber

  logger.info("FormatAnalyzeAction start.")

  subscriber
    .map(streamLog("start format analyze", _))
    .mapAsync(10)(actionFormat).withAttributes(ActorAttributes.supervisionStrategy(decider))
    .map(streamLog("end format analyze", _))
    .mapConcat(rs => splitAnalyzeResult(rs).toList)
    .map(streamLog("view format rs s", _))
    .mapAsync(10) { case (_index, _type, rs) => es.pushElasticsearchItem(_index, _type).invoke(rs) }.withAttributes(ActorAttributes.supervisionStrategy(decider))
    .runWith(Sink.ignore)

  def splitAnalyzeResult(elem: (String, String, String)): Seq[(String, String, String)] = {
    try {
      val rs = elem._3
      val arr: Seq[JsValue] = Json.parse(rs).as[JsArray].value
      arr.map(row => (elem._1, elem._2, row.toString()))
    } catch {
      case ex: Exception => {
        logger.error(ex.getMessage)
        Seq()
      }
    }
  }

  def decider(implicit log: Logger): Supervision.Decider = {
    case ex: Exception =>
      log.error(ex.getMessage + " XXXX")
      Supervision.Resume
  }

  def genUrl(name: String): String = {
    List(formatUrl, name, "analyze.py").mkString("/")
  }

  def streamLog[T](log: String, elem: T): T = {
    logger.info(log + s" ${elem.toString}");
    elem
  }

  def actionFormat(fai: FormatAnalyzeItem): Future[(String, String, String)] = Future {
    val FormatAnalyzeItem(id, name, data, args) = fai
    val url: String = genUrl(name)
    logger.info(s"analyze ${url}")
    val workDirName = executeFormatBefore(url, data, args)
    val rs = execScript(workDirName)
    //    executeFormatAfter(workDirName)
    ("OS", name, rs)
  }

  //  def actionFormat(name: String, data: String, args: String): Future[String] = Future {
  //    val url = genUrl(name)
  //    val workDirName = executeFormatBefore(url, data, args)
  //    val rs = execScript(workDirName)
  ////    executeFormatAfter(workDirName)
  //    rs
  //  }

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

  def execScript(workDirName: String): String = {
    import sys.process._
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
