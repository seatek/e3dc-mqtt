package seatek.e3dc.rest;

import java.io.UnsupportedEncodingException;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.apache.commons.text.CaseUtils;
import org.apache.logging.log4j.util.Strings;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import seatek.e3dc.rest.Measurement.Field;
import seatek.e3dc.rest.homie.HomieConfig;
import seatek.e3dc.rest.homie.HomieNode;
import seatek.e3dc.rest.homie.HomieProperties;
import seatek.e3dc.rest.homie.HomieNode.HomieNodeBuilder;

@Slf4j
@Component
public class PublishMqttHomie {
	private static final String HOMIE_E3DC = "homie/e3dc";

	private static final String propertyStates = "states";

	private static final String propertySolarPower = "solarPower";

	private static final String nodePv = "pv";

	private static final String propertyCharge = "charge";

	private static final String propertyPower = "power";

	private static final String nodeWallbox = "wallbox";

	private static final String nodeHouse = "house";

	private static final String nodeBattery = "battery";

	private static final String nodeLine = "line";

	String intType = "int";
	@Autowired
	MqttClient session;

	private Map<Measurement.Field, String> destination;

	private HomieProperties powerInputProperty;

	private HomieProperties batteryPowerProperty;

	private HomieProperties batteryChargeProperty;

	private HomieProperties linePowerProperty;

	private HomieProperties homePowerProperty;

	/*
	 * (non-Javadoc)
	 *
	 */
	@PostConstruct
	public void init() throws MqttException {
		this.destination = new EnumMap<Measurement.Field, String>(Measurement.Field.class);
		MqttClient client = session;

		powerInputProperty = HomieProperties.builder().name("Power Input").unit("W").datatype(intType).settable(false).build();

		batteryPowerProperty = HomieProperties.builder().name("Battery Power").unit("W").datatype(intType).settable(false)
				.build();

		batteryChargeProperty = HomieProperties.builder().name("Battery Charge").unit("%").datatype(intType).settable(false)
				.build();

	
		linePowerProperty = HomieProperties.builder().name("Line Power").unit("W").datatype(intType).settable(false).build();

		homePowerProperty = HomieProperties.builder().name("Home Power Consumption").unit("W").datatype(intType).settable(false)
				.build();
	
		
		//HomieProperties wallboxFlags = HomieProperties.builder().name("Wallbox states").datatype("enum[]").build();

		HomieNode pvNode = HomieNode.builder().name("Photo Voltaics").property(propertyPower, powerInputProperty).build();
		//@formatter:off
		HomieNode wallboxNode = buildWallboxNode();
		HomieConfig config = HomieConfig.builder()
				.name("E3DC Solar Power")
				.node(nodePv, pvNode)
				.node(nodeBattery, HomieNode.builder().name("Battery").property(propertyCharge, batteryChargeProperty)
						.property(propertyPower,batteryPowerProperty).build())
				.node(nodeHouse, HomieNode.builder().name("House").property(propertyPower, homePowerProperty).build())
				.node(nodeLine, HomieNode.builder().name("Line").property(propertyPower,linePowerProperty).build())
				.node(nodeWallbox, wallboxNode)
				
				.build();
		//@formatter:on

		publish(client, topicName("$homie"), "3.0", true);

		publish(client, topicName("$name"), config.getName(), true);
		publish(client, topicName("$state"), "ready", true);
		publish(client, topicName("$nodes"), Strings.join(config.getNodes().keySet(), ','), true);

		for (Entry<String, HomieNode> n : config.getNodes().entrySet()) {

			HomieNode node = n.getValue();
			publish(client, topicName(n.getKey(), "$name"), node.getName(), true);
			publish(client, topicName(n.getKey(), "$properties"), Strings.join(node.getProperties().keySet(), ','),
					true);

			for (Entry<String, HomieProperties> p : node.getProperties().entrySet()) {
				HomieProperties properties = p.getValue();
				publish(client, topicName(n.getKey(), p.getKey(), "$name"), properties.getName(), true);
				publish(client, topicName(n.getKey(), p.getKey(), "$datatype"), properties.getDatatype(), true);
				publish(client, topicName(n.getKey(), p.getKey(), "$settable"), String.valueOf(properties.isSettable()),
						true);
				properties.getUnit().ifPresent(unit->
					publish(client, topicName(n.getKey(), p.getKey(), "$unit"), unit,
						true));
			}
		}

	}

