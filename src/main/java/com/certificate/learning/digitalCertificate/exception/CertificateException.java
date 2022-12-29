package com.certificate.learning.digitalCertificate.exception;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CertificateException {
	
	@ExceptionHandler(value = CertificateNotFoundException.class)
	public ResponseEntity<String> customerNotFound(CertificateNotFoundException ex){
		return new ResponseEntity<String>("Certificate Not Found...", HttpStatus.NOT_FOUND);
	}
}
