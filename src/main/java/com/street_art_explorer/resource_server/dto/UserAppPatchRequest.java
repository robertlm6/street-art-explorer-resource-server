package com.street_art_explorer.resource_server.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserAppPatchRequest {

	private String firstName;
	private String lastName;
	private Date birthDate;
}
