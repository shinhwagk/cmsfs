package org.wex.cmsfs.format.impl

import java.io.{File, PrintWriter}

import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.pubsub.{PubSubRegistry, TopicId}
import com.typesafe.config.ConfigFactory
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import org.wex.cmsfs.config.api.ConfigService
import org.wex.cmsfs.format.api.{FormatItem, FormatService}

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source

class FormatServiceImpl(pubSub: PubSubRegistry, cs: ConfigService)
                       (implicit ec: ExecutionContext, materializer: Materializer) extends FormatService {

  private val log = LoggerFactory.getLogger(classOf[FormatServiceImpl])

  val analyzeTopic = pubSub.refFor(TopicId[(String, String)]("ANALYZE"))

  analyzeTopic.subscriber.map { p => num = num - 1; log.info(s"shoudao down ${num}"); p }.mapAsync(10) { case (url, data) => actionFormat(url, data) }.runWith(Sink.ignore)

  var num = 0

  override def pushFormatAnalyze(metricName: String, collectData: String): ServiceCall[NotUsed, Done] = ServiceCall { _ =>
    val url = genUrl(metricName)
    analyzeTopic.publish((url, collectData))
    num = num + 1
    log.info(s"shoudao up ${num}")
    Future.successful(Done)
  }

  override def pushFormatAlarm(monitorId: Int, collectId: Long): ServiceCall[FormatItem, Done] = ServiceCall { f =>
    Future.successful(Done)
  }

  //  analyzeTopic.subscriber.mapAsync(1) { case (metricName, collectData) =>
  //    for {
  //      MonitorDepository(_, _, _, _, data) <- cs.getMonitorDepositoryById(cId).invoke()
  //      //      i <- cs.getFormatScriptById("analyze", fi.formatId).invoke()
  //      rs <- actionFormat(FormatConfigure.formatUrl, data)
  //    //      none <- {
  //    //        cs.addDepositoryAnalyze.invoke(DepositoryAnalyze(None, fi.formatId, rs))
  //    //      }
  //    } yield None
  //  }.runWith(Sink.ignore)

  def actionFormat(url: String, data: String): Future[String] = Future {
    val workDirName = executeFormatBefore(url, data)
    println(workDirName)
    val rs = execScript(workDirName)
    executeFormatAfter(workDirName)
    rs
  }

  def executeFormatBefore(url: String, data: String): String = {
    val workDirName: String = createWorkDir
    println(url)
    downAndWriteScript(url, workDirName)
    writeData(data, workDirName)
    workDirName
  }

  def execScript(workDirName: String): String = {
    import sys.process._
    try {
      Seq("python", s"${workDirName}/format.py", s"${workDirName}/data.json").!!.trim
    } catch {
      case e: Exception => "[]"
    }
  }

  def createWorkDir: String = {
    val dirName = s"workspace/${System.nanoTime().toString}"
    val file = new File(dirName)
    file.exists() match {
      case true => createWorkDir
      case false => file.mkdir()
    }
    dirName
  }

  def downAndWriteScript(url: String, dirPath: String): Unit = {
    val script = Source.fromURL(url, "UTF-8").mkString
    writeFile(s"${dirPath}/format.py", script)
  }

  def writeData(data: String, dirPath: String): Unit = {
    writeFile(s"${dirPath}/data.json", data)
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

  def genUrl(name: String): String = {
    val formatUrl = ConfigFactory.load().getString("format.url")
    List(formatUrl, name, "analyze.py").mkString("/")
  }
}
