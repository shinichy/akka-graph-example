import MasterWorkerProtocol.Result
import akka.actor.{ActorPath, ActorRef}
import akka.pattern.pipe

import concurrent.Future

class GraphWorker(graph: Graph, masterLocation: ActorPath) extends Worker(graph, masterLocation) {
  // We'll use the current dispatcher for the execution context.
  // You can use whatever you want.
  implicit val ec = context.dispatcher

  def doWork(workSender: ActorRef, msg: AbstractInput): Unit = {
    Future {
      val algResult = algorithm.flatMap(_.execute(graph, msg))

      algResult match {
        case None =>
        case _ => workSender ! Result(msg, algResult)
      }

      WorkComplete("done")
    } pipeTo self
  }
}
