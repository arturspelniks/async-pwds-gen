package asyncpasswordgenerator

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
/*
Generator creates child actor for each password, and replies with list of generated passwords to sender
 */
class Generator extends Actor with ActorLogging {
  import Generator._
  import Slave._
  override def receive: Receive = {
    case AskForPasswords(passwordHint: String, nOfPasswords: Int, passwordGenerator) =>
      log.info(s"[GENERATOR] Asked for passwords: hint = $passwordHint, nOfPasswords = $nOfPasswords")
      val originalSender = sender()
      self ! Initialize(nOfPasswords, originalSender)
      self ! GeneratePasswordList(passwordHint, passwordGenerator)
    case Initialize(nOfSlaves, originalSender) =>
      if (nOfSlaves > 0) {
        val slaveRefs = for (i <- 0 until nOfSlaves) yield context.actorOf(Props[Slave], s"slave_$i")
        context.become(withSlave(slaveRefs, 0, 0, Map(), originalSender))
      }
      else
        sender() ! ReplyWithError("number of slaves should be greater than 0")
  }

  def withSlave(slaveRefs: Seq[ActorRef], currentSlaveIndex: Int, currentTaskId: Int, requestMap: Map[Int, String], originalSender: ActorRef): Receive = {
    case GeneratePasswordList(passwordHint: String, passwordGenerator) =>
      log.info(s"[Hint] - $passwordHint")
      val task = GeneratePassword(currentTaskId, passwordHint, passwordGenerator)
      val slaveRef = slaveRefs(currentSlaveIndex)
      slaveRef ! task

      val nextChildIndex = currentSlaveIndex + 1
      val newTaskId = currentTaskId + 1
      context.become(withSlave(slaveRefs, nextChildIndex, newTaskId, requestMap, originalSender))
      if (nextChildIndex < slaveRefs.length)
        self ! GeneratePasswordList(passwordHint, passwordGenerator)
    case SecurePasswordReply(id, password) =>
      log.info(s"[Secure Password $id] - $password")
      val newRequestMap = requestMap + (id -> password)
      if (newRequestMap.size == slaveRefs.length) {
        log.info(s"[REPLYING TO] - ${originalSender} with passwords ${requestMap.mkString}")
        originalSender ! newRequestMap.map(_._2).toSeq
      }
      else
        context.become(withSlave(slaveRefs, currentSlaveIndex, currentTaskId, newRequestMap, originalSender))
  }
}

object Generator {
  final case class Initialize(nOfSlaves: Int, originalSender: ActorRef)
  final case class GeneratePasswordList(passwordHint: String, passwordGenerator: String => String)
  final case class SecurePasswordReply(id: Int, password: String)
  final case class ReplyWithError(error: String)
  final case class AskForPasswords(passwordHint: String, nOfPasswords: Int, passwordGenerator: String => String)
}
