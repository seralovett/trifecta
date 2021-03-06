package com.ldaniels528.trifecta.rest

import akka.actor.Actor
import com.ldaniels528.trifecta.io.json.JsonHelper
import com.ldaniels528.trifecta.rest.WebSocketActor._
import org.mashupbots.socko.events.WebSocketFrameEvent
import org.slf4j.LoggerFactory

/**
 * Web Socket Actor
 * @author Lawrence Daniels <lawrence.daniels@gmail.com>
 */
class WebSocketActor(embeddedWebServer: EmbeddedWebServer) extends Actor {
  private lazy val logger = LoggerFactory.getLogger(getClass)

  override def receive = {
    case event: WebSocketFrameEvent =>
      writeWebSocketResponse(event)
    case message =>
      logger.warn(s"received unknown message of type: $message")
      unhandled(message)
  }

  /**
   * Processing incoming web socket events
   */
  private def writeWebSocketResponse(event: WebSocketFrameEvent) {
    val webSocketId = event.webSocketId

    // was it a binary message?
    if (event.isBinary) {
      val message = event.readBinary()
      logger.info(s"[binary]: $message")
    }

    // was it a text-based message?
    else if (event.isText) {
      val message = event.readText()
      logger.info(s"[text]: $message")

      message match {
        case js if js.contains("startMessageSampling") =>
          val request = JsonHelper.transform[StartSamplingRequest](js)
          embeddedWebServer.startMessageSampling(webSocketId, request.topic, request.partitions)

        case js if js.contains("stopMessageSampling") =>
          val request = JsonHelper.transform[StopSamplingRequest](js)
          embeddedWebServer.stopMessageSampling(webSocketId, request.topic)
      }
    }

    else {
      logger.error(s"Unknown message type: $event")
    }
  }

}

/**
 * Web Socket Actor Singleton
 * @author Lawrence Daniels <lawrence.daniels@gmail.com>
 */
object WebSocketActor {

  case class StartSamplingRequest(action: String, topic: String, partitions: Seq[Long])

  case class StopSamplingRequest(action: String, topic: String)

}
