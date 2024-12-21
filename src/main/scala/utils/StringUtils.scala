package utils

extension (s: String)
  def writeToCsv(fileName: String): Unit =
    tvFileImport(fileName)(s)
