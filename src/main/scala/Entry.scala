import utils.{
  fileContentsToStrings,
  fileFromDownloadFolder,
  safeRead,
  tvFileImport,
  writeToCsv
}

import java.io.File
import java.net.{HttpURLConnection, URL}

@main
def Entry(command: String, fileName: String) =
  println("=" * 100)
  performOperation(command, fileName)
  println("=" * 100)

def performOperation(command: String, input: String) =
  command match
    case "Breakout"              => breakOutWithVolume()
    case "TvImport"              => tvImport(fileName = input)
    case "Inspect"               => inspect()
    case "SectionedByIndustry"   => sectionedByIndustry()
    case "StrongCanslim"         => strongCanslim()
    case "HighRS"                => relativeStrength()
    case "TrendTemplate"         => trendTemplate()
    case "HighPiotroski"         => highPiotroski()
    case "QualityScan"           => growthWithMomtm()
    case "Score"                 => score()
    case "ConsistentCompounders" => compounders()
    case "Sectors"               => downloadSectorConstituents(input)
    case "PnL"                   => pnl(input)
    case peerCmd if peerCmd.split("=")(0) == "Peers" =>
      fetchPeersOf(peerCmd.split("=")(1))

def pnl(fileName: String): Unit =
  val file = fileFromDownloadFolder(fileName, "csv")
  val pnl = fileContentsToStrings(new File(file))
    .map { r =>
      val split = r.split(":::")
      val name = split(0)
      val qty = split(1)
      val buyValue = split(2).replaceAll(",", "")
      val sellValue = split(4).replaceAll(",", "")
      s"$name, $qty, $buyValue, $sellValue"
    }
    .mkString("\n")
  println("Extracting PnL....")
  pnl.writeToCsv("pnl-extracted.csv")
  println("Extracting PnL - Done.")

def tvImport(fileName: String): Unit =
  println("Importing for Trading View...")
  val filePath = s"/Users/akhil/Downloads/$fileName.csv"
  tvFileImport(s"$fileName.txt")(
    fileContentsToStrings(new File(filePath)).asTVTickers(2)
  )
  println("Done!!")

def inspect(): Unit =
  println("Importing for Trading View...")
  val filePath = s"/Users/akhil/Downloads/Inspect.csv"
  tvFileImport("Inspect.txt")(
    fileContentsToStrings(new File(filePath)).asTVTickers(1)
  )
  println("Done!!")

def sectionedByIndustry(): Unit =
  println("Importing Sectioned Sectors for Trading View...")
  val filePath = s"/Users/akhil/Downloads/Inspect.csv"
  tvFileImport("Inspect.txt")(
    fileContentsToStrings(new File(filePath))
      .map(l => stockWithIndustry(l.split(",")))
      .groupBy { case (industry, _) => industry }
      .flatMap { case (industry, tickers) =>
        List(s"###$industry") ++ tickers.asNseTickerList
      }
      .toList
      .mkString(",")
  )
  println("Done!!")

private def stockWithIndustry(split: Array[String]) =
  split(1) -> split(2)

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
