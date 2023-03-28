package com.boletafast.main.app.mail;

import java.io.IOException;

import org.springframework.core.io.ByteArrayResource;

import com.boletafast.main.app.models.documents.ShippingRecord;

import reactor.core.publisher.Mono;

public interface SendMailService {

	public Mono<Void> sendMail (ShippingRecord shippingRecord, ByteArrayResource byteArrayResource) throws IOException;
}
