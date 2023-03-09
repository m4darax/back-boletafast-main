package com.boletafast.main.app.handler;

import com.boletafast.main.app.models.documents.ShippingRecord;
import com.boletafast.main.app.models.services.ShippingRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ShippingRecordHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ShippingRecordHandler.class);

    @Autowired
    private ShippingRecordService shippingRecordService;

    public Mono<ServerResponse> getBoletaPdf(ServerRequest request){

        return this.shippingRecordService.boletaPdf(new ShippingRecord()).flatMap( p -> ServerResponse.ok().contentType(MediaType.APPLICATION_PDF).body(BodyInserters.fromValue(p)));
    }

}
