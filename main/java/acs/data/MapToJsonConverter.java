package acs.data;
import java.util.Map;

import javax.persistence.AttributeConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


public class MapToJsonConverter implements AttributeConverter<Map<String, Object>, String>{
	private ObjectMapper jackson;
	
	public MapToJsonConverter() {
		this.jackson = new ObjectMapper();
	}
	
	@Override
	public String convertToDatabaseColumn(Map<String, Object> attributes) {
		// use jackson for marshalling the attributes 
		try {
			return this.jackson
					.writeValueAsString(attributes);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Map<String, Object> convertToEntityAttribute(String json) {
		// use jackson for unmarshalling the json
		try {
			return this.jackson
					.readValue(json, new TypeReference<Map<String, Object>>(){});
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

}

//TypeReference<Map<String, Object>> attributesMapRef = new TypeReference<Map<String, Object>>() {
//};
//return this.jackson.readValue(json, attributesMapRef);
