package com.tsurip.mqttserver

import android.content.Context
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*


class MQTTConfigure(val context: Context,val Parametros: MQTTConnectionParams){


    val client = MqttAndroidClient(context,Parametros.Host, MqttClient.generateClientId())

    fun connect(v: TextView){
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isAutomaticReconnect = true
        mqttConnectOptions.isCleanSession = false
        //mqttConnectOptions.setUserName(this.connectionParams.username)
        //mqttConnectOptions.setPassword(this.connectionParams.password.toCharArray())
        try
        {
            val params = this.Parametros
            client.connect(mqttConnectOptions, null, object: IMqttActionListener {
                override fun onSuccess(asyncActionToken:IMqttToken) {
                    val disconnectedBufferOptions = DisconnectedBufferOptions()
                    disconnectedBufferOptions.isBufferEnabled = true
                    disconnectedBufferOptions.bufferSize = 100
                    disconnectedBufferOptions.isPersistBuffer = false
                    disconnectedBufferOptions.isDeleteOldestMessages = false
                    client.setBufferOpts(disconnectedBufferOptions)
                    subscribe(params.topic)
                    v.setBackgroundColor(ContextCompat.getColor(context,R.color.Verde))
                    v.text = "Conectado"
                }
                override fun onFailure(asyncActionToken:IMqttToken, exception:Throwable) {
                    Toast.makeText(context,"Falhou em conectar em: " + Parametros.Host + exception.toString(), Toast.LENGTH_SHORT).show()
                }
            })
        }
        catch (ex:MqttException) {
            ex.printStackTrace()
        }
    }
    //Publisher
    fun publish(message:String){
        try
        {
            var msg = "Android diz:" + message
            client.publish(this.Parametros.topic,msg.toByteArray(),0,false,null,object :IMqttActionListener{
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.w("Mqtt", "Sucesso em Publish!")
                    Toast.makeText(context,"Foi publicado ao Topic", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.w("Mqtt", "Publish Failed!")
                    Toast.makeText(context,"Falhou em publicar ao Topic", Toast.LENGTH_SHORT).show()
                }

            })
        }
        catch (ex:MqttException) {
            Toast.makeText(context,"Publish Exception", Toast.LENGTH_SHORT).show()
            ex.printStackTrace()
        }
    }

    //Subscribe
    fun subscribe(topic: String){
        try
        {
            client.subscribe(topic, 0, null, object:IMqttActionListener {
                override fun onSuccess(asyncActionToken:IMqttToken) {
                    Toast.makeText(context,"Se increveu no Topic", Toast.LENGTH_SHORT).show()
                }
                override fun onFailure(asyncActionToken:IMqttToken, exception:Throwable) {
                    Toast.makeText(context,"Falhou em se increver no Topic", Toast.LENGTH_SHORT).show()
                }
            })
        }
        catch (ex:MqttException) {
            Toast.makeText(context,"Exception Subscribing", Toast.LENGTH_SHORT).show()
            ex.printStackTrace()
        }
    }
    //Receiver
    fun receiveMessages(messageActiviter: TextView?) {
        client.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable) {}
            override fun messageArrived(topic: String, message: MqttMessage) {
                try {
                    var newText = """
                    --->${message.toString()}
                    """
                    //var newText = text.toString() + "\n" + message +  "\n"
                    messageActiviter?.text = newText
                } catch (e: Exception) {
                }
            }
            override fun deliveryComplete(token: IMqttDeliveryToken) {}
        })
    }
}

class MQTTConnectionParams (val clientId: String?,val Host: String, val topic: String, val username: String?, val password: String?){}
