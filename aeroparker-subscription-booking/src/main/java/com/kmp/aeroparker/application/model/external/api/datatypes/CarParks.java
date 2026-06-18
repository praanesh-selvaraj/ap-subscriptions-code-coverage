package com.kmp.aeroparker.application.model.external.api.datatypes;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarParks
{
	@JsonProperty("CarPark")
	private ArrayList<CarPark> carParks = new ArrayList<>();
}
