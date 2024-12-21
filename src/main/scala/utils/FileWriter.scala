package utils

import os.Path

type FileWriter[T] = T => Boolean

def tvFileImport(fileName: String = "tv_import.txt"): FileWriter[String] =
  ticker =>
    val downloadFolder = os.home / "Downloads" / fileName
    os.write.over(downloadFolder, ticker)
    true

// TODO: rename this to be more generic
