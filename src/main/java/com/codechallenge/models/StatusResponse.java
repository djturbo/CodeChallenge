package com.codechallenge.models;

import java.math.BigDecimal;

import com.codechallenge.models.enums.TransactionStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatusResponse {
	@NonNull
	String reference;
	TransactionStatus status;
	BigDecimal amount;
	BigDecimal fee;
}
