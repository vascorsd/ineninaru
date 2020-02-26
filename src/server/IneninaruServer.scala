package ineninaru

import configuration.Configuration
import zio._
import zio.console._
import zio.interop.catz._

object IneninaruServer extends App {

  def run(args: List[String]): URIO[Console, Int] = {
    (for {
      _ <- putStrLn("Ineninaru, starting...")
      conf <- Configuration.definition
        .load[Task]
        .tapError(err => putStrLn(err.getMessage))
      _ <- program(conf)
    } yield ()).fold(_ => 1, _ => 0)
  }

  def program(conf: Configuration) = {
    for {
      _ <- putStrLn(s"Running on ${conf.serverHost}:${conf.serverPort}")
    } yield ()
  }
}
