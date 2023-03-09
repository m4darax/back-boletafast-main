package com.boletafast.main.app.models.dao;

import com.boletafast.main.app.models.documents.ShippingRecord;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ShippingRecordDao extends ReactiveMongoRepository<ShippingRecord, String> {

}
