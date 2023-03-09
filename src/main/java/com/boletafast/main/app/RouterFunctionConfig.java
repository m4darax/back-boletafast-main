package com.boletafast.main.app;

import com.boletafast.main.app.handler.ShippingRecordHandler;
import com.boletafast.main.app.models.services.ShippingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Component
public class RouterFunctionConfig {

    @Autowired
    private ShippingRecordService shippingRecordService;

    @Bean
    public RouterFunction<ServerResponse> routes(ShippingRecordHandler handler) {
        return RouterFunctions.route(RequestPredicates.GET("/api/pdf/{id}"), request -> handler.getBoletaPdf(request) );
    }
}
