import java.io.{File, FileReader}
import scala.io.Source

type FileReader[T] = File => List[T]

// Failure scenario has to be added.
def csvToStrings: FileReader[String] =
  (file: File) => safeRead(file)

def csvToHoldings: FileReader[Holding] =
  ???

/*
class CSVReader[T] extends FileReader[T]:
  override def apply(file: File): List[T] =
    Source.fromFile(file).getLines().toList
 */
