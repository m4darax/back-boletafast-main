package com.boletafast.main.app.handler;

import com.amazonaws.Response;
import com.boletafast.main.app.aws.S3Service;
import com.boletafast.main.app.generationresources.GenerationReportService;
import com.boletafast.main.app.models.documents.DTOEmployeeSend;
import com.boletafast.main.app.models.documents.Employee;
import com.boletafast.main.app.models.documents.RecordShippingEmployee;
import com.boletafast.main.app.models.documents.Employee.EmployeeBuilder;
import com.boletafast.main.app.models.documents.ShippingRecord;
import com.boletafast.main.app.models.services.EmployeeService;
import com.boletafast.main.app.models.services.RecordShippingEmployeeService;
import com.boletafast.main.app.models.services.ShippingRecordService;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.jfree.util.Log;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ShippingRecordHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ShippingRecordHandler.class);

    @Autowired
    private GenerationReportService generationReportService;
    
    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private ShippingRecordService shippingRecordService;
    
    @Autowired
    private RecordShippingEmployeeService recordShippingEmployeeService;
    
    @Autowired
    private S3Service s3Service;

    public Mono<ServerResponse> getBoletaPdf(ServerRequest request){
       //  this.generationReportService.boletaPdf(new Employee()).flatMap( p -> ServerResponse.ok().contentType(MediaType.APPLICATION_PDF).body(BodyInserters.fromValue(p)));
        return ServerResponse.ok().build();
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
    
    //public Mono<ServerResponse> sendBoleta (ServerRequest request) {
    	//Mono<DTOEmployeeSend> dtoEmployee = request.bodyToMono(DTOEmployeeSend.class);
    	//return dtoEmployee.flatMap(p -> {
    		//return this.generationReportService.sendBoleta(p).flatMap( pe -> ServerResponse.ok().contentType(MediaType.APPLICATION_PDF).body(BodyInserters.fromValue(pe)));
    //	} ).switchIfEmpty(ServerResponse.noContent().build());
    //}
    
   /* public Mono<ServerResponse> sendBoletaWithS3Upload(ServerRequest request) {
    	Mono<DTOEmployeeSend> dtoEmployee = request.bodyToMono(DTOEmployeeSend.class);
    	return dtoEmployee.flatMap(p -> {
    		return this.generationReportService.sendBoletaWithS3Upload(p).flatMap( pe -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).build());
    	} ).switchIfEmpty(ServerResponse.noContent().build());
    }*/
    
    public Mono<ServerResponse> findAllEmployee(ServerRequest request) {
    	 //return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
           //      .body(this.employeeService.findAll(), Employee.class);
    	 
    	 return this.employeeService.findAll().collectList().flatMap(employees -> ServerResponse.ok()
    	            .contentType(MediaType.APPLICATION_JSON)
    	            .bodyValue(employees));
    	 
    }
    
    public Mono<ServerResponse> detailsShippingEmployess(ServerRequest request) {
    	return request.bodyToMono(Employee.class).flatMap( p -> {
    		return this.shippingRecordService.findByDni(p).collectList().flatMap( pe -> {
    			return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(pe);
    		});
    		
    	});
    }
    
    public Mono<ServerResponse> fileExcelShipping(ServerRequest request) {
    	Mono<Employee> employee = request.multipartData().map( multipart -> {
    		 FormFieldPart dni = (FormFieldPart) multipart.toSingleValueMap().get("dni");
    		Employee employeen =  Employee.builder().dni(Long.valueOf(dni.value())).build();
    		return employeen;
    	});
    	return request.multipartData().map(multipart ->  multipart.toSingleValueMap().get("file"))
    		.cast(FilePart.class)
    		.flatMap(file -> {
    			return employee.flatMap( emp -> {
    				
    				return this.generationReportService.listJsonExcel(file, emp).flatMap( rest -> {
    					
    					return ServerResponse.ok().body(BodyInserters.fromValue(rest));
    				});
    				
    			});
    	});
    }
    
    public Mono<ServerResponse> sendShippingUploadS3(ServerRequest request) {
    	
    	Mono<RecordShippingEmployee> recordMono = request.bodyToMono(RecordShippingEmployee.class);
    	
    	return recordMono.flatMap( rec -> {
    		
    		return this.generationReportService.sendShippingUploadS3(rec).flatMap( ma -> {
    			
    			return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(ma));
    		});
    	 
    	});
    	
    	
    	//RecordShippingEmployee recordShippingEmployee = new RecordShippingEmployee();
    	//recordShippingEmployee.setPathFileExcelS3("76697566/2023/documentoEnProceso/data.xlsx");
    	
    	//this.generationReportService.sendShippingUploadS3(recordShippingEmployee).subscribe();
    	
    	
    	//return ServerResponse.ok().build();
    }
    
}
