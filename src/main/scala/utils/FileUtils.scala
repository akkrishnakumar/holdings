package utils

import java.io.File

// TODO: Make fileType type safe
// TODO: return option of File

def fileFromDownloadFolder(fileName: String, fileType: String = "txt") =
  s"/Users/akhil/Downloads/$fileName.$fileType"

def findFilesStartingWith(directoryPath: String, prefix: String): Array[File] =
  val directory = new File(directoryPath)
  if (directory.exists() && directory.isDirectory)
    directory
      .listFiles()
      .filter(file => file.isFile && file.getName.startsWith(prefix))
  else
    Array.empty
