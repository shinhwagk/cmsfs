package org.wex.cmsfs.format.impl.action

import java.io.{File, PrintWriter}

import akka.stream.Materializer
import com.typesafe.config.ConfigFactory
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import org.wex.cmsfs.format.impl.FormatTopic

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source

class AnalyzeAction(ft: FormatTopic)(implicit ex: ExecutionContext, mi: Materializer) {

  private val log = LoggerFactory.getLogger(classOf[AnalyzeAction])

  ft.analyzeTopic.subscriber.mapAsync(2) { fi =>
    val url = genUrl(fi.metricName)
    log.info("action analyze")
    actionFormat(url, fi.data)
  }.runForeach(x => println("xxxxx", x))


  def actionFormat(url: String, data: String): Future[String] = Future {
    val workDirName = executeFormatBefore(url, data)
    val rs = execScript(workDirName)
    println(rs)
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

  val formatUrl = ConfigFactory.load().getString("format.url")

  def genUrl(name: String): String = {
    List(formatUrl, name, "analyze.py").mkString("/")
  }

}