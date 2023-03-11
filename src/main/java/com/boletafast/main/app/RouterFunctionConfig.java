package com.boletafast.main.app;

import com.boletafast.main.app.handler.ShippingRecordHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Component
public class RouterFunctionConfig {

    @Bean
    public RouterFunction<ServerResponse> routes(ShippingRecordHandler handler) {
        return RouterFunctions.route(RequestPredicates.GET("/api/pdf/{id}"), request -> handler.getBoletaPdf(request) )
        		.andRoute(RequestPredicates.POST("/api/createemployee"), handler::createEmployee)
        		.andRoute(RequestPredicates.POST("/api/sendboleta"), handler::sendBoleta);
    }
}
