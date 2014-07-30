import akka.actor.ActorRef

object MasterWorkerProtocol {
  // Messages from Workers
  case class WorkerCreated(worker: ActorRef)
  case class WorkerRequestsWork(worker: ActorRef)
  case class WorkIsDone(worker: ActorRef)
  case object AllWorkSent

  // Messages to Workers
  case class WorkToBeDone(work: AbstractInput)
  case object WorkIsReady
  case object NoWorkToBeDone
  case class DoAlgorithm(algorithm: AbstractAlgorithm)

  // Messages to RequestHandler
  case object AllWorkCompleted
  case object IsWorkCompleted
  case class Result(input: AbstractInput, maybeResult: Option[AbstractResult])
}
