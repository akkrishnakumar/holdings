import Ticker.sanitize
import backtest.Signal
import utils.toDate

import java.util.Date

class Ticker(val name: String)

object Ticker:
  def apply(t: String) =
    new Ticker(sanitize(t))

  def sanitize(s: String): String =
    s
      .replace("&", "_")
      .replace("-", "_")
      .replace("\"", "")
      .trim

extension (list: List[String])

  def asNseTickers(filter: String => String = identity): String =
    list
      .map(Ticker(_).name)
      .map(filter)
      .mkString(",")

  def asTVTickers(symbolCol: Int = 1): String =
    asTVTickersList(symbolCol)
      .mkString(",")

  def asTVTickersList(symbolCol: Int = 1) =
    list
      .drop(1)
      .map(r => Ticker(r.split(",")(symbolCol)))
      .map(_.name)

  def asNormalizedList(symbolCol: Int = 1): List[String] =
    list
      .drop(1)
      .map(r => sanitize(r.split(",")(symbolCol)))

  def asSignal: List[Signal] =
    list
      .drop(1)
      .map { r =>
        val rSplit = r.split(",")
        backtest.Signal(rSplit(0).toDate, rSplit(1))
      }

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

extension (list: List[(String, String)])
  def asNseTickerList: List[String] =
    list.map { case (ticker, _) =>
      val tvCompatibleTicker = ticker.replaceAll("-", "_")
      s"NSE:$tvCompatibleTicker"
    }
