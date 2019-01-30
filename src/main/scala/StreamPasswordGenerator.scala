import akka.stream._
import akka.stream.scaladsl._
import akka.{ NotUsed }
import akka.actor.ActorSystem
import scala.concurrent._
import asyncpasswordgenerator.GeneratorUtils._

object StreamPasswordGenerator extends App {

  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher
  val source: Source[Int, NotUsed] = Source(1 to 10)
  val passwordHint = "Secure"
  val parallelism = Runtime.getRuntime.availableProcessors
  val pwdSeqSink = Sink.fold[Seq[String], String](Seq())((acc, p) => acc :+ p)
  val pwdSeq = source
    .mapAsync(parallelism)(_ => Future(generateSecurePassword(passwordHint)))
    .toMat(pwdSeqSink)(Keep.right)

  pwdSeq.run().onComplete{
    s ⇒
      println(s)
      system.terminate()
  }
}
