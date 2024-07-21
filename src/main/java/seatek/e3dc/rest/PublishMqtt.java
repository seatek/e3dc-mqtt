package seatek.e3dc.rest;

import java.io.UnsupportedEncodingException;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import seatek.e3dc.rest.Measurement.Field;

@Slf4j
@Component
public class PublishMqtt {
	@Autowired
	MqttClient session;

	private Map<Measurement.Field, String> destination;

	/*
	 * (non-Javadoc)
	 *
	 */
	@PostConstruct
	public void init() throws MqttException {
		this.destination = new EnumMap<Measurement.Field, String>(Measurement.Field.class);
		for (Measurement.Field t : Measurement.Field.values()) {
			String topicName = toTopicName(t);
			log.info("Creating topic " + topicName);
			this.destination.put(t, topicName);
		}

	}

	private String toTopicName(Field t) {

		return String.format("home:pv:%s", t.name().toLowerCase(Locale.ENGLISH));
	}

	/*
	 * (non-Javadoc)
	 *
	 */
	public void send(final Measurement.Field type, final int value) throws MqttException {

		MqttMessage message;
		try {
			message = new MqttMessage(String.valueOf(value).getBytes("UTF-8"));
			message.setQos(1);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		try {
			session.publish(this.destination.get(type), message);
		} catch (MqttException e) {
			session.reconnect();
		} 
	}

}
