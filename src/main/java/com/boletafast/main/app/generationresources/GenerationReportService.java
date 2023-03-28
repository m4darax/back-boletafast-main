package com.boletafast.main.app.generationresources;

import java.util.List;
import java.util.Map;

import org.bson.json.JsonObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.Description;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.codec.multipart.FilePart;

import com.boletafast.main.app.models.documents.DTOEmployeeSend;
import com.boletafast.main.app.models.documents.Employee;
import com.boletafast.main.app.models.documents.RecordShippingEmployee;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GenerationReportService {

	@Description("required id in documents")
	Mono<ByteArrayResource> boletaPdf(Employee employee, JSONObject jsonObject);

	Mono<String> generateJsonReport();
	
	Mono<Void> sendBoletaWithS3Upload( JSONObject jsonEmployeeExcel, String nameInitialFolder);
	
	Mono<Map<String, Object>> listJsonExcel (FilePart filePart, Employee employee);
	
	Mono<JSONObject> listJsonExcelWithS3 (RecordShippingEmployee recordShippingEmployee);
	
	Mono<List<JSONObject>> listJsonExcelWithS3 (String urlExcel);
	
	Mono<Map<String,Object>> sendShippingUploadS3(RecordShippingEmployee recordShippingEmployee);
	
}
