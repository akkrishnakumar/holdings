package backtest

import java.util.Date
import utils.toSimpleDateString

case class Signal(date: Date, symbol: String)

// class EntrySignal extends Signal
// class ExitSignal(val date: Date, val symbol: String) extends Signal

extension (s: Signal)
  def closePriceFormula: String =
    s"INDEX(GOOGLEFINANCE(\"NSE:${s.symbol}\", \"close\", DATEVALUE(\"${s.date.toSimpleDateString}\")), 2, 2)"

  def currentPriceFormula: String =
    s"GOOGLEFINANCE(\"NSE:${s.symbol}\", \"price\")"
