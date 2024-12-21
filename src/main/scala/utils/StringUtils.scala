package utils

import java.io.File
import java.text.SimpleDateFormat
import java.util.{Date, Locale}

extension (s: String)

  def csvToStrings: List[String] = fileContentsToStrings(new File(s))

  def toDate: Date =
    new SimpleDateFormat("dd-MM-yyyy h:mm a", Locale.ENGLISH)
      .parse(s)

  def isInt: Boolean =
    s.toIntOption.isDefined

  def writeToCsv(fileName: String): Unit =
    tvFileImport(fileName)(s)
