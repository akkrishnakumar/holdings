import java.io.File
import scala.io.Source
import scala.util.Using

def safeRead[T](fileName: String)(f: List[String] => T): T =
  safeRead(new File(fileName))(f)

def safeRead(file: File): List[String] =
  safeRead[List[String]](file)(identity)

def safeRead[T](file: File)(f: List[String] => T): T =
  Using.resource(Source.fromFile(file))(s => f(s.getLines.toList))
