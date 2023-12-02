package backtest

case class BackTestRow(entry: Signal, exit: Signal)

val defaultBackTestHeader: List[String] =
  List(
    "Ticker",
    "Entry Date",
    "Exit Date",
    "Entry Price",
    "Exit Price",
    "Gain/Loss"
  )
