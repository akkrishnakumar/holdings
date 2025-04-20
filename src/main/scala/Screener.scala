import org.openqa.selenium.firefox.FirefoxDriver
import utils.{isInt, tvFileImport}

import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.*

type Screen = (String, ScanXpath)

case class ScanXpath(name: String, id: String, link: String) {
  override def toString: String =
    s"//a[@href='/$link/$id/${if (!name.isBlank) s"$name/" else ""}']".trim
}

def SectorXpath(name: String, id: String) =
  ScanXpath(name, id, "company/compare")

def ScreenXpath(name: String, id: String) =
  ScanXpath(name, id, "screens")

val canslimSoicScan = "//a[@href='/screens/1364210/canslim-soic/']"
val highPiotroskiScan = "//a[@href='/screens/1296104/high-piotroski/']"
// val relativeStrengthScan = "//a[@href='/screens/1267185/relative-strength/']"
val relativeStrengthScan = ScreenXpath("relative-strength", "1267185")
val growthWithMomtmScan = ScreenXpath("strong-growth-with-momentum", "1936281")

extension (s: String)
  def toScreen: Screen =
    s match
      case "IT" =>
        "IT sector.txt" -> SectorXpath("", "00000034")
      case "PHARMA" =>
        "Pharma sector.txt" -> SectorXpath("", "00000046")
      case "HEALTHCARE" =>
        "Healthcare sector.txt" -> SectorXpath("", "00000030")
      case "SUGAR" =>
        "Sugar sector.txt" -> SectorXpath("", "00000059")
      case "NonFerrous" =>
        "Non Ferrous Metals Sector.txt" -> SectorXpath("", "00000040")
      case "Steel" =>
        "Steel Sector.txt" -> SectorXpath("", "00000057")

      // FINANce 00000026
      case _ =>
        "" -> SectorXpath("", "")

def compounders(): Unit = downloadScreen(
  "Consistent Compounders.txt" -> ScreenXpath(
    "consistent-compounders",
    "2204721"
  )
)

def downloadSector(sector: String): Unit =
  downloadScreen(sector.toScreen)

def downloadScreen(screen: Screen): Unit =
  loginAndThen { implicit driver =>
    tvFileImport(screen._1)(getScreenerList(screen._2).asNseTickers())
  }

def canslimSoic(): Unit =
  loginAndThen { implicit driver =>
    tvFileImport("Canslim-SOIC.txt")(
      getScreenerList(canslimSoicScan).asNseTickers()
    )
  }

def growthWithMomtm(): Unit =
  loginAndThen { implicit driver =>
    tvFileImport("03 - Growth With Momentum.txt")(
      getScreenerList(growthWithMomtmScan).asNseTickers()
    )
  }

def highPiotroski(): Unit =
  loginAndThen { implicit driver =>
    tvFileImport("03 - High Piotroski.txt")(
      getScreenerList(highPiotroskiScan).asNseTickers()
    )
  }

type Filter = String => String

val bseMap = Map("NSE:KOVAI" -> "KOVAI", "NSE:MINDSPACE" -> "MINDSPACE")
val Filter_RS: Filter = (ticker: String) =>
  bseMap
    .find((k, _) => k == ticker)
    .map((_, v) => v)
    .getOrElse(ticker)

def relativeStrength(): Unit =
  loginAndThen { implicit driver =>
    val list = getScreenerList(relativeStrengthScan)
    tvFileImport("High RS.txt")(list.asNseTickers(Filter_RS))
    println(s"List count: ${list.size}")
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
    getScreenerAllPageListWithIndex.toList.asNseTickers()
  )

def fetchIndustryPeers(ticker: String)(implicit driver: FirefoxDriver): Unit =
  searchCompany(ticker)
  val industryElement = findElementUsingXpath("//section[@id='peers']//p/a[2]")
  val industry = industryElement.getText
  industryElement.click()
  tvFileImport(s"$industry.txt")(
    getScreenerAllPageListWithIndex.toList.asNseTickers()
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

def getScreenerList(scan: ScanXpath)(implicit
  driver: FirefoxDriver
): List[String] =
  getScreenerList(scan.toString)

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
    s"//table[@class='data-table text-nowrap striped mark-visited no-scroll-right']//tr/td[2]/a"
  ).asScala.map { e =>
    val name = e.getAttribute("href").split("/")(4)
    if (name.isInt) s"BSE:${e.getAttribute("text")}"
    else s"$name"
  }.toList
