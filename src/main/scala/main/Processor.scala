package main

import java.net.URLEncoder
import java.nio.file.{Files, Paths}

import parser.Parser
import scrape.Scraper

object Processor {

  def downloadAndParse(folderPath: String, baseUrl: String, username: String, password: String): Unit = {
    download(folderPath, baseUrl, username, password)
    parse(folderPath)
  }

  private def download(folderPath: String, baseUrl: String, username: String, password: String) = {
    val encodedPassword = URLEncoder.encode(password, "UTF-8")
    val scraper = new Scraper(baseUrl, username, encodedPassword)
    scraper.downloadPayslips(folderPath, baseUrl, username).get
    scraper.downloadForm106s(folderPath, baseUrl, username).get
  }

  private def parse(folderPath: String) = {
    Parser.parseAndWriteToXls(folderPath)
  }

}
