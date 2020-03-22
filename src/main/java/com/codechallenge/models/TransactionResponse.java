package com.codechallenge.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@ApiModel(value = "Transaction Response", description = "Response from a created transaction")
public class TransactionResponse {
	@ApiModelProperty(value = "The transaction unique reference number")
	String reference;
}
