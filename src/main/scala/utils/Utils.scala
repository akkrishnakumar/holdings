package utils

import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.{Date, Locale}
import scala.io.Source
import scala.util.Using

def safeRead[T](fileName: String)(f: List[String] => T): T =
  safeRead(new File(fileName))(f)

def safeRead(file: File): List[String] =
  safeRead[List[String]](file)(identity)

def safeRead[T](file: File)(f: List[String] => T): T =
  Using.resource(Source.fromFile(file))(s => f(s.getLines.toList))

extension (d: Date)
  def toSimpleDateString: String =
    new SimpleDateFormat("yyyy-M-d").format(d)

def today =
  val date = LocalDate.now
  val formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy")
  date.format(formatter)
