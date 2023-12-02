import Operations.{Consolidate, ScreenerToTV, TVImport}

import java.io.File
import java.net.{HttpURLConnection, URL}
import scala.io.Source
import scala.quoted.*

@main
def Entry(command: String, fileName: String) =
  println("=" * 100)
  performOperation(command, fileName)
  println("=" * 100)

def performOperation(command: String, fileName: String) =
  command match
    case "Consolidate"  => consolidateHoldings(fileName)
    case "TVImport"     => tvImport(fileName)
    case "ScreenerToTV" => screenerToTv(fileName)
    case "fakeCall"     => fakeCall

def consolidateHoldings(fileName: String) =
  println("Reading File....")
  val filePath = s"/Users/akhil/Downloads/$fileName.csv"
  val holdings = csvToStrings(new File(filePath)).asHoldings
  consolidate(holdings)

def tvImport(fileName: String) =
  println("Importing for Trading View...")
  val filePath = s"/Users/akhil/Downloads/$fileName.csv"
  tvFileImport(s"$fileName.txt")(csvToStrings(new File(filePath)).asTVTickers())
  println("Done!!")

/** JS Script Get href and stock name
  * [...document.querySelectorAll("td>a")].map(e => e.href + ", " +
  * e.innerText).join(":::") Href has the NSE Ticker name. Compress stock name
  * if the Ticker name is not NSE compatible.
  */
def screenerToTv(fileName: String) =
  println("Importing from Screener to TradingView List...")
  val filePath = s"/Users/akhil/Downloads/$fileName.txt"
  val rows = safeRead(filePath)(lines => lines.head.split(":::").toList)
  tvFileImport(s"$fileName-TVImport.txt")(rows.asScreenerTickers)
  println("Done!!")

def fakeCall =

  val url = new URL("https://www.google.com")
  val connection = url.openConnection().asInstanceOf[HttpURLConnection]
  connection.setRequestMethod("GET")

  val responseCode = connection.getResponseCode
  println(s"Response Code from Google is $responseCode")
