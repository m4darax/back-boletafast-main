package com.boletafast.main.app.models.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boletafast.main.app.models.dao.EmployeeNoReactiveDao;
import com.boletafast.main.app.models.documents.Employee;

@Service
public class EmployeeNoReactiveServiceImpl implements EmployeeNoReactiveService {

	@Autowired
	private EmployeeNoReactiveDao employeeNoReactiveDao;
	
	@Override
	public Boolean findByDniBoolean(Employee employee) {
		// TODO Auto-generated method stub
		return this.employeeNoReactiveDao.findByDni(employee.getDni()).isEmpty();
	}
}
