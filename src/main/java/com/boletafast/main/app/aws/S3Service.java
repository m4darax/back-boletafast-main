package com.boletafast.main.app.aws;

import org.springframework.core.io.ByteArrayResource;

import com.boletafast.main.app.models.documents.RecordShippingEmployee;
import com.boletafast.main.app.models.documents.ShippingRecord;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface S3Service {

	Flux<Void> uploadFileS3Old(ByteArrayResource byteArrayResource, ShippingRecord shippingRecord); 
	
	Mono<Void> uploadFileS3(ByteArrayResource byteArrayResource, ShippingRecord shippingRecord);
	
	Mono<Void> uploadFileS3(ByteArrayResource byteArrayResource, RecordShippingEmployee recordShippingEmployee);
}
