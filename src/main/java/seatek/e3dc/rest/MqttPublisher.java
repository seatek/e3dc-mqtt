package seatek.e3dc.rest;

import java.util.EnumSet;

import org.eclipse.paho.client.mqttv3.MqttException;

public interface MqttPublisher {

	void send(final EnumSet<WallboxState> type) ;

	void send(final Measurement.Field type, final int value) ;

}
