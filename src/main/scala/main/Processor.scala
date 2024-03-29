package main

import java.net.URLEncoder

import clients.Client
import parser.Parser
import scrape.Scraper

import scala.util.Try

object Processor {

  def downloadAndParse(folderPath: String, baseUrl: String, username: String, password: String): Unit = {
    println("Downloading data...")
    download(folderPath, baseUrl, username, password).get
    println("Processing...")
    parse(folderPath)
  }

  private def download(folderPath: String, baseUrl: String, username: String, password: String): Try[Unit] = {
    val client = new Client()
    for {
      encodedPassword <- Try(URLEncoder.encode(password, "UTF-8"))
      authenticatedClient <- client.login(baseUrl, username, encodedPassword)
      scraper = new Scraper(authenticatedClient)
      _ <- scraper.downloadPayslips(folderPath)
      result <- scraper.downloadForm106s(folderPath)
    } yield result
  }

  def parse(folderPath: String): Unit = {
    Parser.parseAndWriteToXls(folderPath)
    println("Results successfully written to " + folderPath)
  }

}
