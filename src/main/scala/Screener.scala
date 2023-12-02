import org.openqa.selenium.firefox.FirefoxDriver
import utils.{fileContentsToStrings, isInt}

import java.io.File
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.*

case class ScanXpath(name: String, id: Int) {
  override def toString: String = s"//a[@href='/screens/$id/$name/']"
}

val aggressiveScan = "//a[@href='/screens/1364210/aggressive-canslim/']"
val highPiotroskiScan = "//a[@href='/screens/1296104/high-piotroski/']"
val relativeStrengthScan = "//a[@href='/screens/1267185/relative-strength/']"
val growthWithMomtmScan =
  "//a[@href='/screens/1936281/strong-growth-with-momentum/']"

type Screen = (String, String)

def compounders(): Unit = downloadScreen(
  "01 - Consistent Compounders.txt" -> "//a[@href='/screens/2204721/consistent-compounders/']"
)

extension (s: String)
  def toScreen: Screen =
    s match
      case "IT" =>
        "IT sector.txt" -> "/company/compare/00000034/".asLink
      case "PHARMA" =>
        "Pharma sector.txt" -> "/company/compare/00000046/".asLink
      case "HEALTHCARE" =>
        "Healthcare sector.txt" -> "/company/compare/00000030/".asLink
      case _ =>
        "" -> "".asLink

  def asLink: String =
    s"//a[@href='$s']"

def downloadSectorConstituents(sector: String): Unit =
  loginAndThen { implicit driver =>
    val screen = sector.toScreen
    tvFileImport(screen._1)(getScreenerList(screen._2).asNseTickers)
  }

def downloadScreen(screen: Screen): Unit =
  loginAndThen { implicit driver =>
    tvFileImport(screen._1)(getScreenerList(screen._2).asNseTickers)
  }

def strongCanslim(): Unit =
  loginAndThen { implicit driver =>
    tvFileImport("03 - Strong-Canslim.txt")(
      getScreenerList(aggressiveScan).asNseTickers
    )
  }

def growthWithMomtm(): Unit =
  loginAndThen { implicit driver =>
    tvFileImport("03 - Growth With Momentum.txt")(
      getScreenerList(growthWithMomtmScan).asNseTickers
    )
  }

def highPiotroski(): Unit =
  loginAndThen { implicit driver =>
    tvFileImport("03 - High Piotroski.txt")(
      getScreenerList(highPiotroskiScan).asNseTickers
    )
  }

def relativeStrength(): Unit =
  loginAndThen { implicit driver =>
    tvFileImport("02 - HighRS.txt")(
      getScreenerList(relativeStrengthScan).asNseTickers
    )
  }

def fetchPeersOf(ticker: String): Unit =
  loginAndThen { implicit driver =>
    fetchSectorPeers(ticker)
    fetchIndustryPeers(ticker)
  }

def loginAndThen(f: FirefoxDriver => Unit): Unit =
  withImplicitDriver { implicit driver =>
    logInToScreener
    f(driver)
  }

def searchCompany(company: String)(implicit driver: FirefoxDriver): Unit =
  sendKeysUsingXpath(
    company,
    "//div[@class='top-nav-holder']//input[@type='search']"
  ) // Search company
  clickUsingXpath(
    "//div[@class='top-nav-holder']//ul[@class='dropdown-content visible']/li[1]"
  ) // Select the first company in the list

def fetchSectorPeers(ticker: String)(implicit driver: FirefoxDriver): Unit =
  searchCompany(ticker)
  val sectorElement = findElementUsingXpath("//section[@id='peers']//p/a[1]")
  val sector = sectorElement.getText
  sectorElement.click()
  tvFileImport(s"$sector.txt")(
    getScreenerAllPageListWithIndex.toList.asNseTickers
  )

def fetchIndustryPeers(ticker: String)(implicit driver: FirefoxDriver): Unit =
  searchCompany(ticker)
  val industryElement = findElementUsingXpath("//section[@id='peers']//p/a[2]")
  val industry = industryElement.getText
  industryElement.click()
  tvFileImport(s"$industry.txt")(
    getScreenerAllPageListWithIndex.toList.asNseTickers
  )

def logInToScreener(implicit driver: FirefoxDriver): Unit =
  // Navigate to the webpage
  goTo("https://www.screener.in/login/?")

  // Login
  sendKeysUsingName("akhil.krishnakumar.iicmr@gmail.com", "username")
  sendKeysUsingName("Akhil@123$", "password")
  clickUsingXpath("//button[@type='submit']")

  goToScreener

def goToScreener(implicit driver: FirefoxDriver): Unit =
  clickUsingXpath("//div[@class='desktop-links']//a[@href='/explore/']")

def getScreenerList(scan: String)(implicit
  driver: FirefoxDriver
): List[String] =
  clickUsingXpath(scan)
  getScreenerAllPageList.toList

private def getScreenerAllPageListWithIndex(implicit driver: FirefoxDriver) = {
  val allStocks = getScreenerAllPageList
  println("=" * 100)
  println(allStocks.filterNot(_.startsWith("BSE:")).take(10).mkString(" + "))
  println("=" * 100)
  allStocks
}

private def getScreenerAllPageList(implicit driver: FirefoxDriver) =
  val stocks = new ListBuffer[String]()

  // If next page is available, iterate through the pages.
  if (isNextBtnPresent) {
    while (findElementUsingXpath(nextBtn).getText == "Next") {
      stocks.addAll(getScreenerPageListTickerNames)
      findElementUsingXpath(nextBtn).click()
    }
  }

  // Else get the contents of the current page
  stocks.addAll(getScreenerPageListTickerNames)
  stocks

private def isNextBtnPresent(implicit driver: FirefoxDriver) =
  try
    findElementUsingXpath(nextBtn)
    true
  catch case _: Exception => false

def getScreenerPageListTickerNames(implicit
  driver: FirefoxDriver
): List[String] =
  findElementsUsingXpath(
    s"//table[@class='data-table text-nowrap striped mark-visited']//tr/td[2]/a"
  ).asScala.map { e =>
    val name = e.getAttribute("href").split("/")(4)
    if (name.isInt) s"BSE:${e.getAttribute("text")}"
    else s"NSE:$name"
  }.toList
