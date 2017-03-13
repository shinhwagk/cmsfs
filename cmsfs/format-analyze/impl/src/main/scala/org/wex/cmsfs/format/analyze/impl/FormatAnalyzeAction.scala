package org.wex.cmsfs.format.analyze.impl

import java.io.{File, PrintWriter}
import java.util.concurrent.ThreadLocalRandom

import org.apache.commons.io.FileUtils
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.format.analyze.impl.api.FormatAnalyzeItem
import play.api.Configuration

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source

class FormatAnalyzeAction(topic: FormatAnalyzeTopic, config: Configuration)(implicit ec: ExecutionContext) {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private val formatUrl = config.getString("format.url")

  private val subscriber = topic.formatTopic.subscriber

  //  subscriber
  //    //    .mapAsync(10)(fai => actionFormat(fai.metricName, fai.data, fai.args))
  //    .map(streamLog("start format analyze", _))
  //    .mapAsync(10)(actionFormat)
  //    .map(streamLog("end format analyze", _))
  //    .runWith(Sink.ignore)

  def genUrl(name: String): String = {
    List(formatUrl, name, "analyze.sh").mkString("/")
  }

  def streamLog[T](log: String, elem: T): T = {
    logger.info(log);
    elem
  }

  def actionFormat(fai: FormatAnalyzeItem): Future[String] = Future {
    val url = genUrl(fai.metricName)
    val workDirName = executeFormatBefore(url, fai.data, fai.args)
    val rs = execScript(workDirName)
    executeFormatAfter(workDirName)
    rs
  }

  def actionFormat(name: String, data: String, args: String): Future[String] = Future {
    val url = genUrl(name)
    val workDirName = executeFormatBefore(url, data, args)
    val rs = execScript(workDirName)
    executeFormatAfter(workDirName)
    rs
  }

  def executeFormatBefore(url: String, data: String, args: String): String = {
    val workDirName: String = createWorkDir
    downAndWriteScript(url, workDirName)
    writeData(data, workDirName)
    writeData(args, workDirName)
    workDirName
  }

  def createWorkDir: String = {
    val dirName = s"workspace/${ThreadLocalRandom.current.nextLong(100000000).toString}"
    val file = new File(dirName)
    file.exists() match {
      case false => file.mkdir()
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
      case e: Exception => "[]"
    }
  }
}
