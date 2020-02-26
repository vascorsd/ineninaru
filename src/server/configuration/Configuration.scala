package configuration

import ciris._
import cats.implicits._

final case class Configuration private (
    serverHost: String,
    serverPort: Int
)

object Configuration {
  val definition: ConfigValue[Configuration] = {
    (
      env("INENINARU_HOST")
        .default("127.0.0.1"),
      env("INENINARU_PORT")
        .as[Int]
        .default(8080)
    ).parMapN(Configuration.apply)
  }
}
