package com.healthgov.external;

import org.springframework.stereotype.Component;

import com.healthgov.exceptions.ProgramNotFoundException;

import feign.Response;
import feign.codec.ErrorDecoder;

@Component
public class FeignClientErrorDecoder implements ErrorDecoder {

	@Override
	public Exception decode(String methodKey, Response response) {

		if (response.status() == 404) {
			return new ProgramNotFoundException("Program not found");
		}

		return new RuntimeException("Program service error. HTTP status: " + response.status());
	}
}