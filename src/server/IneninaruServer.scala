package ineninaru

import configuration.Configuration
import zio._
import zio.console._
import zio.interop.catz._
import zio.nio.core.SocketAddress
import zio.nio.core.channels.AsynchronousServerSocketChannel
import zio.nio.core.channels.AsynchronousSocketChannel

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
      _                <- putStrLn(s"Starting on ${conf.serverHost}:${conf.serverPort}")
      serverSocket     <- server(conf.serverHost, conf.serverPort)
      _                <- putStrLn("socket: server socket bound")
      connectionsCount <- Ref.make(0)
      _                <- putStrLn("waiting client connections...")
      _                <- handleConnectionsLoop(connectionsCount, serverSocket)
    } yield ()
  }

  def server(
      host: String,
      port: Int
  ): ZIO[Any, Exception, AsynchronousServerSocketChannel] =
    for {
      socket        <- AsynchronousServerSocketChannel()
      serverAddress <- SocketAddress.inetSocketAddress(host, port)
      _             <- socket.bind(serverAddress)
    } yield (socket)

  def handleConnectionsLoop(
      connections: Ref[Int],
      server: AsynchronousServerSocketChannel
  ): ZIO[Console, Exception, Unit] =
    for {
      _ <- server.accept >>= { clientChannel =>
            for {
              connectionN <- connections.updateAndGet(_ + 1)
              _           <- putStrLn(s"${connectionN}: client connected...")

              _ <- singleConnectionHandler(connectionN, clientChannel)
                    .catchAll(ex => putStrLn(ex.getMessage))
                    .fork
            } yield ()
          }
      _ <- handleConnectionsLoop(connections, server)
    } yield ()

  def singleConnectionHandler(
      connectionN: Int,
      channel: AsynchronousSocketChannel
  ): ZIO[Console, Exception, Unit] = {
    for {
      chunk <- channel.read(16)
      _     <- putStrLn(s"${connectionN}: received = ${chunk.toArray.mkString}")
      _     <- channel.write(chunk)
    } yield ()
  }.whenM(channel.isOpen)
    .forever
    .ensuring(putStrLn(s"${connectionN}: closing...") *> channel.close.orDie)
}
