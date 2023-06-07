case class Holding(
    instrument: String,
    quantity: Int,
    avgCost: Double,
    lastTradedPrice: Double,
    currentValue: Double
)

object Holding:

  def apply(list: Array[String]) =
    new Holding(
      list(0),
      list(1).toInt,
      list(2).toDouble,
      list(3).toDouble,
      list(4).toDouble
    )

extension (list: List[String])
  def asHoldings: List[Holding] =
    list
      .drop(1)
      .map(r => Holding(r.split(",")))
