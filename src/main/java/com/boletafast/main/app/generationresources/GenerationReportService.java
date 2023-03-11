package com.boletafast.main.app.generationresources;

import org.springframework.context.annotation.Description;
import org.springframework.core.io.ByteArrayResource;

import com.boletafast.main.app.models.documents.DTOEmployeeSend;
import com.boletafast.main.app.models.documents.Employee;

import reactor.core.publisher.Mono;

public interface GenerationReportService {

	@Description("required id in documents")
	Mono<ByteArrayResource> boletaPdf(Employee employee);

	Mono<String> generateJsonReport();
	
	Mono<ByteArrayResource> sendBoleta( DTOEmployeeSend dtoEmployeeSend);
}
