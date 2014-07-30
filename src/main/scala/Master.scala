import akka.actor.{Actor, ActorLogging, ActorRef, Terminated}

import collection.mutable

class Master(algorithm: AbstractAlgorithm, resultHandler: ActorRef) extends Actor with ActorLogging {
  import MasterWorkerProtocol._

  // Holds known workers and what they may be working on
  val workers = mutable.Map.empty[ActorRef, Option[AbstractInput]]
  // Holds the incoming list of work to be done as well
  // as the memory of who asked for it
  val workQ = mutable.Queue.empty[AbstractInput]

  var allWorkReceived: Boolean = false

  def checkIfAllWorkIsDone(): Unit = {
//    if (workers.size == 0 && workQ.size == 0 && allWorkReceived == true)
    if (workers.count(_._2.isDefined) == 0 && workQ.size == 0 && allWorkReceived)
      resultHandler ! AllWorkCompleted
  }

  // Notifies workers that there's work available, provided they're
  // not already working on something
  def notifyWorkers(): Unit = {
    if (workQ.nonEmpty) {
      workers.foreach {
        case (worker, m) if m.isEmpty => worker ! WorkIsReady
        case _ =>
      }
    }
  }

  def receive = {
    // Worker is alive. Add him to the list, watch him for
    // death, and let him know if there's work to be done
    case WorkerCreated(worker) =>
      log.info("Worker created: {}", worker)
      context.watch(worker)
      workers += (worker -> None)
      notifyWorkers()
      worker ! DoAlgorithm(algorithm)

    // A worker wants more work.  If we know about him, he's not
    // currently doing anything, and we've got something to do,
    // give it to him.
    case WorkerRequestsWork(worker) =>
      log.info("Worker requests work: {}", worker)
      if (workers.contains(worker)) {
        if (workQ.isEmpty) {
          worker ! NoWorkToBeDone
          checkIfAllWorkIsDone()
        } else if (workers(worker) == None) {
          val work = workQ.dequeue()
          workers += (worker -> Some(work))
          // Use the special form of 'tell' that lets us supply
          // the sender
          worker.tell(WorkToBeDone(work), resultHandler)
        }
      }

    // Worker has completed its work and we can clear it out
    case WorkIsDone(worker) =>
      if (!workers.contains(worker))
        log.error("Blurgh! {} said it's done work but we didn't know about him", worker)
      else
        workers += (worker -> None)

    // A worker died.  If he was doing anything then we need
    // to give it to someone else so we just add it back to the
    // master and let things progress as usual
    case Terminated(worker) =>
      if (workers.contains(worker) && workers(worker) != None) {
        log.error("Blurgh! {} died while processing {}", worker, workers(worker))
        // Send the work that it was doing back to ourselves for processing
        val work = workers(worker).get
        self ! work
      }
      workers -= worker

    case work: AbstractInput =>
      log.info("Received work: {}", work)
      workQ.enqueue(work)
      notifyWorkers()

    case AllWorkSent => allWorkReceived = true

    case badMessage =>
      log.error("ERROR: Master received invalid message {} from {}", badMessage, sender())
  }
}
