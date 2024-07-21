package seatek.e3dc.rest;

import java.util.ArrayList;
import static seatek.e3dc.rest.Measurement.Field.*;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.facade.AbstractModbusMaster;
import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster;
import com.ghgande.j2mod.modbus.procimg.Register;

import seatek.e3dc.rest.Measurement.Field;

@Component
public class ModbusRepository {
	AbstractModbusMaster master;

	@Value("${e3dc.ip}")
	private String address;

	@PostConstruct
	public void init() {
		this.master = new ModbusTCPMaster(this.address, 502);
		try {
			this.master.connect();
		} catch (Exception e) {
			throw new IllegalStateException("Could not start modbus client", e);
		}
	}

	@PreDestroy
	public void shutdown() {
		this.master.disconnect();
	}

	public int getHausleistung() {
		int ref = 40072;
		return readInt(ref, 1);
	}
	
	public void setChargeCancelled(boolean state) {
		writeCoil(40089, state);
	}

	private int readInt(final int ref, final int count) {
		Register[] mulReg;
		try {
			mulReg = this.master.readMultipleRegisters(ref - 1, count);
		} catch (ModbusException e) {
			throw new RuntimeException(e);
		}

		return mulReg[0].getValue();
	}
	
	private void writeCoil(final int ref, boolean state) {
		try {
			master.writeCoil(ref, state);
		} catch (ModbusException e) {
			throw new RuntimeException(e);
		}

	}

	public int getSolarleistung() {
		return readInt(40068, 1);
	}

	public int getBatterieleistung() {
		return readInt(40070, 1);
	}

	public int getBatteriekapazität() {
		return readInt(40083, 1);
	}
	
	public int getWallboxleistung() {
		return readInt(WALLBOX_POWER.getModbusField(), 1);
	}
	
	public int getWallboxsolarleistung() {
		return readInt(WALLBOX_SOLAR_POWER.getModbusField(), 1);
	}

	public int getNetzleistung() {
		int possiblyNegative = readInt(40074, 1);
		int adjusted = convertNegativeShort(possiblyNegative);
		return adjusted;
	}
	public List<EnumSet<WallboxState>> getWallboxStates(){
		List<EnumSet<WallboxState>> list = new ArrayList<>();
		for(int i=40088;i<40096;i++) {
			
			EnumSet<WallboxState> states = getWallboxStates(i);
			if(states.contains(WallboxState.AVAILABLE))
			list.add(states);
			
		}
		return list;
	}
	

	private EnumSet<WallboxState> getWallboxStates(int ref) {
		int mask = readInt(ref,1);
		
		List<WallboxState> list = new ArrayList<WallboxState>();
		for (WallboxState value : WallboxState.values()) {
		  if ( value.matchesBit((mask & (1 << value.getBit())))) {
		    list.add(value);
		  } 
		}
		return EnumSet.copyOf(list);
	}

	private int convertNegativeShort(int possiblyNegative) {
		if ((possiblyNegative & 0x8000) > 0) {
			possiblyNegative ^= 0xFFFF;
			possiblyNegative++;
			possiblyNegative *= -1;

		}
		return possiblyNegative;
	}

	public Measurement getMeasurement() {
		Measurement m = new Measurement();
		BeanUtils.copyProperties(this, m);
		return m;
	}

	public int getValue(Field powerConsumption) {
		int possiblyNegative = readInt(powerConsumption.getModbusField(), 1);
		int adjusted = possiblyNegative;
		if(EnumSet.of(Field.LINE_POWER,Field.BATTERY_POWER).contains(powerConsumption))
			adjusted = convertNegativeShort(possiblyNegative);
		return adjusted;
	}
}
