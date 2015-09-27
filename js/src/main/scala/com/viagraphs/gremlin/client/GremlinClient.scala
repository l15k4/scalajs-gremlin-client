package com.viagraphs.gremlin.client

import com.viagraphs.websocket._
import monifu.concurrent.Scheduler
import monifu.reactive.Observable
import upickle.{json, Invalid}
import upickle.legacy._
import scala.scalajs.js
import scala.scalajs.js.JSON._

class GremlinClient(url: Url)(implicit scheduler: Scheduler) extends RxWebSocketClient(url) {

  private def parse(s: String): js.Dynamic = {
    try js.JSON.parse(s) catch {
      case js.JavaScriptException(e: js.SyntaxError) => throw Invalid.Json(e.message, s)
    }
  }

  /**
   * @param msg serialized JSON message to be sent out
   * @return Observable stream of incoming messages
   *
   * @note that TypeClass is going to need an evidence of corresponding type T
   * @see https://github.com/lihaoyi/upickle
   */
  def send[T](message: js.Dynamic)(implicit r: Reader[ResponseMessage[T]]): Observable[Result[T]] = {
    val uuid = UUID()
    message.updateDynamic(uuid.name)(uuid.value)
    Observable.create[Result[T]] { observer =>
      sendAndReceive(OutMsg(stringify(message)))
        .map { inMsg =>
          parse(inMsg.text)
        }.collect {
          case dynamic if dynamic.selectDynamic("requestId").asInstanceOf[String] == uuid.value =>
            readJs[ResponseMessage[T]](json.readJs(dynamic))
        }.foreach { r =>
          r.status.code match {
            case OKCode => observer.onNext(r.result)
            case EndCode => observer.onComplete()
            case BadQuery => observer.onError(new Exception(r.status.message))
          }
        }
    }
  }
}

object GremlinClient {
  def apply(url: Url)(implicit scheduler: Scheduler): GremlinClient = new GremlinClient(url)
}
