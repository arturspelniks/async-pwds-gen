import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import asyncpasswordgenerator.{GeneratorUtils, Slave}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import GeneratorUtils._
import scala.concurrent.duration._
import asyncpasswordgenerator.Generator._
import Slave._

class SlaveSpec extends TestKit(ActorSystem("GeneratorSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll
{

  // setup
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A slave actor " should {
    val system = ActorSystem("PasswordGenerator")
    "reply with secure password and same id for some hint" in {
      val id = 1;
      val passwordHint = "Slave"
      val slave = system.actorOf(Props[Slave], "slave1")
      slave ! GeneratePassword(id, passwordHint, generateSecurePassword)
      val results: Seq[(Int, String)] = receiveWhile[(Int, String)](max=5 seconds, idle=500 millis, messages = 5) {
        case SecurePasswordReply(id, password) => (id, password)
      }
      assert(results.length == 1 && results.forall(t => t._1 == id && isPasswordSecure(passwordScore(t._2))))
    }

    "reply with secure password and same id for empty hint" in {
      val id = 1;
      val passwordHint = ""
      val slave = system.actorOf(Props[Slave], "slave2")
      slave ! GeneratePassword(id, passwordHint, generateSecurePassword)
      val results: Seq[(Int, String)] = receiveWhile[(Int, String)](max=5 seconds, idle=500 millis, messages = 5) {
        case SecurePasswordReply(id, password) => (id, password)
      }
      assert(results.length == 1 && results.forall(t => t._1 == id && isPasswordSecure(passwordScore(t._2))))
    }

    "reply with NON-SECURE password and same id for function, that returns NON-SECURE password" in {
      val id = 1;
      val passwordHint = "non-secure hint"
      val slave = system.actorOf(Props[Slave], "slave3")
      slave ! GeneratePassword(id, passwordHint, p => p)
      val results: Seq[(Int, String)] = receiveWhile[(Int, String)](max=5 seconds, idle=500 millis, messages = 5) {
        case SecurePasswordReply(id, password) => (id, password)
      }
      assert(results.length == 1 && results.forall(t => t._1 == id && !isPasswordSecure(passwordScore(t._2))))
    }
  }
}
