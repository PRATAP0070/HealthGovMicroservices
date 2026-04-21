package com.healthgov.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectCreatedEvent {
	private Long projectId;
	private String title;
}
