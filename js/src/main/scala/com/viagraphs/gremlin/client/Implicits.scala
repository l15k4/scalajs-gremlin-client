package com.viagraphs.gremlin.client

import upickle.{Js, Invalid}
import upickle.legacy._
import upickle.legacy.Aliases._
import upickle.Js.Obj
import scala.language.implicitConversions

import scala.scalajs.js

object Implicits {
  def validate[T](name: String)(pf: PartialFunction[Js.Value, T]) = Internal.validate(name)(pf)
  /** for constructing messages from dynamic literals without having to deconstructing objects */
  implicit def NamedStringValueToTuple[N <: Named with Valued[String]](x: N): (String, js.Any) = (x.name, x.value)
  implicit def NamedIntValueToTuple[N <: Named with Valued[Int]](x: N): (String, js.Any) = (x.name, x.value)
  implicit def NamedDynamicValueToTuple[N <: Named with Valued[js.Dynamic]](x: N): (String, js.Dynamic) = (x.name, x.value)

  implicit def CodeReader: R[ResultCode] = R[ResultCode] (
    validate("Undefined value of ResultCode !") {
      case x: Js.Num => x.value match {
        case 200 => OKCode
        case 299 => EndCode
        case 597 => EndCode
      }
    }
  )

  implicit def UUIDReader: R[UUID] = R[UUID] (
    validate("Undefined value of UUID !") {
      case x: Js.Str => UUID(x.value)
    }
  )

  /** upickle doesn't seem to handle https://github.com/lihaoyi/upickle/issues/46 */
  implicit def MapR[V: R]: R[Map[String, V]] = R[Map[String, V]](
    validate("Unable to read Map[String, V]"){
      case Js.Obj(arr@_*) => arr.map { case (k,v) => (k, readJs[V](v))}.toMap
    }
  )

  /** reader for Response properties, upickle doesn't support deserialization of Map/List with Any type */
  implicit def MapReader: R[Map[String, Any]] = R[Map[String, Any]] (
    validate("Unable to read Map[String, Any]") {
      case x: Js.Obj => x.value.map( t => (t._1, t._2.value)).toMap
    }
  )

  implicit def VerticesReader: R[IndexedSeq[Vertex]] = R[IndexedSeq[Vertex]] (
    validate("Unable to read Result[Vector[Vertex]]") {
      case Js.Arr(x@_*) => x.map(readJs[Vertex]).toVector
      case Js.Null => Array[Vertex]()
    }
  )

  implicit def EdgesReader: R[IndexedSeq[Edge]] = R[IndexedSeq[Edge]] (
    validate("Unable to read Result[Edge]") {
      case Js.Arr(x@_*) => x.map(readJs[Edge]).toVector
      case Js.Null => Array[Edge]() // this might have dedicated Reader for Nulls - dunno how yet
    }
  )

  implicit def EdgeReader: R[Edge] = R[Edge] {
    readerCaseFunction[Edge](
      Array("id", "label", "type", "inV", "outV", "inVLabel", "outVLabel", "properties"),
      Array(null, null, null, Obj(Seq[(java.lang.String, Js.Value)]():_*)),
      {case x: Js.Arr => Edge.tupled(readJs[(Int, String, String, Int, Int, String, String, Map[String, Any])](x))}
    )
  }

  implicit def VertexReader: R[Vertex] = R[Vertex]{
    readerCaseFunction[Vertex](
      Array("id", "label", "type", "properties"),
      Array(null, null, null, Obj(Seq[(java.lang.String, Js.Value)]():_*)),
      {case x: Js.Arr => Vertex.tupled(readJs[(Int, String, String, Map[String, IndexedSeq[Map[String, Any]]])](x))}
    )
  }

  private[this] def readerCaseFunction[T](names: Array[String], defaults: Array[Js.Value], read: PartialFunction[Js.Value, T]): PartialFunction[Js.Value, T] = {
    validate("Unable to read element with names : " + names) {
      case x: Js.Obj =>
        read(mapToArray(x, names, defaults))
    }
  }

  private[this] def mapToArray(o: Js.Obj, names: Array[String], defaults: Array[Js.Value]) = {
    val accumulated = new Array[Js.Value](names.length)
    val map = o.value.toMap
    var i = 0
    val l = names.length
    while(i < l){
      if (map.contains(names(i))) accumulated(i) = map(names(i))
      else if (defaults(i) != null) accumulated(i) = defaults(i)
      else throw new Invalid.Data(o, "Key Missing: " + names(i))
      i += 1
    }
    Js.Arr(accumulated:_*)
  }

}
