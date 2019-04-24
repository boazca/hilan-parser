package util

import java.io.FileOutputStream
import java.util.Date

import data.{Key, Value}
import org.apache.poi.hssf.usermodel._


object ExcelExport {

  def writeToFile(filePath: String, mappedData: Map[Key, Map[Date, Value]]): Unit = {

    val data = mappedData.toSeq.sortBy(_._1.info)
    val months = data.flatMap(_._2.keys).toList.distinct.sorted

    def getColumnByDate(date: Date) = months.indexOf(date) + 2

    val wb = new HSSFWorkbook

    val firstColumnStyle = wb.createCellStyle()
    firstColumnStyle.setReadingOrder(2)

    val payslipsSheet = wb.createSheet("Payslips")
    val bestEffortSheet = wb.createSheet("Best Effort")
    val verboseSheet = wb.createSheet("Verbose")

    setUpHeaders(payslipsSheet, months, wb, firstColumnStyle)
    setUpHeaders(bestEffortSheet, months, wb, firstColumnStyle)
    setUpHeaders(verboseSheet, months, wb, firstColumnStyle)

    fillPayslipsWorkSheet(data, payslipsSheet, getColumnByDate)

    fillWorkSheet(data, bestEffortSheet, getColumnByDate, { (cell: HSSFCell, value: Value) => cell.setCellValue(value.lastOne) })
    fillWorkSheet(data, verboseSheet, getColumnByDate, { (cell: HSSFCell, value: Value) => cell.setCellValue(value.fullLine) })

    autoSizeColumn(months.size + 2, payslipsSheet, bestEffortSheet, verboseSheet)

    val fileOut = new FileOutputStream(filePath)
    wb.write(fileOut)
    fileOut.close()
  }

  private def fillWorkSheet(data: Seq[(Key, Map[Date, Value])],
                            worksheet: HSSFSheet,
                            columnResolver: (Date => Int),
                            setCellValue: ((HSSFCell, Value) => Unit)): Unit = {
    data.zipWithIndex.foreach { case ((k, v), i) =>

      val rowInWorkSheet = createRow(worksheet, k.info, k.notes.getOrElse(""), i + 1)

      v.foreach { case (date, value) =>
        val column = columnResolver(date)
        setCellValue(rowInWorkSheet.createCell(column), value)
      }
    }
  }

  private def createRow(worksheet: HSSFSheet, first: String, seconds: String, rowNumber: Int): HSSFRow = {
    val createdRow = worksheet.createRow(rowNumber)
    createdRow.createCell(0).setCellValue(first)
    createdRow.createCell(1).setCellValue(seconds)
    createdRow
  }

  private def fillPayslipsWorkSheet(data: Seq[(Key, Map[Date, Value])], payslipsSheet: HSSFSheet, columnResolver: (Date => Int)): Unit = {

    val pensionData = data.filter(isPension).zipWithIndex

    pensionData.foreach { case ((k, v), i) =>
      val rowNumber = (i * 2) + 1
      val rowForEmployer = createRow(payslipsSheet, k.info, "מעסיק", rowNumber)
      val rowForEmployee = createRow(payslipsSheet, k.info, "עובד", rowNumber + 1)
      v.foreach { case (date, value) =>
        val column = columnResolver(date)
        rowForEmployer.createCell(column).setCellValue(value.pensionData.map(_.employer).getOrElse(-1.0))
        rowForEmployee.createCell(column).setCellValue(value.pensionData.map(_.employee).getOrElse(-1.0))
      }
    }

    val offset = pensionData.map(_._2).max * 2 + 2
    val nonPensionData = data.filterNot(isPension).zipWithIndex

    nonPensionData.foreach { case ((k, v), i) =>
      val rowWithoutPension = createRow(payslipsSheet, k.info, k.notes.getOrElse(""), i + offset + 1)
      v.foreach { case (date, value) =>
        rowWithoutPension.createCell(columnResolver(date)).setCellValue(value.lastOne)
      }
    }
  }

  private def isPension(element: (Key, Map[Date, Value])): Boolean =
    element._2.exists(_._2.pensionData.isDefined)

  private def setUpHeaders(sheet: HSSFSheet, months: Seq[Date], wb: HSSFWorkbook, firstColumnStyle: HSSFCellStyle): Unit = {

    sheet.setDefaultColumnStyle(0, firstColumnStyle)
    sheet.setDefaultColumnStyle(1, firstColumnStyle)

    val cellStyle = wb.createCellStyle
    val createHelper = wb.getCreationHelper
    cellStyle.setDataFormat(createHelper.createDataFormat.getFormat("mmm yyyy"))

    val firstRow = sheet.createRow(0)
    firstRow.createCell(0).setCellValue("תיאור")
    firstRow.createCell(1).setCellValue("הערות")

    months.zipWithIndex.foreach { case (date, i) =>
      val cell = firstRow.createCell(i + 2)
      cell.setCellValue(date)
      cell.setCellStyle(cellStyle)
    }
  }

  private def autoSizeColumn(numberOfColumn: Int, sheets: HSSFSheet*): Unit = {
    sheets.foreach(sheet => {
      for (i <- 0 to numberOfColumn) {
        sheet.autoSizeColumn(i)
      }
    })
  }
}
