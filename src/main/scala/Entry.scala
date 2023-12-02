import Operations.{Consolidate, ScreenerToTV, TVImport}
import utils.{fileContentsToStrings, safeRead}
import utils.fileFromDownloadFolder

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
    case "fakeCall"              => fakeCall
    case "Score"                 => score()
    case "ConsistentCompounders" => compounders()
    case "Ranking"               => ranking()
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
  tvFileImport("pnl-extracted.csv")(pnl)
  println("Extracting PnL - Done.")

case class Price(ticker: String, price: Float)
case class ROCRank(ticker: String, roc: Int)

extension (list: List[String])
  def asNifty500: List[String] =
    list
      .drop(2)
      .map(_.split(",")(0).trim)

  def asPrice: List[Price] =
    list
      .drop(1)
      .map { r =>
        val split = r.split(",")
        Price(split(1).trim, split(8).toFloat)
      }

extension (list: List[Price])
  def toROCRanking(toFind: Price): Option[ROCRank] =
    list
      .find(p => p.ticker == toFind.ticker)
      .map(p => rocOf(toFind, p))

  def filterOnIndex(index: List[String]): List[Price] =
    list
      .filter(l => index.iterator.exists(i => i.contains(l.ticker)))

def rocOf(currentPrice: Price, pastPrice: Price): ROCRank =
  ROCRank(currentPrice._1, calROC(currentPrice, pastPrice))

def calROC(current: Price, past: Price): Int =
  (((current._2 - past._2) / past._2) * 100).toInt

def ranking(): Unit =
  println("Gathering Nifty 500 components...")
  val nifty500FilePath = s"/Users/akhil/Downloads/data/Nifty500.csv"
  val nifty500 = fileContentsToStrings(new File(nifty500FilePath)).asNifty500

  println("Creating ranking..")
  val currentDate = s"/Users/akhil/Downloads/data/EQ290124.csv"
  val pastDate = s"/Users/akhil/Downloads/data/EQ281123.csv"

  val currentPrices =
    fileContentsToStrings(new File(currentDate)).asPrice.filterOnIndex(nifty500)
  val pastPrices =
    fileContentsToStrings(new File(pastDate)).asPrice.filterOnIndex(nifty500)

  val ranked = currentPrices
    .flatMap(pastPrices.toROCRanking)
    .sortWith((roc, rocNxt) => roc._2 > rocNxt._2)
    .take(50)

  println(ranked)
  tvFileImport("Week 4.txt")(ranked.map(_.ticker).mkString(","))

def consolidateHoldings(fileName: String) =
  println("Reading File....")
  val filePath = s"/Users/akhil/Downloads/$fileName.csv"
  val holdings = fileContentsToStrings(new File(filePath)).asHoldings
  consolidate(holdings)

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

def fakeCall =

  val url = new URL("https://www.google.com")
  val connection = url.openConnection().asInstanceOf[HttpURLConnection]
  connection.setRequestMethod("GET")

  val responseCode = connection.getResponseCode
  println(s"Response Code from Google is $responseCode")
