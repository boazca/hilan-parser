package main

import java.awt.EventQueue

import ui.LoginUI

object Main {

  def main(args: Array[String]): Unit = {
    if (args.length == 0) {

      runUI()

    } else {

      runWithoutUI(args)
    }
  }

  private def runWithoutUI(args: Array[String]): Unit = {
    val folderPath = args.headOption.getOrElse(throw new IllegalArgumentException("no path to folder was given"))
    val baseUrl = args.lift(1).getOrElse(throw new IllegalArgumentException("no url was given"))
    val username = args.lift(2).getOrElse(throw new IllegalArgumentException("no username was given"))
    val password = args.lift(3).getOrElse(throw new IllegalArgumentException("no password was given"))

    Processor.downloadAndParse(folderPath, baseUrl, username, password)
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
