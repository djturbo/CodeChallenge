package com.codechallenge.models;

import org.springframework.data.domain.Sort.Direction;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ApiModel(value = "Transaction Search Request", description = "Optional params to search transactions")
public class SearchRequest {
	@JsonProperty("account_iban")
	@ApiModelProperty(value = "(Optional) Filter by IBAN", required = false)
	String iban;

	@ApiModelProperty(value = "(Optional) Sort by amount (ascending/descending), defaults to ASC", required = false, allowableValues = "ASC,DESC")
	Direction sort;
}
