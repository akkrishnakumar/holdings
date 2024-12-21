package utils

import java.io.{File, FileReader}
import scala.io.Source

type FileReader[T] = File => List[T]

// TODO: Failure scenario has to be added.
def fileContentsToStrings: FileReader[String] =
  (file: File) => safeRead(file)
