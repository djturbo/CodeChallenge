package com.codechallenge.serialization;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class MoneySerializer extends JsonSerializer<BigDecimal> {
	@Override
	public void serialize(final BigDecimal value, final JsonGenerator jgen, final SerializerProvider provider)
			throws IOException {
		jgen.writeNumber(value.setScale(2, BigDecimal.ROUND_FLOOR));
	}
}