	private HomieNode buildWallboxNode() {
		HomieProperties wallboxPowerProperty = HomieProperties.builder().name("Wallbox Power Consumption").unit("W").datatype(intType).settable(false)
				.build();
		HomieProperties wallboxSolarPowerProperty = HomieProperties.builder().name("Wallbox Solar Power Consumption").unit("W").datatype(intType).settable(false)
				.build();
		 HomieNodeBuilder wallboxNodeBuilder = HomieNode.builder().name("Wallbox")
				.property(propertyPower,wallboxPowerProperty)
				.property(propertySolarPower,wallboxSolarPowerProperty);
		for(WallboxState s : WallboxState.values()) {
			wallboxNodeBuilder.property(toPropertyKey(s), HomieProperties.builder().datatype("boolean").settable(s.isSettable()).name(toName(s)).build());
		}
		HomieNode wallboxNode = wallboxNodeBuilder.build();
		return wallboxNode;
	}

	private String toName(WallboxState s) {
		switch(s) {
		case AVAILABLE:
			return "Wallbox is connected and running";
		case CHARGE_CANCELLED:
			return "Charging was cancelled";
		case CHARGE_ENABLED:
			return "Charging is enabled";
		case CHARGING:
			return "Is Charging";
		case MIXED_MODE:
			return "Mixed mode is active";
		case ONE_LEAD_ACTIVE:
			return "Charging via one lead is active";
		case RELAY_16A_3L_CLOSED:
			return "16A 3 lead relay is closed";
		case RELAY_32A_3L_CLOSED:
			return "32A 3 lead relay is closed";
		case RELAY_SCHUKO_CLOSED:
			return "Schuko relay is closed";
		case SCHUKO_SOCKET_ACTIVE:
			return "Schuko socket is active";
		case SCHUKO_SOCKET_LOCKED:
			return "Schuko socket is locked";
		case SCHUKO_SOCKET_PLUGGED_IN:
			return "Schuko socket is plugged in";
		case SOLAR_MODE:
			return "Solar mode is active";
		case THREE_LEAD_ACTIVE:
			return "Three lead mode is active";
		case TYPE_2_PLUG_LOCKED:
			return "Type 2 Plug is locked";
		case TYPE_2_PLUG_PLUGGED_IN:
			return "Type 2 Plug is plugged in";
		
		}
		throw new IllegalArgumentException("Unknown type "+s);
	}

	private String toPropertyKey(WallboxState s) {
		return CaseUtils.toCamelCase(s.name(), false,'_');
	}

	private String topicName(String... string) {
		StringBuilder sb = new StringBuilder(HOMIE_E3DC);
		for (String component : string) {
			sb.append("/").append(component);
		}
		return sb.toString();
	}

	private String toTopicName(Field t) {
		String node;
		String key;
		switch (t) {
		case BATTERY_LEVEL:
			key = propertyCharge;
			node = nodeBattery;
			break;
		case BATTERY_POWER:
			key = propertyPower;
			node = nodeBattery;
			break;
		case LINE_POWER:
			key = propertyPower;
			node = nodeLine;
			break;
		case POWER_CONSUMPTION:
			key = propertyPower;
			node = nodeHouse;
			break;
		case SOLAR_POWER:
			key = propertyPower;
			node = nodePv;
			break;
		case WALLBOX_POWER:
			key = propertyPower;
			node = nodeWallbox;
			break;
		case WALLBOX_SOLAR_POWER:
			key = propertySolarPower;
			node = nodeWallbox;
			break;
		
		default:
			throw new IllegalArgumentException("Unknown field " + t);

		}
		return String.format(HOMIE_E3DC+"/%s/%s", node, key);
	}

	private void publish(MqttClient client, String topicName, String value, boolean retain)
			 {
		MqttMessage message;
		try {
			message = new MqttMessage(String.valueOf(value).getBytes("UTF-8"));
			message.setQos(1);
			message.setRetained(retain);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		try {
			client.publish(topicName, message);
		} catch (MqttException e) {
			throw new RuntimeException(e);
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 */
	public void send(final Measurement.Field type, final Number value) throws MqttException {

		publish(session, toTopicName(type), value.toString(), false);
	}
	
	public void send(final EnumSet<WallboxState> type) throws MqttException {
		for(WallboxState s : WallboxState.values()) {
		publish(session, toTopicName(s), String.valueOf(type.contains(s)), false);
		}
	}

	private String toTopicName(WallboxState type) {
		
		return String.format(HOMIE_E3DC + "/wallbox/%s", toPropertyKey(type));
	}

}
