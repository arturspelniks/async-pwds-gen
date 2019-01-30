  import akka.actor.{ActorSystem, Props}
  import akka.testkit.{ImplicitSender, TestKit}
  import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
  import asyncpasswordgenerator._
  import scala.language.postfixOps
  import scala.concurrent.duration._
  import GeneratorUtils._
  import Generator._

  class GeneratorSpec extends TestKit(ActorSystem("GeneratorSpec"))
    with ImplicitSender
    with WordSpecLike
    with BeforeAndAfterAll
  {

    // setup
    override def afterAll(): Unit = {
      TestKit.shutdownActorSystem(system)
    }

    "A generator actor " should {
      val system = ActorSystem("PasswordGenerator")
      "generate 10 passwords" in {
        val nrOfPasswords = 10;
        val passwordHint = "Secure"
        val generator = system.actorOf(Props[Generator], "generator1")
        generator ! Initialize(nrOfPasswords, self)
        generator ! GeneratePasswordList(passwordHint, generateSecurePassword)
        val results: List[String] = receiveOne(max=5 seconds).asInstanceOf[List[String]]
        assert(results.length == 10)
      }
      "reply with error message if number of slaves not greater than 0" in {
        val nrOfPasswords = 0;
        val passwordHint = "Generator"
        val generator = system.actorOf(Props[Generator], "generator2")
        generator ! Initialize(nrOfPasswords, self)
        generator ! GeneratePasswordList(passwordHint, generateSecurePassword)
        expectMsg(ReplyWithError("number of slaves should be greater than 0"))
      }
      "reply with secure password" in {
        val nrOfPasswords = 5;
        val passwordHint = "Me"
        val generator = system.actorOf(Props[Generator], "generator3")
        generator ! Initialize(nrOfPasswords, self)
        generator ! GeneratePasswordList(passwordHint, generateSecurePassword)
        val results: List[String] = receiveOne(max=5 seconds).asInstanceOf[List[String]]
        assert(results.forall(p => isPasswordSecure(passwordScore(p))))
      }
    }
}
