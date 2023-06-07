class Ticker(val name: String)

object Ticker:
  def apply(t: String) =
    new Ticker(
      "NSE:" +
        t.replace("&", "_").replace("-", "_")
    )

extension (list: List[String])

  def asTVTickers(symbolCol: Int = 1) =
    list
      .drop(1)
      .map(r => Ticker(r.split(",")(symbolCol)))
      .map(_.name)
      .mkString(",")

  def asScreenerTickers =
    list
      .map { row =>
        val r = row.split(",")
        r(0) -> r(1)
      }
      .map { (link, fallback) =>
        val ticker = link.split("/")(4)
        if (!ticker.forall(Character.isDigit)) Ticker(ticker).name
        else Ticker(fallback).name
      }
      .mkString(",")
