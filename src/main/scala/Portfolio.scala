import com.sun.jdi.DoubleValue

def splitIntoActivePassive(holdings: List[Holding]) =
  holdings.partition(h =>
    h.instrument.contains("BEES") || h.instrument.contains("INVIT")
  )

def consolidate(holdings: List[Holding]): Unit =
  val portfolioTotal = calTotal(holdings)
  val (passive, active) = splitIntoActivePassive(holdings)
  val passiveTotal = calTotal(passive)
  val activeTotal = calTotal(active)
  println(s"Current Portfolio Value: Rs. $portfolioTotal")
  showPassivePercent(passiveTotal, portfolioTotal)
  showActivePercent(activeTotal, portfolioTotal)

  println("Percentage of Holdings:")
  active.foreach(asPercentageWithTotal(activeTotal))

def calTotal(holdings: List[Holding]) =
  holdings.map(_.currentValue).sum

def showPassivePercent(passive: Double, portfolio: Double) =
  println(s"Passive Fund Percent: ${calPercent(passive, portfolio)} %")

def showActivePercent(active: Double, portfolio: Double) =
  println(s"Active Fund Percent: ${calPercent(active, portfolio)} %")

def asPercentageWithTotal(total: Double)(holding: Holding) =
  println(
    s"${holding.instrument}" +
      s"(${holding.quantity}, ${holding.avgCost}):" +
      s" ${calculatePercent(holding, total)} %"
  )

def calculatePercent(holding: Holding, total: Double): Int =
  ((calPositions(holding) / total) * 100).toInt

def calPositions(holding: Holding) =
  holding.avgCost * holding.quantity

def calPercent(num: Double, deno: Double) =
  ((num / deno) * 100).toInt

def activePercentFrom(holdings: List[Holding], total: Double) =
  val active =
    holdings.filterNot(_.instrument.contains("BEES")).map(calPositions).sum
  ((active / total) * 100).toInt

def passivePercentFrom(holdings: List[Holding], total: Double) =
  val passive =
    holdings.filter(_.instrument.contains("BEES")).map(calPositions).sum
  ((passive / total) * 100).toInt
