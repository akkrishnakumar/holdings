import org.openqa.selenium.firefox.FirefoxDriver
import utils.{fileContentsToStrings, tvFileImport}

import java.io.File
import scala.util.Try

val powerPlayUrl = "https://chartink.com/screener/power-play-high-tight-flag"
val near50dmaUrl = "https://chartink.com/screener/near-50dma-8"
val near20dmaUrl = "https://chartink.com/screener/flattening-sma20"
val trendTemplateUrl = "https://chartink.com/screener/trend-template-2168"

def score(): Unit =
  println("Importing for Trading View...")
  val filePath = s"/Users/akhil/Downloads/Momentum Score.csv"
  tvFileImport(s"02 - Momentum Score.txt")(
    fileContentsToStrings(new File(filePath)).asTVTickers()
  )
  println("Done!!")

def rank(rows: List[String]): List[String] =
  rows
    .drop(1)
    .sortWith((a, b) => a.split(",")(2).toFloat > b.split(",")(2).toFloat)
    .take(50)

def near50dma(): Unit =
  withImplicitDriver { implicit driver =>

    downloadFromChartink(near50dmaUrl)
    tvImport("Near 50DMA, Technical Analysis Scanner")
  }

def near20dma(): Unit =
  withImplicitDriver { implicit driver =>

    downloadFromChartink(near20dmaUrl)
    tvImport("Flattening SMA20, Technical Analysis Scanner")
  }

def trendTemplate(): Unit =
  withImplicitDriver { implicit driver =>
    downloadFromChartink(trendTemplateUrl)
    tvImport("Trend Template, Technical Analysis Scanner")
  }

val breakoutWithVolumeUrl =
  "https://chartink.com/screener/breakout-with-volume-239"

def breakOutWithVolume(): Unit =
  withImplicitDriver { implicit driver =>
    downloadFromChartink(breakoutWithVolumeUrl)
    tvImport("Breakout With Volume, Technical Analysis Scanner")
  }

private def downloadFromChartink(scanURL: String)(implicit
  driver: FirefoxDriver
): Unit =
  runScan(scanURL)
  // Click on download the scan results.
  clickUsingXpath("//button/span[contains(text(), 'CSV')]")

def downloadBackTestFromChartink(scanURL: String)(implicit
  driver: FirefoxDriver
): Unit =
  runScan(scanURL)
  // Click on download the backtest results.
  clickUsingXpath("//a[@value='Download backtest']")

def runScan(scanURL: String)(implicit driver: FirefoxDriver): Unit =
  goTo(scanURL)
  clickUsingXpath("//button[@title='Click to run scan']")
  Thread.sleep(3000)
  // TODO: Need to add wait
