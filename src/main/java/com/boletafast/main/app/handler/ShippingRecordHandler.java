package com.boletafast.main.app.handler;

import com.boletafast.main.app.generationresources.GenerationReportService;
import com.boletafast.main.app.models.documents.DTOEmployeeSend;
import com.boletafast.main.app.models.documents.Employee;
import com.boletafast.main.app.models.documents.ShippingRecord;
import com.boletafast.main.app.models.services.EmployeeService;

import java.net.URI;
import java.util.Date;

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
    private GenerationReportService generationReportService;
    
    @Autowired
    private EmployeeService employeeService;

    public Mono<ServerResponse> getBoletaPdf(ServerRequest request){

        return this.generationReportService.boletaPdf(new Employee()).flatMap( p -> ServerResponse.ok().contentType(MediaType.APPLICATION_PDF).body(BodyInserters.fromValue(p)));
    }

    
    public Mono<ServerResponse> createEmployee(ServerRequest request){
    	
    	Mono<Employee> newEmployee = request.bodyToMono(Employee.class);
    	
    	return newEmployee.flatMap(p -> {
    		p.setDateCreateEmployee(new Date());
    		return this.employeeService.save(p).flatMap(em -> {
    			return ServerResponse.created(URI.create("/api/createemployee/".concat(em.getId()))).contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(em));
    		});
    	});
    }
    
    public Mono<ServerResponse> sendBoleta (ServerRequest request) {
    	Mono<DTOEmployeeSend> dtoEmployee = request.bodyToMono(DTOEmployeeSend.class);
    	return dtoEmployee.flatMap(p -> {
    		return this.generationReportService.sendBoleta(p).flatMap( pe -> ServerResponse.ok().contentType(MediaType.APPLICATION_PDF).body(BodyInserters.fromValue(pe)));
    	} ).switchIfEmpty(ServerResponse.noContent().build());
    }
}
