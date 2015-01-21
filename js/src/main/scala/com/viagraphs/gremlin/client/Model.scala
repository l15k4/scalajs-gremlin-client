package com.viagraphs.gremlin.client

import scala.scalajs.js.Dynamic

/* REQUEST */

sealed trait Named { def name: String }
sealed trait Valued[T] { def value: T }

sealed trait Proc extends Named with Valued[String] {
  val name = "processor"
}
case object DefaultProc extends Proc { val value = "" }
case object ControlProc extends Proc { val value = "control" }
case object SessionProc extends Proc { val value = "session" }

sealed trait Op extends Named with Valued[String] {
  val name = "op"
}
case object ShowOp extends Op { val value = "show" }
case object EvalOp extends Op { val value = "eval" }
case object TraverseOp extends Op { val value = "traverse" }
case object ImportOp extends Op { val value = "import" }
case object InvalidOp extends Op { val value = "invalid" }
case object ResetOp extends Op { val value = "reset" }
case object UseOp extends Op { val value = "use" }
case object VersionOp extends Op { val value = "version" }

case class Args(value: Dynamic) extends Named with Valued[Dynamic] {
  val name = "args"
}

sealed trait Arg extends Named
case class BindingsArg(value: Dynamic) extends Arg with Valued[Dynamic] { val name = "bindings" }
case class CoordinatesArg(value: String) extends Arg with Valued[String] { val name = "coordinates" }
case class GraphNameArg(value: String) extends Arg with Valued[String] { val name = "graphName" }
case class GremlinArg(value: String) extends Arg with Valued[String] { val name = "gremlin" }
case class ImportsArg(value: String) extends Arg with Valued[String] { val name = "imports" }
case class InfoTypeArg(value: String) extends Arg with Valued[String] { val name = "infoType" }
case class LangArg(value: String) extends Arg with Valued[String] { val name = "language" }
case class BatchSizeArg(value: Int) extends Arg with Valued[Int] { val name = "batchSize" }
case class SessionArg(value: String) extends Arg with Valued[String] { val name = "session" }
case class GroupArg(value: String) extends Arg with Valued[String] { val name = "group" }
case class ArtifactArg(value: String) extends Arg with Valued[String] { val name = "artifact" }
case class VersionArg(value: String) extends Arg with Valued[String] { val name = "version" }


/* RESPONSE */

case class ResponseMessage[T](requestId: UUID, status: Status, result: Result[T])

case class Status(code: ResultCode, message: String, attributes: Map[String, Any])

sealed trait ResultCode extends Valued[Int]
case object OKCode extends ResultCode { val value = 200 }
case object EndCode extends ResultCode { val value = 299 }
case object BadQuery extends ResultCode { val value = 597 }

case class Result[T](data: T, meta: Map[String, Any])

trait Element[P] {
  def id: Int
  def label: String
  def properties: Map[String, P]
}

case class Vertex(
                   id: Int,
                   label: String,
                   `type`: String,
                   properties: Map[String, IndexedSeq[Map[String, Any]]]
                   ) extends Element[IndexedSeq[Map[String, Any]]]

case class Edge(
                 id: Int,
                 label: String,
                 `type`: String,
                 inV: Int,
                 outV: Int,
                 inVLabel: String,
                 outVLabel: String,
                 properties: Map[String, Any]
                 ) extends Element[Any]


/* REQUEST & RESPONSE */

/** There is actually no pure scala implementation of uuid that might be compiled to javascript */
class UUID(val uuid: String) extends Named with Valued[String] with Product with Serializable {
  val name = "requestId"
  val value = uuid

  override def equals (o: Any): Boolean = o match {
    case UUID(x: String) => uuid == x
    case _ => false
  }

  override def toString = s"${getClass.getSimpleName}${scala.runtime.ScalaRunTime._toString(this)}"

  def productArity = 2

  def productElement(n: Int): Any = n match {
    case 0 => this.name
    case 1 => this.value
    case _ => throw new IndexOutOfBoundsException(n.toString)
  }

  def canEqual(that: Any) = that.isInstanceOf[UUID]

}

object UUID {

  def unapply(me: UUID): Option[String] = Option(me.uuid)

  /** provide your own secure uuid implementation */
  def apply(uuid: String) = new UUID(uuid)

  /** Generate UUID using the most simple and straightforward dummy implementation */
  def apply(): UUID = {
    def generate(source: String, groupSizes: List[Int], sep: String): String = {
      val r = new scala.util.Random
      def random(n: Int): String = Stream.continually(r.nextInt(source.size)).map(source).take(n).mkString
      groupSizes.map(random).mkString(sep)
    }
    UUID(generate("abcdef0123456789", List(8,4,4,4,12), "-"))
  }

}