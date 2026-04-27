package com.healthgov.external;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.healthgov.exceptions.ProgramServiceUnavailableException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProgramFeignClientFallbackFactory implements FallbackFactory<ProgramFeignClient> {

	@Override
	public ProgramFeignClient create(Throwable cause) {
		return programId -> {

			log.error("Feign fallback triggered. programId={}", programId, cause);

			throw new ProgramServiceUnavailableException(
					"Program service is temporarily unavailable. Please try again later.");
		};
	}
}