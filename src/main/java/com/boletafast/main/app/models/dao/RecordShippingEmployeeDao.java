package com.boletafast.main.app.models.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.boletafast.main.app.models.documents.RecordShippingEmployee;

public interface RecordShippingEmployeeDao extends ReactiveMongoRepository<RecordShippingEmployee, String> {

}
