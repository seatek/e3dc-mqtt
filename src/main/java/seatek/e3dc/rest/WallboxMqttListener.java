package seatek.e3dc.rest;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WallboxMqttListener implements IMqttMessageListener {
	@Autowired
	ModbusRepository modbusRepository;

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		//modbusRepository.
	}

}
