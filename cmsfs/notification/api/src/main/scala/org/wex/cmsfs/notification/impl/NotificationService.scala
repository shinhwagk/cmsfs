package org.wex.cmsfs.notification.impl

import akka.util.ByteString
import com.lightbend.lagom.scaladsl.api.deser.{MessageSerializer, StrictMessageSerializer}
import com.lightbend.lagom.scaladsl.api.deser.MessageSerializer.{NegotiatedDeserializer, NegotiatedSerializer}
import com.lightbend.lagom.scaladsl.api.transport.{MessageProtocol, NotAcceptable, SerializationException, UnsupportedMediaType}
import com.lightbend.lagom.scaladsl.api.{CircuitBreaker, Service, ServiceCall}

import scala.collection.immutable.Seq

object NotificationService {
  val SERVICE_NAME = "notification"
}

trait NotificationService extends Service {

  def pushNotificationItem: ServiceCall[String, String]

  override final def descriptor = {
    import NotificationService._
    import Service._
    named(SERVICE_NAME).withCalls(
      pathCall("/mns-web/services/rest/msgNotify", pushNotificationItem)
      (new ProtobufSerializer, MessageSerializer.StringMessageSerializer)
        .withCircuitBreaker(CircuitBreaker.identifiedBy("alarm-circuitbreaker"))
    )
  }
}

class PlainTextSerializer(val charset: String) extends NegotiatedSerializer[String, ByteString] {
  override val protocol = MessageProtocol(Some("application/json"), Some(charset))

  override def serialize(message: String) = ByteString.fromString(message, charset)
}

class ProtobufSerializer extends StrictMessageSerializer[String] {

  final private val serializer = {
    new NegotiatedSerializer[String, ByteString]() {
      override def protocol: MessageProtocol =
        MessageProtocol(Some("application/json"))

      def serialize(order: String): ByteString = {
        ByteString.fromString(order)
      }
    }
  }

  final private val deserializer = {
    new NegotiatedDeserializer[String, ByteString] {
      override def deserialize(bytes: ByteString) = bytes.toString()
    }
  }

  override def serializerForRequest: NegotiatedSerializer[String, ByteString] = serializer

  @scala.throws[UnsupportedMediaType]
  override def deserializer(protocol: MessageProtocol): NegotiatedDeserializer[String, ByteString] = deserializer

  @scala.throws[NotAcceptable]
  override def serializerForResponse(acceptedMessageProtocols: Seq[MessageProtocol]): NegotiatedSerializer[String, ByteString] = serializer
}
