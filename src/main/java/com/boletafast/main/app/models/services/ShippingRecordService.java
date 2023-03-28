package com.boletafast.main.app.models.services;

import com.boletafast.main.app.models.documents.Employee;
import com.boletafast.main.app.models.documents.ShippingRecord;
import jdk.jfr.Description;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ShippingRecordService  {

    @Description(value = "required id string")
    Mono<ShippingRecord> findById(ShippingRecord shippingRecord);
    
    Mono<ShippingRecord> save(ShippingRecord shippingRecord);
    
    @Description(value = "required dni string")
    Flux<ShippingRecord> findByDni(Employee employee);

}
