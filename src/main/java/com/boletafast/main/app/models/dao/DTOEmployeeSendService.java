package com.boletafast.main.app.models.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.boletafast.main.app.models.documents.DTOEmployeeSend;

public interface DTOEmployeeSendService extends ReactiveMongoRepository<DTOEmployeeSend, String> {

}
