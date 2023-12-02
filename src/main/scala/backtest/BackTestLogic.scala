package backtest

import backtest.Signal

sealed trait BackTestLogic:
  def test(entries: List[Signal], exits: List[Signal]): List[BackTestRow]

case class SimpleMACD(command: String, above5: Map[String, Int])
    extends BackTestLogic:
  override def test(
      entries: List[Signal],
      exits: List[Signal]
  ): List[BackTestRow] =
    var exitCopy = exits.toBuffer
    entries
      .filter(includePiotroski(command, above5))
      .foldRight(List[BackTestRow]()) { case (entry, acc) =>
        exits
          .find(_.symbol == entry.symbol)
          .filterNot(s => acc.exists(br => br.entry.symbol == s.symbol))
          .map { exit =>
            exitCopy = exitCopy.filterNot(_ == exit)
            BackTestRow(entry, exit) :: acc
          }
          .getOrElse(acc)
      }

case class StrongBreakout(command: String, above5: Map[String, Int])
    extends BackTestLogic:
  override def test(
      entries: List[Signal],
      exits: List[Signal]
  ): List[BackTestRow] =
    entries
      .zip(exits)
      .filter((entry, exit) => includePiotroski(command, above5)(entry))
      .map((entry, exit) => BackTestRow(entry, exit))

def includePiotroski(command: String, above5: Map[String, Int])(
    entry: Signal
): Boolean =
  if (command.toLowerCase == "with") above5.contains(entry.symbol) else true
