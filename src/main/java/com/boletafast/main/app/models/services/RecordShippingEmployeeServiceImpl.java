package com.boletafast.main.app.models.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boletafast.main.app.models.dao.RecordShippingEmployeeDao;
import com.boletafast.main.app.models.documents.RecordShippingEmployee;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RecordShippingEmployeeServiceImpl implements RecordShippingEmployeeService {

	@Autowired
	private RecordShippingEmployeeDao recordShippingEmployeeDao;
	
	@Override
	public Mono<RecordShippingEmployee> save(RecordShippingEmployee recordShippingEmployee) {
		return this.recordShippingEmployeeDao.save(recordShippingEmployee);
	}

	@Override
	public Flux<RecordShippingEmployee> findAll() {
		return this.recordShippingEmployeeDao.findAll();
	}

}
