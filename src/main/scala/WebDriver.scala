import org.openqa.selenium.{By, WebElement}
import org.openqa.selenium.firefox.{FirefoxDriver, FirefoxOptions}

import java.time.Duration.ofSeconds
import java.util

case class UIElement(
  description: String,
  name: Option[String] = None,
  xpath: Option[String] = None
):
  override def toString: String = description

def getFFDriver: FirefoxDriver =
  // Set the path to your ChromeDriver executable
  System.setProperty("webdriver.gecko.driver", "src/resources/geckodriver");

  // Set Firefox in headless mode
  val options = new FirefoxOptions()
  options.addArguments("-headless")

  implicit val driver: FirefoxDriver = new FirefoxDriver(options);
  driver.manage.timeouts.implicitlyWait(ofSeconds(5))

  driver

def goTo(url: String)(implicit driver: FirefoxDriver): Unit =
  driver.get(url)

def clickUsingXpath(xpath: String)(implicit driver: FirefoxDriver): Unit =
  val e = findElementUsingXpath(xpath)
  println(s"Clicking Element - $xpath")
  e.click()

def findElementUsingXpath(xpath: String)(implicit
  driver: FirefoxDriver
): WebElement =
  println(s"Finding Element - $xpath")
  driver.findElement(By.ByXPath(xpath))

def findElementUsingName(name: String)(implicit
  driver: FirefoxDriver
): WebElement =
  println(s"Finding element - $name")
  driver.findElement(By.ByName(name))

def findElementsUsingXpath(xpath: String)(implicit
  driver: FirefoxDriver
): util.List[WebElement] =
  println(s"Finding Elements = $xpath")
  driver.findElements(By.ByXPath(xpath))

def sendKeysUsingName(text: String, name: String)(implicit
  driver: FirefoxDriver
): Unit =
  val e = findElementUsingName(name)
  println(s"Entering text into element - $name")
  e.sendKeys(text)

def sendKeysUsingXpath(text: String, xpath: String)(implicit
  driver: FirefoxDriver
): Unit =
  val e = findElementUsingXpath(xpath)
  println(s"Entering text into element - $xpath")
  e.sendKeys(text)

extension (driver: FirefoxDriver)
  def silentShutdown(): Unit =
    try driver.close()
    catch case _ => ()

    try driver.quit()
    catch case _ => ()

def withImplicitDriver(f: FirefoxDriver => Unit): Unit =
  implicit val driver: FirefoxDriver = getFFDriver
  try
    f(driver)
    driver.silentShutdown()
  catch
    case e: Exception =>
      e.printStackTrace()
      try driver.silentShutdown()
      catch case _ => ()
      ()
