package seatek.e3dc.rest;

public enum WallboxState {
	//@formatter:off
	AVAILABLE(0),
	SOLAR_MODE(1, true), MIXED_MODE(1, false),
	CHARGE_CANCELLED(2, true), CHARGE_ENABLED(2, false),
	CHARGING(3),
	TYPE_2_PLUG_LOCKED(4),
	TYPE_2_PLUG_PLUGGED_IN(5),
	SCHUKO_SOCKET_ACTIVE(6),
	SCHUKO_SOCKET_PLUGGED_IN(7),
	SCHUKO_SOCKET_LOCKED(8),
	RELAY_SCHUKO_CLOSED(9),
	RELAY_16A_3L_CLOSED(10),
	RELAY_32A_3L_CLOSED(11),
	ONE_LEAD_ACTIVE(12,true), THREE_LEAD_ACTIVE(12,false);
	private final int bit;
	private final boolean state;

	//@formatter:on
	WallboxState(int bit, boolean state) {
		this.bit = bit;
		this.state = state;
	}

	WallboxState(int state) {
		this(state,true);
	}
	
	public int getBit() {
		return bit;
	}

	public boolean isState() {
		return state;
	}

	public boolean matchesBit(int bit) {
		if(bit==0 && !state)return true;
		if(bit>0 && state)return true;
		return false;
	}

	boolean isSettable() {
		return false;
	}
}
