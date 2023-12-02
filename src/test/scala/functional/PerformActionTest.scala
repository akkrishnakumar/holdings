package functional

import utils.BaseFeatureTest

class PerformActionTest extends BaseFeatureTest:

  info("Download latest signals from Chartink")

  Feature("Generate MACD signals") {

    Scenario("Download MACD weekly signal from Chartink") {

      Given("The paths")
      val macdScreenUrl = "https://chartink.com/dashboard/133179"
      val downloadFolderPath = ""

      When("I click download button")

      Then("I should download the file to my 'Downloads' folder")


    }

  }
