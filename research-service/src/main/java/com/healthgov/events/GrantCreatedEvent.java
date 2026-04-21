package com.healthgov.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GrantCreatedEvent {

	private final Long grantId;
	private final Long projectId;

}