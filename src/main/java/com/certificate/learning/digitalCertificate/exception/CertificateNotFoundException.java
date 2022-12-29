package com.certificate.learning.digitalCertificate.exception;

@SuppressWarnings("serial")
public class CertificateNotFoundException extends RuntimeException{
	public CertificateNotFoundException() {
		
	}
	public CertificateNotFoundException(String message){
		super(message);
	}
}