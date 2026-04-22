package fhnw.emoba.thatsapp.data

import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.UUID
import kotlin.text.Charsets.UTF_8

/**
 * ACHTUNG: Das ist nur eine erste Konfiguration eines Mqtt-Brokers.
 *
 * Dient vor allem dazu mit den verschiedenen Parametern experimentieren zu können
 *
 * siehe die Doku:
 * https://hivemq.github.io/hivemq-mqtt-client/
 * https://github.com/hivemq/hivemq-mqtt-client
 *
 * Ein generischer Mqtt-Client (gut, um Messages zu kontrollieren)
 * http://www.hivemq.com/demos/websocket-client/
 *
 */
class MqttConnector (mqttBroker: String,
                     val qos: MqttQos = MqttQos.EXACTLY_ONCE
){
    private val username: String = "User123"
    private val password: String = "User12345678"

    private val client = Mqtt5Client.builder()
        .serverHost(mqttBroker)
        .serverPort(8883)
        .sslWithDefaultConfig()
        .buildAsync()

    fun connect(
        onConnectionSucceed: () -> Unit = {},
        onConnectionFailed: () -> Unit = {}
    ) {
        println("Versuche, Verbindung zum Broker"+ client.toString()+ "aufzubauen...")
        client.connectWith()
            .simpleAuth()
            .username(username)
            .password(UTF_8.encode(password))
            .applySimpleAuth()
            .cleanStart(true)
            .keepAlive(30)
            .send()
            .whenComplete { _, throwable ->
                if (throwable != null) {
                    onConnectionFailed()
                    println("Verbindung fehlgeschlagen: ${throwable.localizedMessage}")
                } else {
                    onConnectionSucceed()
                    println("Verbindung konnte erfolgreich aufgebaut werden!")
                }
            }
    }

    fun subscribe(topic:        String,
                  onNewMessage: (JSONObject) -> Unit,
                  onError:      (Exception, String) -> Unit = { e, _ -> e.printStackTrace() }){
        client.subscribeWith()
            .topicFilter(topic)
            .qos(qos)
            .noLocal(true)
            .callback {
                try {
                    onNewMessage(it.payloadAsJSONObject())
                }
                catch (e: Exception){
                    onError(e, it.payloadAsString())
                }
            }
            .send()
    }

    fun publish(topic:       String,
                retain: Boolean,
                message:     Message,
                onPublished: () -> Unit = {},
                onError:     () -> Unit = {}) {
        client.publishWith()
            .topic(topic)
            .payload(message.asPayload())
            .qos(qos)
            .retain(retain)
            .messageExpiryInterval(120)
            .send()
            .whenComplete {_, throwable ->
                if(throwable != null){
                    println("Message konnte nicht gesendet weden ${throwable.localizedMessage}")
                    onError()
                }
                else {
                    println("Message wurde erfolgreich gesendet")
                    onPublished()
                }
             }
    }

    fun disconnect() {
        client.disconnectWith()
            .sessionExpiryInterval(0)
            .send()
    }
}
// praktische Extension Functions
private fun String.asPayload() : ByteArray = toByteArray(StandardCharsets.UTF_8)
private fun Mqtt5Publish.payloadAsJSONObject() : JSONObject = JSONObject(payloadAsString())
private fun Mqtt5Publish.payloadAsString() : String = String(payloadAsBytes, StandardCharsets.UTF_8)
private fun Message.asPayload() : ByteArray = asJsonString().asPayload()