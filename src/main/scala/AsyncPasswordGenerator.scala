import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import asyncpasswordgenerator.Generator

import scala.concurrent.ExecutionContext.Implicits.global
import asyncpasswordgenerator.Generator._
import asyncpasswordgenerator.GeneratorUtils._

import scala.concurrent.{Future}
import scala.util.{Failure, Success}
import scala.concurrent.duration._
import scala.language.postfixOps

object AsyncPasswordGenerator extends App {

  val system = ActorSystem("PasswordGenerator")
  val generator = system.actorOf(Props[Generator], "generator")
  implicit val timeout = Timeout(20 seconds)
  val futurePasswords: Future[Any] = generator ? AskForPasswords("Secure", 5, generateSecurePassword)

  futurePasswords.onComplete ({
    case Success(passwords) ⇒ println(passwords)
    case Failure(ex)      ⇒ println(s"[ERROR] ${ex.getMessage}")
  })
}