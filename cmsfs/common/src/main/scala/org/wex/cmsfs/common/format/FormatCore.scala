package org.wex.cmsfs.common.format

import java.io.File
import java.net.URL
import java.nio.charset.Charset
import java.util.concurrent.ThreadLocalRandom

import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.wex.cmsfs.common.core.Common

trait FormatCore extends Common {
  val logger: Logger

  def createWorkDirAndDeleteAfterOperation(id: Int, dirName: String): () => Unit = {
    logger.info(s"create work dir: ${id} ${dirName}")
    val dir = new File(dirName)
    FileUtils.forceMkdir(dir)
    logger.info(s"success create work dir: ${id}  ${dirName}")

    () => {
      logger.info(s"delete work dir: ${id} ${dirName}")
      FileUtils.deleteDirectory(dir)
      logger.info(s"success delete work dir: ${id} ${dirName}")
    }
  }

  def writeData(data: String, dirPath: String): Unit =
    writeFile(s"${dirPath}/data.json", data)

  def writeArgs(args: String, dirPath: String): Unit =
    writeFile(s"${dirPath}/args.json", args)

  def downFormatScript(url: String, dirPath: String): Unit = {
    logger.info(s"start download script: ${url}")
    FileUtils.copyURLToFile(new URL(url), new File(dirPath + "/" + url.split("/").last))
    logger.info(s"success down script: ${url}")
  }

  def writeFile(fileName: String, content: String): Unit =
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

  def executeFormat(id: Int, path: String, mainFile: String, data: String, args: String): String = {
    val url: String = getUrlByPath(path)
    val workDirName = s"workspace/${ThreadLocalRandom.current.nextLong(100000000).toString}"
    val deleteWorkDirFun = createWorkDirAndDeleteAfterOperation(id, workDirName)
    downFormatScript(url, workDirName)
    writeData(data, workDirName)
    writeArgs(args, workDirName)
    val formatResult: String = executeScript(workDirName, mainFile)
    deleteWorkDirFun()
    formatResult
  }
}
