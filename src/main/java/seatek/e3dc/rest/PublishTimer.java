package seatek.e3dc.rest;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import seatek.e3dc.rest.Measurement.Field;

@Component
@Slf4j
public class PublishTimer {
	@Autowired
	ModbusRepository repository;
	@Autowired
	private PublishMqtt mqttPublisher;

	@Scheduled(fixedRate = 10000)
	public void readAndPublish() throws MqttException {
		for(Field field : Field.values()) {
		int value = repository.getValue(field);
		mqttPublisher.send(field, value);
		log.debug("published field "+field+" with value "+value);
	}}
}
