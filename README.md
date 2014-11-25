scalajs-gremlin-client
==============

Allows your browser applications to communicate with [Gremlin Server](http://www.tinkerpop.com/docs/3.0.0-SNAPSHOT/#gremlin-server) via [reactive-websocket](https://github.com/viagraphs/reactive-websocket) thanks to [Monifu](http://monifu.org) and [uPickle](https://github.com/lihaoyi/upickle).

TESTING:

This expects gremlin-sever be already running, please see sbt settings :
```
> js/fastOptStage::test
```

This starts gremlin-server before running a test suite if you have `gremlinServerHome` set up correctly :
```
> js/test
```

USAGE:

```
// requests are built using js.Dynamic literals 
lit(
  EvalOp,
  DefaultProc,
  Args(
    lit(
      GremlinArg("g.v(1)"),
      BatchSizeArg(20),
      LangArg("gremlin-groovy")
    )
  )
)

// import all types or just those that you'll need
 import com.viagraphs.gremlin.client._

// connect to server
val client = GremlinClient(Url(WS, "localhost", 8182, Option.empty))

// sending a request requires specifying return type information to satisfy uPickle's Reader TypeClass. 
// You'll get strongly typed result in return...
val observable = client.send[Vector[Vertex]](query)

// do all kinds of Rx operations 
observable.map(...).filter(...).foreach(...) 

```

* Observable contains only responses corresponding to particular requestId, so you don't need to worry about that
* Please see GremlinClientSuite as a source of inspiration
* As gremlin language is turing complete, it is inevitable that you'll find new and new return types that are not supported. In that case you need to add corresponding uPickle Reader to Implicits

NOTE:

* It currently depends on scala-js 0.6.0-SNAPSHOT which means you'd need to compile all deps with it unless you're riding cutting edge as I do
* Just the basic operations are implemented and tested so far - but it is expected that adding new operations will mostly consist in adding a TypeClass evidence  
* Tests work on real operation systems only, some additional work required on MS Windows
