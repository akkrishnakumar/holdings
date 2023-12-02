package utils

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.*

import java.io.FileOutputStream
import scala.util.Using

def writeXlsx[T](fileName: String, h: List[String], l: List[T])(
    f: (Row, T, Int) => Unit
) =
  Using(new HSSFWorkbook()) { wb =>
    val sheet = wb.createSheet()

    val headerRow = sheet.createRow(0)
    h.zipWithIndex.foreach { (header, i) =>
      headerRow.createCell(i).setCellValue(header)
    }

    l.zipWithIndex.foreach { (e, i) =>
      val row = sheet.createRow(i + 1)
      f(row, e, i + 2)
    }

    h.zipWithIndex.foreach((_, i) => sheet.autoSizeColumn(i))

    val downloadFolder = os.home / "Downloads" / fileName
    Using(new FileOutputStream(s"$downloadFolder.xlsx")) { out =>
      wb.write(out)
    }

    println(s"WorkBook Created: $downloadFolder")
  }
