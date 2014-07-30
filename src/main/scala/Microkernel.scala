import akka.actor.{Props, ActorSystem}
import akka.kernel.Bootable

class Microkernel extends Bootable {
  val system = ActorSystem("Microkernel")
//  val graph = ParseConfig.readGraph()
//  val masterLocation = ParseConfig.getMasterLocation()

  def startup = {
//    system.actorOf(Props(new GraphWorker(graph, masterLocation)), "worker")
  }

  def shutdown = {
    system.shutdown()
  }
}
