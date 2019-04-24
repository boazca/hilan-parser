package main

import java.net.URLEncoder
import java.nio.file.{Files, Paths}

import parser.Parser
import scrape.Scraper

object Processor {

  def downloadAndParse(folderPath: String, baseUrl: String, username: String, password: String): Unit = {

    if (!Files.isDirectory(Paths.get(folderPath))) throw new IllegalArgumentException(s"$folderPath is not a folder")

    download(folderPath, baseUrl, username, password)

    parse(folderPath)
  }

  private def download(folderPath: String, baseUrl: String, username: String, password: String) = {
    val encodedPassword = URLEncoder.encode(password, "UTF-8")
    val scraper = new Scraper()
    scraper.downloadPayslips(folderPath, baseUrl, username, encodedPassword).get
  }

  private def parse(folderPath: String) = {
    Parser.parseAndWriteToXls(folderPath)
  }

}
