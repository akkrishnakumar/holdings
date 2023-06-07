import Operations.{Consolidate, ScreenerToTV, TVImport}

import java.io.File
import scala.io.Source
import scala.quoted.*

object Entry extends App:
  println("=" * 100)
  performOperation(ScreenerToTV)
  println("=" * 100)

def performOperation(operation: Operations) =
  operation match
    case Consolidate  => consolidateHoldings()
    case TVImport     => tvImport()
    case ScreenerToTV => screenerToTv()

def consolidateHoldings() =
  println("Reading File....")
  val filePath = "/Users/akhil/Downloads/holdings.csv"
  val holdings = csvToStrings(new File(filePath)).asHoldings
  consolidate(holdings)

def tvImport() =
  println("Importing for Trading View...")
  val filePath = "/Users/akhil/Downloads/Trend Template.csv"
  tvFileImport("Trend Template.txt")(
    csvToStrings(new File(filePath)).asTVTickers()
  )
  println("Done!!")

/** JS Script Get href and stock name
  * [...document.querySelectorAll("td>a")].map(e => e.href + ", " +
  * e.innerText).join("\n") Href has the NSE Ticker name. Compress stock name if
  * the Ticker name is not NSE compatible.
  */
def screenerToTv() =
  println("Importing from Screener to TradingView List...")
  val filePath = "/Users/akhil/Downloads/screener.txt"
  val rows = safeRead(filePath)(lines => lines.head.split(":::").toList)
  tvFileImport("CANSLIM.txt")(rows.asScreenerTickers)
  println("Done!!")
