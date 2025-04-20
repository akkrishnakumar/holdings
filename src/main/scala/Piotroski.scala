import org.openqa.selenium.By.ByXPath
import org.openqa.selenium.firefox.{FirefoxDriver, FirefoxOptions}
import org.openqa.selenium.{By, WebElement}
import utils.{fileContentsToStrings, isInt, tvFileImport}

import java.io.File
import java.time.Duration
import java.time.Duration.ofSeconds
import scala.::
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.*
import scala.util.Using

val nextBtn = "//div[@class='flex-baseline options']//a[last()]"

def sortWithHighRS(fileNames: List[String], tickerColumn: Int = 1): Unit =
  withImplicitDriver { implicit driver =>

    logInToScreener
    val highRS = getHighRS

    fileNames.foreach { fileName =>

      val screener_download = fileContentsToStrings(
        new File(s"/Users/akhil/Downloads/$fileName.csv")
      ).asNormalizedList(tickerColumn)

      val highRSStocks =
        screener_download.foldRight(List.empty[String]) { case (ticker, acc) =>
          if (highRS.contains(ticker)) ticker :: acc else acc
        }

      tvFileImport(s"Weekly - HighRS.txt")(highRSStocks.asNseTickers())
    }

    println("Momentum files generated !")
  }

def getHighRS(implicit driver: FirefoxDriver): Map[String, Int] =

  val rsStocks = mutable.Map[String, Int]()

  // If next page is available, iterate through the pages.
  while (findElementUsingXpath(nextBtn).getText == "Next") {
    val ticker = getScreenerPageListTickerNames
    val fScore = getScreenerPageListFScore
    rsStocks.addAll(ticker.zip(fScore))
    findElementUsingXpath(nextBtn).click()
  }

  // Else get the contents of the current page
  val ticker = getScreenerPageListTickerNames
  val fScore = getScreenerPageListFScore
  rsStocks.addAll(ticker.zip(fScore))

  rsStocks.toMap

private def getScreenerPageListFScore(implicit driver: FirefoxDriver) =
  findElementsUsingXpath(
    s"//table[@class='data-table text-nowrap striped mark-visited']//tr/td[12]"
  ).asScala
    .map(e => e.getText.toFloat.toInt)
    .toList
