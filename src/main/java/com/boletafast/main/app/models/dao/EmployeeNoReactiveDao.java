package com.boletafast.main.app.models.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.boletafast.main.app.models.documents.Employee;


public interface EmployeeNoReactiveDao extends MongoRepository<Employee, String>{

	List<Employee> findByDni(Long dni);
}
