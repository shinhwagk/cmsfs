package org.wex.cmsfs.format.impl

import java.io.{File, PrintWriter}

import akka.{Done, NotUsed}
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.pubsub.{PubSubRegistry, TopicId}
import org.apache.commons.io.FileUtils
import org.wex.cmsfs.config.api.{ConfigService, DepositoryAnalyze, DepositoryCollect}
import org.wex.cmsfs.format.api.{FormatItem, FormatService}

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source

class FormatServiceImpl(pubSub: PubSubRegistry, cs: ConfigService)
                       (implicit ec: ExecutionContext, materializer: Materializer)
  extends FormatService {
  val analyzeTopic = pubSub.refFor(TopicId[(Int, Long)]("ANALYZE"))

  override def pushFormatAnalyze(monitorId: Int, collectId: Long): ServiceCall[NotUsed, Done] = ServiceCall { _ =>
    analyzeTopic.publish((monitorId, collectId))
    Future.successful(Done)
  }

  analyzeTopic.subscriber.mapAsync(1) { case (mId, cId) =>
    for {
      DepositoryCollect(_, _, _, _, data) <- cs.getDepositoryCollectById(cId).invoke()
      i <- cs.getFormatScriptById("analyze", fi.formatId).invoke()
      rs <- actionFormat(i.url, data)
      none <- {
        cs.addDepositoryAnalyze.invoke(DepositoryAnalyze(None, fi.formatId, rs))
      }
    } yield None
  }.runWith(Sink.ignore)


  def actionFormat(url: String, data: String): Future[String] = Future {
    val workDirName = readyExec(url, data)
    val rs = execScript(workDirName)
    deleteWorkDir(workDirName)
    rs
  }

  def readyExec(url: String, data: String): String = {
    val workDirName: String = createWorkDir
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
    val dirName = s"workspace/${System.currentTimeMillis().toString}"
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

  def deleteWorkDir(dirPath: String) = {
    FileUtils.deleteDirectory(new File(dirPath))
  }

  override def pushFormatAlarm: ServiceCall[FormatItem, Done] = ServiceCall { f =>
    Future.successful(Done)
  }

}
