package seatek.e3dc.rest.homie;

import java.util.Map;

import org.apache.catalina.authenticator.jaspic.PersistentProviderRegistrations.Property;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class HomieNode {
	
	
private String name;
@Singular
private Map<String,HomieProperties> properties;

}
