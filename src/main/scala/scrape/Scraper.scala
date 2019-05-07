package scrape

import java.io.{BufferedOutputStream, FileOutputStream, InputStream}
import java.nio.file.{Files, Paths}

import clients.{AuthenticatedClient, HilanDate}
import org.slf4j.LoggerFactory

import scala.util.Try

class Scraper(authenticatedClient: AuthenticatedClient) {
  private val logger = LoggerFactory.getLogger(classOf[Scraper])

  def downloadPayslips(folderPath: String): Try[Unit] = {
    for {
      payslipDates <- authenticatedClient.getAllPayslipsDates()
      downloaded <- downloadPayslipsToFolder(folderPath, payslipDates)
    } yield downloaded
  }

  def downloadForm106s(folderPath: String): Try[Unit] = {
    for {
      form106Dates <- authenticatedClient.getAllForm106Dates()
      downloaded <- downloadForm106sToFolder(Paths.get(folderPath, "Form106").toString, form106Dates)
    } yield downloaded
  }

  private def downloadPayslipsToFolder(folderPath: String, payslipDates: Seq[HilanDate]) = {
    Try(payslipDates.foreach { date =>
      val fileName = s"PaySlip-${date.year}-${date.month}.pdf"
      val fullDate = s"${date.day}/${date.month}/${date.year}"
      val stream = authenticatedClient.getPayslipFileStream(fileName, fullDate)
      downloadFile(folderPath, fileName, stream)
    })
  }

  private def downloadForm106sToFolder(folderPath: String, form106Dates: Seq[HilanDate]) = {
    Try(form106Dates.foreach { date =>
      val fileName = s"Form106-${date.year}.pdf"
      val stream = authenticatedClient.getForm106FileStream(fileName, date.year)
      downloadFile(folderPath, fileName, stream)
    })
  }

  private def downloadFile(localFolderPath: String, localFileName: String, remoteFile: InputStream): Unit = {
    if (!Files.isDirectory(Paths.get(localFolderPath))) Files.createDirectory(Paths.get(localFolderPath))
    val localFileDestination = new java.io.File(Paths.get(localFolderPath, localFileName).toString)
    val out = new BufferedOutputStream(new FileOutputStream(localFileDestination))
    val byteArray = Stream.continually(remoteFile.read).takeWhile(_ != -1).map(_.toByte).toArray
    out.write(byteArray)
    out.flush()
    out.close()
    logger.debug(s"finished downloading $localFileName")
  }

}

class NoDataFoundException(msg: String) extends RuntimeException(msg)
