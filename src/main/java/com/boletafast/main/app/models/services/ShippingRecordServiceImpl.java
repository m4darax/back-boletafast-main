package com.boletafast.main.app.models.services;

import com.boletafast.main.app.models.dao.ShippingRecordDao;
import com.boletafast.main.app.models.documents.ShippingRecord;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@Service
public class ShippingRecordServiceImpl implements  ShippingRecordService{

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

}
