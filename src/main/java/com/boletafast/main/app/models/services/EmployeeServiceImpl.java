package com.boletafast.main.app.models.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boletafast.main.app.models.dao.EmployeeDao;
import com.boletafast.main.app.models.documents.DTOEmployeeSend;
import com.boletafast.main.app.models.documents.Employee;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class EmployeeServiceImpl implements EmployeeService {
	
	@Autowired
	private EmployeeDao employeeDao;
	
	@Override
	public Flux<Employee> findAll() {
		return this.employeeDao.findAll();
	}

	@Override
	public Mono<Employee> findById(Employee employee) {
		return this.employeeDao.findById(employee.getId());
	}

	@Override
	public Mono<Employee> save(Employee employee) {
		return this.employeeDao.save(employee);
	}

	@Override
	public Mono<Employee> findByDni(DTOEmployeeSend dtoEmployeeSend) {
		
		return this.employeeDao.findByDni(dtoEmployeeSend.getDniEmployee());
	}

}
