import MasterWorkerProtocol.{AllWorkCompleted, IsWorkCompleted, Result}
import akka.actor.Actor

class ResultHandler(filename: String) extends Actor {
  val outfile = new java.io.PrintWriter(filename)
  var isWorkCompleted = false

  def receive = {
    case Result(input, result) => outfile.println(input + " " + result)
    case AllWorkCompleted =>
      outfile.close()
//      context.system.shutdown()
      isWorkCompleted = true
    case IsWorkCompleted => sender ! isWorkCompleted
  }
}
