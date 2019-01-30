package asyncpasswordgenerator

import akka.actor.{Actor, ActorLogging}
/*
one slave will generate one password and reply to generator
 */
class Slave extends Actor with ActorLogging {
  import Generator._
  import Slave._
  override def receive: Receive = {
    case GeneratePassword(id: Int, passwordHint: String, passwordGenerator) =>
      log.info(s"[$id] received $passwordHint")
      sender() ! SecurePasswordReply(id, passwordGenerator(passwordHint))
  }
}

object Slave {
  final case class GeneratePassword(id: Int, passwordHint: String, passwordGenerator: String => String)
}