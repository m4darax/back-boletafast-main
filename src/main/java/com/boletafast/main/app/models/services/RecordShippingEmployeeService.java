package com.boletafast.main.app.models.services;

import com.boletafast.main.app.models.documents.RecordShippingEmployee;

import reactor.core.publisher.*;

public interface RecordShippingEmployeeService {
	
	Mono<RecordShippingEmployee> save(RecordShippingEmployee recordShippingEmployee);
	
	Flux<RecordShippingEmployee> findAll();

}
