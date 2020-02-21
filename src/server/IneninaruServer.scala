package ineninaru

import zio.App
import zio.console._

object IneninaruServer
  extends App {

  val program =
    for {
      _ <- putStrLn("Ineninaru howling... awwwooooooo!!")
    } yield ()

  def run(args: List[String]) = {
    program.ignore.map(_ => 0)
  }
}
