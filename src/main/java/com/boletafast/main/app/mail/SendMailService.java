package com.boletafast.main.app.mail;

import reactor.core.publisher.Mono;

public interface SendMailService {

	public Mono<Void> sendMail ();
}
