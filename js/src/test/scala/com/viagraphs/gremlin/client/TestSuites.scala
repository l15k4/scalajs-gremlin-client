package com.viagraphs.gremlin.client

import utest.framework.TestSuite

import scala.collection.mutable.ArrayBuffer

abstract class TestSuites extends TestSuite {
  import Implicits._

  val tinkerGraphVertices =
    Vector(
      Vertex(
        1, "vertex", "vertex",
        Map[String, IndexedSeq[Map[String, Any]]](
          "name"  -> Vector(Map("id" -> 0, "label" -> "name", "value" -> "marko", "properties" -> ArrayBuffer())),
          "age"   -> Vector(Map("id" -> 1, "label" -> "age", "value"  -> 29,      "properties" -> ArrayBuffer()))
        )
      ),
      Vertex(
        2, "vertex", "vertex",
        Map[String, IndexedSeq[Map[String, Any]]](
          "name"  -> Vector(Map("id" -> 2, "label" -> "name", "value" -> "vadas", "properties" -> ArrayBuffer())),
          "age"   -> Vector(Map("id" -> 3, "label" -> "age",  "value" -> 27,      "properties" -> ArrayBuffer()))
        )
      ),
      Vertex(
        3, "vertex", "vertex",
        Map[String, IndexedSeq[Map[String, Any]]](
          "name"  -> Vector(Map("id" -> 4, "label" -> "name", "value" -> "lop", "properties" -> ArrayBuffer())),
          "lang"   -> Vector(Map("id" -> 5, "label" -> "lang", "value" -> "java","properties" -> ArrayBuffer()))
        )
      ),
      Vertex(
        4, "vertex", "vertex",
        Map[String, IndexedSeq[Map[String, Any]]](
          "name"  -> Vector(Map("id" -> 6, "label" -> "name", "value" -> "josh", "properties" -> ArrayBuffer())),
          "age"   -> Vector(Map("id" -> 7, "label" -> "age",  "value" -> 32,     "properties" -> ArrayBuffer()))
        )
      ),
      Vertex(
        5, "vertex", "vertex",
        Map[String, IndexedSeq[Map[String, Any]]](
          "name"  -> Vector(Map("id" -> 8, "label" -> "name", "value" -> "ripple", "properties" -> ArrayBuffer())),
          "lang"   -> Vector(Map("id" -> 9, "label" -> "lang", "value" -> "java",   "properties" -> ArrayBuffer()))
        )
      ),
      Vertex(
        6, "vertex", "vertex",
        Map[String, IndexedSeq[Map[String, Any]]](
          "name"  -> Vector(Map("id" -> 10, "label" -> "name", "value" -> "peter", "properties" -> ArrayBuffer())),
          "age"   -> Vector(Map("id" -> 11, "label" -> "age",  "value" -> 35,      "properties" -> ArrayBuffer()))
        )
      )
    )

  val tinkerGraphEdges =
    Vector(
      Edge(
        7, "knows", "edge",
        2, 1, "vertex", "vertex",
        Map[String, Any]("weight" -> 0.5)
      ),
      Edge(
        8, "knows", "edge",
        4, 1, "vertex", "vertex",
        Map[String, Any]("weight" -> 1)
      ),
      Edge(
        9, "created", "edge",
        3, 1, "vertex", "vertex",
        Map[String, Any]("weight" -> 0.4)
      ),
      Edge(
        10, "created", "edge",
        5, 4, "vertex", "vertex",
        Map[String, Any]("weight" -> 1)
      ),
      Edge(
        11, "created", "edge",
        3, 4, "vertex", "vertex",
        Map[String, Any]("weight" -> 0.4)
      ),
      Edge(
        12, "created", "edge",
        3, 6, "vertex", "vertex",
        Map[String, Any]("weight" -> 0.2)
      )
    )
}
