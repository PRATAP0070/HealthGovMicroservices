package com.healthgov.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProgramCreatedEvent {
	private Long programId;
	private String title;
}
