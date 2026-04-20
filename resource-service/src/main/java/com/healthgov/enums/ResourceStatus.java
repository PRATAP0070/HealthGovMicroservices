package com.healthgov.enums;

/*
 * Defines the lifecycle state of a resource. Status meanings may vary based on
 * ResourceType (FUNDS vs LAB/EQUIPMENT).
 */
public enum ResourceStatus {

	/* Waiting for approval or budget availability (mainly for FUNDS). */
	PENDING,

	/* Resource is currently in use (LAB / EQUIPMENT). */
	ACTIVE,

	/* Resource lifecycle is finished and is immutable (audit state). */
	COMPLETED,

	/* Resource is temporarily unavailable or paused. */
	INACTIVE,

	/* Resource is reserved but not yet in use (mainly for FUNDS). */
	ALLOCATED
}