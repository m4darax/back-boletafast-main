package com.boletafast.main.app.generationresources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.ss.usermodel.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import com.boletafast.main.app.aws.S3Service;
import com.boletafast.main.app.mail.SendMailService;
import com.boletafast.main.app.models.documents.DTOEmployeeSend;
import com.boletafast.main.app.models.documents.Employee;
import com.boletafast.main.app.models.documents.ShippingRecord;
import com.boletafast.main.app.models.services.EmployeeService;
import com.boletafast.main.app.models.services.ShippingRecordService;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GenerationReportServiceImpl implements GenerationReportService {

	private static final Logger LOG = LoggerFactory.getLogger(GenerationReportServiceImpl.class);
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private ShippingRecordService shippingRecordService;
	
	@Autowired
	private SendMailService sendMailService;
	
	@Autowired
	private S3Service s3Service;
	
	@Override
	public Mono<ByteArrayResource> boletaPdf(Employee employee) {
		return Mono.fromCallable( () -> {
			
	            // Cargar el archivo .jasper
	            JasperReport jasperReport = (JasperReport) JRLoader.loadObjectFromFile(employee.getPathJasperBoleta());

	            Map<String, Object> parametros = new HashMap<>();
	            parametros.put("parametro1", "holaa");
	            parametros.put("parametro2", "hola");

	            // Generar el informe
	            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,parametros , new JREmptyDataSource());

	            // Exportar el informe a PDF
	            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

	            // Crear el objeto ByteArrayResource para el archivo PDF
	            ByteArrayResource pdfResource = new ByteArrayResource(pdfBytes);

	            // Devolver el objeto ByteArrayResource
	            return pdfResource;
		});
	}

	@Override
	public Mono<String> generateJsonReport() {

		return Mono.fromCallable( () -> {
			File archivo = new File("D://cabecera.xlsx");
			FileInputStream inputFile = new FileInputStream(archivo);
			// ClassPathResource("D:////\\\\cabecera.xlsx");
			Workbook bookExcel = WorkbookFactory.create(inputFile);

			JSONArray nombresColumnas = new JSONArray();
			Row filaCabecera = bookExcel.getSheetAt(0).getRow(0); //Se obtiene la primera fila para armar el json

			for (int i = 0; i < filaCabecera.getLastCellNum(); i++) {
				nombresColumnas.put(filaCabecera.getCell(i).getStringCellValue());
			}
			
			JSONArray fila = new JSONArray();
			Flux.range(1, bookExcel.getSheetAt(0).getLastRowNum()).flatMap( p -> { 
				Row cell = bookExcel.getSheetAt(0).getRow(p); //esta fila recorre todas las filas hacia abajo
				JSONObject rowObject =  new JSONObject();
				
				Flux.range(0, nombresColumnas.length()).doOnNext( rows -> { //esta linea recorre la fila 
					
					if (cell != null) {
						rowObject.put( nombresColumnas.getString(rows) , cell.getCell(rows) );
					}
				}).subscribe();
				return Mono.just(rowObject.toString());
			} ).doOnNext(p -> {
				fila.put(p);
			}).subscribe();
			
			return fila.toString();
		});
	}
	
	@Description("send the boleta and send the mail")
	@Override
	public Mono<ByteArrayResource> sendBoleta(DTOEmployeeSend dtoEmployeeSend) {
		return this.employeeService.findByDni(dtoEmployeeSend).flatMap( e -> {
			ShippingRecord shippingRecord = new ShippingRecord();
			if ( e != null ) {
				String nameFile = UUID.randomUUID().toString();
				shippingRecord.setDniEmployee(e.getDni());
				shippingRecord.setEmail(e.getEmail());
				shippingRecord.setSender(e.getEmail());
				shippingRecord.setAddressee(dtoEmployeeSend.getAddressee());
				shippingRecord.setPathFileBoleta(e.getDni()+"/"+e.getDni()+"-"+nameFile+".PDF");//name file.pdf
				shippingRecord.setNameFileBoleta(e.getDni()+nameFile+".PDF");
			}
			LOG.info("enviando correo");
		 return this.boletaPdf(e).flatMap( p -> {
			 
			 this.shippingRecordService.save(shippingRecord).map( sh -> {
				 try {
					 LOG.info("ENVIANDO CORREO");
					 	this.s3Service.uploadFileS3(p, shippingRecord).subscribe();
					return this.sendMailService.sendMail(sh.getAddressee(), p).subscribe();
					
				} catch (IOException e1) {
					LOG.info("NO ENVIADO");
					e1.printStackTrace();
					return Mono.empty();
				}
			 }).subscribe();			 
			 return Mono.just(p);
		 });
		});
	}
}
