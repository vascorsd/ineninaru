package ineninaru

import zio.App
import zio.console._

object IneninaruClient
  extends App {

  val program =
    for {
      _ <- putStrLn("Ineninaru listening...")
    } yield (HiMome("here sun"))

  def run(args: List[String]) = {
    program.ignore.map(_ => 0)
  }
}
