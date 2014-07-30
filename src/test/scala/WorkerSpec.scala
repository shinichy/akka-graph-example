import MasterWorkerProtocol.IsWorkCompleted
import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}
import akka.pattern.ask
import scala.concurrent.duration._

import concurrent.Await

class WorkerSpec extends TestKit(ActorSystem("WorkerSpec"))
with ImplicitSender
with WordSpecLike
with BeforeAndAfterAll
with MustMatchers {

  implicit val timeout = Timeout(5 seconds)

  override def afterAll() {
    system.shutdown()
  }

  "Worker" should {
    "work" in {
      val inputs = (1 to 10) map { SingleVertexInput(_) }
      val system = new System(DegreeAlgorithm, inputs, "output")
      system.addWorkers(10)
      waitAllWorkIsDone(system.resultHandler)
    }
  }

  def waitAllWorkIsDone(resultHandler: ActorRef): Unit = {
    while (Await.result(resultHandler ? IsWorkCompleted, 1.minute).asInstanceOf[Boolean] == false) {
      Thread.sleep(500)
    }
  }
}
