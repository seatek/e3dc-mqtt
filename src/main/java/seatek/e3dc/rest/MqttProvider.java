package seatek.e3dc.rest;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class MqttProvider {
	@Value("${mqtt.broker.url}")
	private  String brokerUrl;
	//private Connection connection;
	private MqttClient session;
	@Value("${mqtt.client.id:e3dc}")
	private String clientId;


	@PostConstruct
	public void init() throws MqttException  {
		  session = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
         MqttConnectOptions connOpts = new MqttConnectOptions();
         connOpts.setCleanSession(true);
         System.out.println("Connecting to broker: "+brokerUrl);
         session.connect(connOpts);
	
         
	}

	
	@Bean
	public MqttClient provideSession() {
		return session;
	}

	@PreDestroy
	public void destroy() {
		try {
			session.close();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
