import utils.{fileContentsToStrings, fileFromDownloadFolder, safeRead, tvFileImport, writeToCsv, findFilesStartingWith}

import java.io.File
import java.net.{HttpURLConnection, URL}

@main
def Entry(command: String, fileName: String, stockCol: Int, industryCol: Int) =
  println("=" * 100)
  performOperation(command, fileName, stockCol, industryCol)
  println("=" * 100)

def performOperation(command: String, input: String, stockCol: Int, industryCol: Int) =
  command match
    case "Breakout"              => breakOutWithVolume()
    case "TvImport"              => tvImport(fileName = input)
    case "Inspect"               => inspect()
    case "DailySectorMvmt"       => dailySectorMvmt(fileName = input, stockCol, industryCol)
    case "CanslimSoic"           => canslimSoic()
    case "HighRS"                => relativeStrength()
    case "TrendTemplate"         => trendTemplate()
    case "HighPiotroski"         => highPiotroski()
    case "QualityScan"           => growthWithMomtm()
    case "Score"                 => score()
    case "ConsistentCompounders" => compounders()
    case "Sectors"               => downloadSector(input)
    case "PnL"                   => pnl(input)
    case "NiftyIndices"          => niftyIndices()
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
  tvImport(fileName, fileName)

def tvImport(chartinkFileName: String, exportFileName: String) =
  println("Importing for Trading View...")
  val filePath = s"/Users/akhil/Downloads/$chartinkFileName.csv"
  tvFileImport(s"$exportFileName.txt")(fileContentsToStrings(new File(filePath)).asTVTickers(2))
  println("Done!!")

def inspect(): Unit =
  println("Importing for Trading View...")
  val filePath = s"/Users/akhil/Downloads/Inspect.csv"
  tvFileImport("Inspect.txt")(fileContentsToStrings(new File(filePath)).asTVTickers(1))
  println("Done!!")

// TODO
// Search download folder for all files starting with "ind"
// Create index tv export out of it.
// TODO: Output custom Index also

def niftyIndices(): Unit =
  println("Exporting Nifty indices.....")
  val files = findFilesStartingWith("/Users/akhil/Downloads/", "ind_")
  if (!files.isEmpty)
    files.foreach { f =>
      val fileName = f.getName.split("_")(1).replace("nifty", "")
      val tickers = fileContentsToStrings(f).tail
        .map(row => Ticker(row.split(",")(2)).name)

      println("-" * 100)
      println(s"Generating TV Import for: $fileName...")
      tvFileImport(s"$fileName.txt")(tickers.mkString(","))
      println("-" * 100)
    }
    println("Done!")
  else println("No files with prefix 'ind_' found.")

def dailySectorMvmt(fileName: String, stockCol: Int, IndustryCol: Int): Unit =
  println("Movers and sorting with Industry for Trading View...")
  val filePath = s"/Users/akhil/Downloads/$fileName.csv"
  tvFileImport(s"$fileName.txt") {
    fileContentsToStrings(new File(filePath)).tail
      .map(_.stockAndIndustry(stockCol, IndustryCol))
      .groupBy { case (_, industry) => industry }
      .flatMap { case (industry, tickers) =>
        List(s"###$industry") ++ tickers.asNseTickerList
      }
      .toList
      .mkString(",")
  }
  println("Done!!")

/** JS Script Get href and stock name [...document.querySelectorAll("td>a")].map(e => e.href + ", " +
  * e.innerText).join(":::") Href has the NSE Ticker name. Compress stock name if the Ticker name is not NSE compatible.
  */
def screenerToTv(fileName: String) =
  println("Importing from Screener to TradingView List...")
  val filePath = s"/Users/akhil/Downloads/$fileName.txt"
  val rows = safeRead(filePath)(lines => lines.head.split(":::").toList)
  tvFileImport(s"$fileName-TVImport.txt")(rows.asScreenerTickers)
  println("Done!!")

extension (s: String)
  def stockAndIndustry(stock: Int, industry: Int): (String, String) =
    val split = s.split(",")
    split(stock) -> split(industry)
