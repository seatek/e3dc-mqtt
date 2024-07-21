package seatek.e3dc.rest;

import java.util.EnumSet;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Measurement {
	private int solarleistung;
	private int netzleistung;
	private int hausleistung;
	private int batterieleistung;
	private int batteriekapazität;
	private int wallboxleistung;
	private int wallboxsolarleistung;
	private List<EnumSet<WallboxState>> wallboxStates;
	
	public enum Field {
		SOLAR_POWER(40068),LINE_POWER(40074),POWER_CONSUMPTION(40072),BATTERY_POWER(40070),BATTERY_LEVEL(40083),
		WALLBOX_POWER(40078), WALLBOX_SOLAR_POWER(40080)
		;

		private final int modbusField;
		public int getModbusField() {
			return modbusField;
		}
		private Field(int modbusField) {
			this.modbusField = modbusField;
		}
	}
}
