package main

import java.awt.EventQueue
import java.nio.file.Paths

import ui.LoginUI

import scala.io.StdIn.readLine

object Main {

  private val companies = Map("Facebook" -> "nextage", "Wix" -> "wix", "Microsoft" -> "microsoft", "Fiverr" -> "fiverr", "Taboola" -> "taboola", "Intel" -> "intel")
  private val pathArg = "path"
  private val companyArg = "company"
  private val usernameArg = "username"
  private val passwordArg = "password"

  def main(args: Array[String]): Unit = {
    if (args.length == 0) {
      runGUI()
    } else {
      runCLI(args)
    }
  }

  private def runCLI(args: Array[String]): Unit = {
    val options = parseArgs(args)

    if (options.contains(pathArg) && options.size == 1) {
      Processor.parse(Paths.get(options(pathArg)).toString)
    } else {
      val folderPath = options.getOrElse(pathArg, getUserInput("Destination Path"))
      val company = options.getOrElse(companyArg, companies(getUserSelection("Company (Hilannet Subdomain)", companies.keys.toSeq)))
      val username = options.getOrElse(usernameArg, getUserInput("Username"))
      val password = options.getOrElse(passwordArg, getUserPassword("Password"))
      val baseUrl = s"https://$company.net.hilan.co.il"

      val folderPathString = Paths.get(folderPath).toString
      Processor.downloadAndParse(folderPathString, baseUrl, username, password)
    }
  }

  private def parseArgs(args: Array[String]): Map[String, String] = {
    val notArg = "nothing" -> "nothing"
    args.sliding(2, 2).toList.map {
      case Array("--path", value: String) => pathArg -> value
      case Array("--company", value: String) => companyArg -> value
      case Array("--username", value: String) => usernameArg -> value
      case Array("-u", value: String) => usernameArg -> value
      case Array("--password", value: String) => passwordArg -> value
      case Array("-p", value: String) => passwordArg -> value
      case _ => notArg
    }.filterNot(arg => arg == notArg).toMap

  }

  private def getUserSelection(description: String, multiSelect: Seq[String]): String = {
    println(s"$description: ")
    multiSelect.zipWithIndex.foreach(item => println(s"${item._2}) ${item._1}"))
    println(s"${multiSelect.size}) Other")
    val selected = readLine(s"Please select one [0-${multiSelect.size}]: ").trim.toInt
    if (selected >= multiSelect.size)
      getUserInput(description)
    else
      multiSelect(selected)
  }

  private def getUserInput(description: String): String = readLine(s"$description: ").trim

  private def getUserPassword(description: String): String =
    Option(System.console())
      .map(c => new String(c.readPassword(s"$description: ")))
      .getOrElse(getUserInput(description))

  private def runGUI(): Unit = {
    EventQueue.invokeLater(() => {
      val ex: LoginUI = new LoginUI()
      ex.setVisible(true)
    })
  }


}
