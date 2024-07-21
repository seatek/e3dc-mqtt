package seatek.e3dc.rest.homie;

import java.util.Map;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class HomieConfig {
	
	private String name;
	@Singular("node")
	private Map<String,HomieNode> nodes;

	
}
