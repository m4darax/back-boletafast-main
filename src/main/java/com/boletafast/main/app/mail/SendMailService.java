package com.boletafast.main.app.mail;

import java.io.IOException;

import org.springframework.core.io.ByteArrayResource;

import reactor.core.publisher.Mono;

public interface SendMailService {

	public Mono<Void> sendMail (String email, ByteArrayResource byteArrayResource) throws IOException;
}
