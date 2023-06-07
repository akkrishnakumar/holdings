import scala.quoted.*

type FieldNames = List[String]

def showFields[T]: FieldNames = ???

def createInstance[T: Type](using Quotes): T = ???
