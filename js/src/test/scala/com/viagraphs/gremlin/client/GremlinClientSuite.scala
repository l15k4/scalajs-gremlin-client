package com.viagraphs.gremlin.client

import com.viagraphs.websocket._
import monifu.concurrent.Scheduler
import utest._

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
import scala.scalajs.js.Dynamic.{literal => lit}

object GremlinClientSuite extends TestSuites with Implicits {
  implicit val scheduler = Scheduler()

  def q(query: String, batchSize: Int = 10) =
    lit(
      EvalOp,
      DefaultProc,
      Args(
        lit(
          GremlinArg(query),
          BatchSizeArg(batchSize),
          LangArg("gremlin-groovy")
        )
      )
    )

  // org.scalajs.dom.window.setTimeout(() => System.exit(0), 5000)  // uncomment when troubleshooting problems

  val client = GremlinClient(Url(WS, "localhost", 8182, Option.empty))

  val generalUseCases = TestSuite {

    "should get a vertex with properties loaded" - {
      client.send[Vector[Vertex]](q("g.v(1)")).asFuture.flatMap {
        case Some(result) =>
          val expected = Result(Vector(tinkerGraphVertices(0)), Map[String, Any]())
          assert(expected == result)
          Future.successful(result)
        case None =>
          Future.failed(new Exception("Unable to resolve server response !"))
      }
    }

    "should get all vertices" - {
      client.send[Vector[Vertex]](q("g.V()")).asFuture.flatMap {
        case Some(result) =>
          val expected = Result(tinkerGraphVertices, Map[String, Any]())
          assert(expected == result)
          Future.successful(result)
        case None =>
          Future.failed(new Exception("Unable to resolve server response !"))
      }
    }

    "should get all vertices in 3 batches" - {
      client.send[Vector[Vertex]](q("g.V()", 2)).buffer(3).asFuture.flatMap {
        case Some(results) =>
          assert(results.size == 3)
          val expected = results.flatMap(_.data)
          assert(expected == tinkerGraphVertices)
          Future.successful(results)
        case None =>
          Future.failed(new Exception("Unable to resolve server response !"))
      }
    }

    "should get an edge with properties loaded" - {
      client.send[Vector[Edge]](q("g.e(7)")).asFuture.flatMap {
        case Some(result) =>
          val expected = Result(Vector(tinkerGraphEdges(0)), Map[String, Any]())
          assert(expected == result)
          Future.successful(result)
        case None =>
          Future.failed(new Exception("Unable to resolve server response !"))
      }
    }

    "should get all edges" - {
      client.send[Vector[Edge]](q("g.E()")).asFuture.flatMap {
        case Some(result) =>
          val expected = Result(tinkerGraphEdges, Map[String, Any]())
          assert(expected == result)
          Future.successful(result)
        case None =>
          Future.failed(new Exception("Unable to resolve server response !"))
      }
    }

    "should get outgoing vertices" - {
      client.send[Vector[Vertex]](q("g.v(6).out('created')")).asFuture.flatMap {
        case Some(result) =>
          val expected = Result(Vector(tinkerGraphVertices(2)), Map[String, Any]())
          assert(expected == result)
          Future.successful(result)
        case None =>
          Future.failed(new Exception("Unable to resolve server response !"))
      }
    }

    "should get a vertex property" - {
      client.send[Vector[Map[String, Any]]](q("g.v(1).properties('name')")).asFuture.flatMap {
        case Some(result) =>
          val expected =
            Result(
              Vector(
                Map("id" -> 0, "label" -> "name", "value" -> "marko", "properties" -> ArrayBuffer())
              ),
              Map[String, Any]()
            )
          assert(expected == result)
          Future.successful(result)
        case None =>
          Future.failed(new Exception("Unable to resolve server response !"))
      }
    }

    "should get an edge property" - {
      client.send[Vector[Map[String, Double]]](q("g.e(7).properties('weight')")).asFuture.flatMap {
        case Some(result) =>
          val expected = Result(Vector(Map("value" -> 0.5)), Map())
          assert(expected == result)
          Future.successful(result)
        case None =>
          Future.failed(new Exception("Unable to resolve server response !"))
      }
    }
  }
}
