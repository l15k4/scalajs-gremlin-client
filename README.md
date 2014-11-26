scalajs-gremlin-client
==============

Allows your browser applications to communicate with [Gremlin Server](1) via [reactive-websocket](2) thanks to [Monifu](3) and [uPickle](4).

**Mainly it approaches** : 
* gremlin's turing completeness by letting its users deal with it themselves in their application, thanks to Scala's Type Classes and excellent serialization library [uPickle](4). Any new type of response might be added by user himself in his application by adding an evidence for a Reader type class like this. Found a new kind of response/request? Add it to your app and submit a feature request :-)
* gremlin server response streaming which is handled the Rx way thanks to the awesome [Monifu](3) project


Testing
-------

This expects gremlin-sever be already running, please see sbt settings :
```scala
> js/fastOptStage::test
```

This starts gremlin-server before running a test suite if you have `gremlinServerHome` set up correctly :
```scala
> js/test
```

Usage
-----

```scala
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

NOTE :
----

* It currently depends on scala-js 0.6.0-SNAPSHOT which means you'd need to compile all deps with it unless you're riding cutting edge as I do
* Just the basic operations are implemented and tested so far - but it is expected that adding new operations will mostly consist in adding a TypeClass evidence  
* Tests work on real operation systems only, some additional work required on MS Windows


  [1]: http://www.tinkerpop.com/docs/3.0.0-SNAPSHOT/#gremlin-server
  [2]: https://github.com/viagraphs/reactive-websocket
  [3]: https://github.com/monifu
  [4]: https://github.com/lihaoyi/upickle