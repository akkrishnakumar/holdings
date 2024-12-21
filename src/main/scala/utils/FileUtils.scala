package utils

// TODO: Make fileType type safe
// TODO: return option of File

def fileFromDownloadFolder(fileName: String, fileType: String = "txt") =
  s"/Users/akhil/Downloads/$fileName.$fileType"
