package main

import java.awt.EventQueue
import java.nio.file.Paths

import scala.io.StdIn.readLine
import ui.LoginUI

object Main {

  def main(args: Array[String]): Unit = {
    if (args.length == 0) {
      runUI()
    } else {
      runWithoutUI(args)
    }
  }

  private def parseOption(options: Map[String, String], args: List[String]) : Map[String, String] = {
    if (args.isEmpty) options else {
      val option :: tail = args
      option.split('=').toSeq match {
        case Seq("--path", value) =>
          parseOption(options ++ Map("path" -> value), tail)
        case Seq("--company", value) =>
          parseOption(options ++ Map("company" -> value), tail)
        case Seq("--username", value) =>
          parseOption(options ++ Map("username" -> value), tail)
        case Seq("--password", value) =>
          parseOption(options ++ Map("password" -> value), tail)
        case Seq("-u") =>
          val value :: rest = tail
          parseOption(options ++ Map("username" -> value), rest)
        case Seq("-p") =>
          val value :: rest = tail
          parseOption(options ++ Map("password" -> value), rest)
        case _ => parseOption(options, tail)
      }
    }
  }

  private def runWithoutUI(args: Array[String]): Unit = {
    val options = parseOption(Map(), args.toList)

    def getOption(name: String, description: String, multiSelect: String*): String = {
      options.find(o => o._1 == name).map(o => o._2).getOrElse({
        if (multiSelect.isEmpty)
          readLine(s"$description: ")
        else {
          println(s"$description: ")
          multiSelect.zipWithIndex.foreach[Unit](item => println(s"${item._2}) ${item._1}"))
          println(s"${multiSelect.size}) Other")
          val selected = readLine(s"Please select one [0-${multiSelect.size}]: ").toInt
          if (selected == multiSelect.size)
            getOption(name, description)
          else
            multiSelect(selected)
        }
      })
    }

    val folderPath = getOption("path", "Destination Path")
    val company = getOption("company", "Company (Hilannet Subdomain)", "Wix", "Microsoft", "Fiverr").toLowerCase
    val username = getOption("username", "Username")
    val password = getOption("password", "Password")
    val baseUrl = s"https://$company.net.hilan.co.il"

    val string = Paths.get(folderPath).toString
    Processor.downloadAndParse(string, baseUrl, username, password)
  }

  private def runUI(): Unit = {
    EventQueue.invokeLater(new Runnable {
      override def run(): Unit = {
        val ex: LoginUI = new LoginUI()
        ex.setVisible(true)
      }
    })
  }


}
