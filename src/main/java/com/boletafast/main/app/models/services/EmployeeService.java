package com.boletafast.main.app.models.services;

import com.boletafast.main.app.models.documents.DTOEmployeeSend;
import com.boletafast.main.app.models.documents.Employee;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EmployeeService {

	Flux<Employee> findAll();
	
	Mono<Employee> findById(Employee employee);
	
	Mono<Employee> findByDni(Employee employee);
	
	Mono<Employee> save(Employee employee);
	
	Mono<Employee> findByDni( DTOEmployeeSend dtoEmployeeSend);
	
	
}
