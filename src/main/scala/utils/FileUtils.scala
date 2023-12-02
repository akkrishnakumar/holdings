package utils

// TODO: Make fileType type safe

def fileFromDownloadFolder(fileName: String, fileType: String = "txt") =
  s"/Users/akhil/Downloads/$fileName.$fileType"
