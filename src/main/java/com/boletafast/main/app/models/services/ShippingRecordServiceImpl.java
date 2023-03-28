package com.boletafast.main.app.models.services;

import com.boletafast.main.app.models.dao.ShippingRecordDao;
import com.boletafast.main.app.models.documents.Employee;
import com.boletafast.main.app.models.documents.ShippingRecord;

import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ShippingRecordServiceImpl implements  ShippingRecordService{

	private static final Logger LOG = LoggerFactory.getLogger(ShippingRecordServiceImpl.class);
	
    @Autowired
    private ShippingRecordDao shippingRecordDao;

    @Override
    public Mono<ShippingRecord> findById(ShippingRecord shippingRecord) {
        return shippingRecordDao.findById(shippingRecord.getId());
    }

	@Override
	public Mono<ShippingRecord> save(ShippingRecord shippingRecord) {
		return this.shippingRecordDao.save(shippingRecord);
	}

	@Override
	public Flux<ShippingRecord> findByDni(Employee employee) {
		return this.shippingRecordDao.findByDniEmployee(employee.getDni());
	}

}
