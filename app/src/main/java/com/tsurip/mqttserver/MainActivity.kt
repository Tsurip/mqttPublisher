package com.tsurip.mqttserver

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.eclipse.paho.android.service.MqttAndroidClient

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var client: MQTTConfigure

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnConectar.setOnClickListener(this)
    }

    override fun onClick(v : View) {
        when(v.id) {
            btnConectar.id -> {
                try {
                    connect(statusBar)
                    client.receiveMessages(edMensagemReceiver)
                }catch(e: Exception){
                    Toast.makeText(this, "Deu não menó", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    fun connect(v: TextView){

        if (!(edHostServer.text.isNullOrEmpty() && edTopicPublish.text.isNullOrEmpty())) {
            var host = "tcp://" + edHostServer.text.toString() + ":1883"
            var topic = edTopicPublish.text.toString()
            var configParams = MQTTConnectionParams(null, host, topic, null, null)

            client = MQTTConfigure(this, configParams)
            client.connect(v)
        }else{
            Toast.makeText(this, "É nn deu nn em", Toast.LENGTH_SHORT).show()
        }

    }
}

