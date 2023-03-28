package com.boletafast.main.app.models.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.boletafast.main.app.models.documents.Employee;

import reactor.core.publisher.Mono;

public interface EmployeeDao extends ReactiveMongoRepository<Employee, String>{
	
	Mono<Employee> findByDni(Long dni);
	
	
}
