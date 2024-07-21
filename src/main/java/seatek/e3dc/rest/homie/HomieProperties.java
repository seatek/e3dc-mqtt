package seatek.e3dc.rest.homie;

import java.util.Optional;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomieProperties {
	private String name;
	private String datatype;
	private boolean settable;
	private String unit;
	
	public Optional<String> getUnit() {
		return Optional.ofNullable(unit);

	}
}
