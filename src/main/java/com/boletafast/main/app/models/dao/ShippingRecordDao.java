package com.boletafast.main.app.models.dao;

import com.boletafast.main.app.models.documents.ShippingRecord;

import jdk.jfr.Description;
import reactor.core.publisher.Flux;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ShippingRecordDao extends ReactiveMongoRepository<ShippingRecord, String> {

	@Description(value = "required dni string")
	Flux <ShippingRecord> findByDniEmployee(Long dniEmployee);
	
}
