trait AbstractInput
trait AbstractResult

trait AbstractAlgorithm {
  def execute(graph: Graph, input: AbstractInput): Option[AbstractResult]
}

case class SingleVertexInput(u: Int) extends AbstractInput {
  override def toString = u.toString
}

case class LongResult(result: Long) extends AbstractResult {
  override def toString = result.toString
}

object DegreeAlgorithm extends AbstractAlgorithm {
  override def execute(graph: Graph, input: AbstractInput): Option[AbstractResult] = input match {
    case SingleVertexInput(u) => Some(graph.degree(u))
    case _ => None
  }
}
