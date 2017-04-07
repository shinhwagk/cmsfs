package org.wex.cmsfs.common.format

import java.io.File
import java.net.URL
import java.nio.charset.Charset
import java.util.concurrent.ThreadLocalRandom

import org.apache.commons.io.FileUtils
import org.slf4j.Logger

trait FormatCore {
  val logger: Logger

  def createWorkDir(dirName: String): Unit =
    FileUtils.forceMkdir(new File(dirName))

  def writeData(data: String, dirPath: String): Unit =
    writeFile(s"${dirPath}/data.json", data)

  def writeArgs(args: String, dirPath: String): Unit =
    writeFile(s"${dirPath}/args.json", args)

  def downFormatScript(url: String, dirPath: String): Unit =
    FileUtils.copyURLToFile(new URL(url), new File(url.split("/").last))

  def writeFile(fileName: String, content: String) =
    FileUtils.writeStringToFile(new File(fileName), content, Charset.forName("UTF-8"), false)

  def executeFormatAfter(dirPath: String) =
    FileUtils.deleteDirectory(new File(dirPath))

  import sys.process._

  def executeScript(workDirName: String, mainFile: String): String = {
    try {
      Seq("python", s"${workDirName}/${mainFile}", s"${workDirName}/data.json", s"${workDirName}/args.json").!!.trim
    } catch {
      case e: Exception => {
        logger.error(e.getMessage)
        "[]"
      }
    }
  }

  def executeFormat(url: String, mainFile: String, data: String, args: String): String = {
    val workDirName = s"workspace/${ThreadLocalRandom.current.nextLong(100000000).toString}"
    createWorkDir(workDirName)
    downFormatScript(url, workDirName)
    writeData(data, workDirName)
    writeArgs(args, workDirName)
    val formatResult: String = executeScript(workDirName, mainFile)
    executeFormatAfter(workDirName)
    formatResult
  }
}
