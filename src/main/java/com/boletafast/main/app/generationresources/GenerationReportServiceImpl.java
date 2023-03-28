package com.boletafast.main.app.generationresources;

import java.beans.Beans;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.LongAccumulator;

import org.apache.poi.ss.usermodel.*;
import org.jfree.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Description;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.boletafast.main.app.aws.S3Service;
import com.boletafast.main.app.mail.SendMailService;
import com.boletafast.main.app.models.documents.DTOEmployeeSend;
import com.boletafast.main.app.models.documents.Employee;
import com.boletafast.main.app.models.documents.RecordShippingEmployee;
import com.boletafast.main.app.models.documents.ShippingRecord;
import com.boletafast.main.app.models.services.EmployeeNoReactiveService;
import com.boletafast.main.app.models.services.EmployeeService;
import com.boletafast.main.app.models.services.RecordShippingEmployeeService;
import com.boletafast.main.app.models.services.ShippingRecordService;
import com.boletafast.main.app.util.MapMoths;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Service
public class GenerationReportServiceImpl implements GenerationReportService {

	private static final Logger LOG = LoggerFactory.getLogger(GenerationReportServiceImpl.class);
	
	@Value("${aws.s3.bucket}")
	private String nameBucket;
	
	@Value("${aws.s3.region}")
	private String awsRegion;
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private EmployeeNoReactiveService employeeNoReactiveService;
	
	@Autowired
	private ShippingRecordService shippingRecordService;
	
	@Autowired
	private SendMailService sendMailService;
	
	@Autowired
	private S3Service s3Service;
	
	@Autowired
	private MapMoths mapMoths;
	
	@Autowired
	private AmazonS3 amazonS3;
	
	@Autowired
	private RecordShippingEmployeeService recordShippingEmployeeService;
	
	@Override
	public Mono<ByteArrayResource> boletaPdf(Employee employee, JSONObject jsonObject ) {
		 return Mono.fromCallable(() -> {
			 
			 S3Object s3Object = amazonS3.getObject(new GetObjectRequest(nameBucket, "resourcesJasper/plantillaboleta.jasper"));
			 InputStream da = s3Object.getObjectContent();
	            // Cargar el archivo .jasper
	            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(da);

	            Map<String, Object> parametros = jsonObject.toMap();

	            // Generar el informe
	            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, new JREmptyDataSource());

	            // Exportar el informe a PDF
	            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

	            // Crear el objeto ByteArrayResource para el archivo PDF
	            ByteArrayResource pdfResource = new ByteArrayResource(pdfBytes);

	            // Devolver el objeto ByteArrayResource
	            return pdfResource;
	        })
	        .subscribeOn(Schedulers.boundedElastic())
	        .doFinally(signal -> {
	            // Liberar el objeto JasperReport
	            //jasperReport.close();
	        });
		
		/*
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
		});*/
	}

	@Override
	public Mono<String> generateJsonReport() {
/*
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
		});*/
		return Mono.empty();
	}

	@Description("send the boleta and send the mail")
	@Override
	public Mono<Void> sendBoletaWithS3Upload(JSONObject jsonEmployeeExcel, String nameInitialFolder) {
		
		Map<String,Object> mapData = jsonEmployeeExcel.toMap();
		String dniS = mapData.get("DNI").toString();
		
		return this.employeeService.findByDni(Employee.builder().dni(Long.valueOf(dniS)).build()).filter(emplo -> {
			
			LOG.info("EXITE: " + emplo.toString());
			return emplo != null;
		}).flatMap( e -> {
			LOG.info("EXITE: " + e.toString());
			ShippingRecord shippingRecord = new ShippingRecord();
			if ( e != null ) {
				String idNameInitialFolder = nameInitialFolder;
				String nameFile = e.getDni() +"-"+ UUID.randomUUID().toString()+ ".PDF";
				Integer anio = LocalDate.now().getYear();
				String month = mapMoths.getMoth(LocalDate.now().getMonth().toString().toLowerCase());
				String pathFullS3NotName = idNameInitialFolder + "/" + e.getDni()+"/"+anio+"/"+month.toUpperCase();
				String pathFileS3 = pathFullS3NotName+"/"+nameFile;
				
				shippingRecord.setAnio(anio);
				shippingRecord.setMonth(month);
				shippingRecord.setDniEmployee(e.getDni());
				shippingRecord.setAddressee(e.getEmail());
				shippingRecord.setPathFileBoleta(pathFileS3);//name file.pdf
				shippingRecord.setNameFileBoleta(nameFile);
				shippingRecord.setPathFileS3NotName(pathFullS3NotName);
			}
			LOG.info("enviando correo");
		 return this.boletaPdf(e, jsonEmployeeExcel).flatMap( p -> {
			 
			 this.shippingRecordService.save(shippingRecord).map( sh -> {
				 try {
					 	this.s3Service.uploadFileS3(p, shippingRecord).subscribe();
					 	this.boletaPdf(e, jsonEmployeeExcel);
					return this.sendMailService.sendMail(sh, p).subscribe();
					
				} catch (IOException e1) {
					LOG.info("NO ENVIADO");
					e1.printStackTrace();
					return Mono.empty();
				}
			 }).subscribe();	
			 //return Mono.just(p);
			 return Mono.empty();
		 });
		});
	}

	
	//obtiene el filepart lo sube a s3, luego de s3 obtiene el excel y devuelve el json
	@Override
	public Mono<Map<String, Object>> listJsonExcel(FilePart filePart, Employee employee) {
		
		String initialFolderS3 = UUID.randomUUID().toString();
		Integer anio = LocalDate.now().getYear();
    	String pathFile = initialFolderS3 +"/documentoEnProceso";
    	String pathFileUpload = pathFile+"/"+filePart.filename().replace(" ","").replace(":", "");
    	RecordShippingEmployee recordShippingEmployee = RecordShippingEmployee.builder()
    			.dniEmployee(employee.getDni())
    			.pathFileExcelS3(pathFileUpload)
    			.dateSend(new Date())
    			.idFolderS3(initialFolderS3)
    			.build();
		
	    // Obtiene un flujo reactivo de búferes de datos de archivo a partir del archivo FilePart
	    Flux<DataBuffer> dataBufferFlux = filePart.content();
	    
	    // Crea un objeto ObjectMetadata para proporcionar información sobre el archivo a subir
	    ObjectMetadata metadata = new ObjectMetadata();
	    
	    return DataBufferUtils.join(dataBufferFlux)
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()]; // Crea un arreglo de bytes con la cantidad de bytes en el búfer de datos actual
                    dataBuffer.read(bytes); // Lee los bytes del búfer de datos y los almacena en el arreglo de bytes
                    DataBufferUtils.release(dataBuffer); // Libera el búfer de datos actual
                    return bytes; // Devuelve el arreglo de bytes
                })
                .map(byteArray -> {metadata.setContentLength(byteArray.length); return new ByteArrayInputStream(byteArray);}) // Convierte el arreglo de bytes en un InputStream
                .flatMap(inputStream -> Mono.just(inputStream))
                .flatMap( inputS -> {
                	
                	PutObjectRequest request = new PutObjectRequest(nameBucket, pathFileUpload, inputS,metadata);
        		    
        		    CompletableFuture<PutObjectResult> featurS3 = CompletableFuture.supplyAsync(() -> amazonS3.putObject(request) );
        		    
        		  return Mono.fromFuture(featurS3).flatMap( p -> {
        			 return this.recordShippingEmployeeService.save(recordShippingEmployee).flatMap( record -> {
        				 
        				 Map<String, Object> map = new HashMap<>();
        				 map.put("record", record);

        				this.listJsonExcelWithS3(record).subscribe(json -> {
        					map.put("content", json.toMap());
        				});
        				
        				return Mono.just(map);
        			 });

        		    }).doOnSuccess( result -> LOG.info("ARCHIVO CARGADO"))
        		    .doOnError( throwable -> new AmazonServiceException ("ERROR AL SUBIR ARCHIVO") );
                	
                }); // Convierte el InputStream en un Mono asíncrono
	  
	}

	@Override
	public Mono<JSONObject> listJsonExcelWithS3(RecordShippingEmployee recordShippingEmployee) {
		
		 return Mono.fromCallable(() -> {
		        // Obtener el objeto S3
		        S3Object s3Object = amazonS3.getObject(new GetObjectRequest(nameBucket, recordShippingEmployee.getPathFileExcelS3()));

		        // Crear el libro de Excel
		        Workbook bookExcel = WorkbookFactory.create(s3Object.getObjectContent());

		        // Crear la lista de nombres de columnas
		        Row filaCabecera = bookExcel.getSheetAt(0).getRow(0);
		        List<String> nombresColumnas = new ArrayList<>();
		        for (int i = 0; i < filaCabecera.getLastCellNum(); i++) {
		            nombresColumnas.add(filaCabecera.getCell(i).getStringCellValue());
		        }

		        // Convertir todas las celdas a texto
		        for (int i = 0; i < bookExcel.getNumberOfSheets(); i++) {
		            Sheet sheet = bookExcel.getSheetAt(i);
		            for (Row row : sheet) {
		                for (Cell cell : row) {
		                    if (cell.getCellType() != CellType.BLANK) {
		                        cell.setCellType(CellType.STRING);
		                    }
		                }
		            }
		        }

		        // Generar el objeto JSON
		        JSONObject json = new JSONObject();
		        JSONArray rowsArray = new JSONArray();
		        
		        for (int i = 1; i <= bookExcel.getSheetAt(0).getLastRowNum(); i++) {
		            Row row = bookExcel.getSheetAt(0).getRow(i);
		            if (row != null) {
		                JSONObject rowObject = new JSONObject();
		                for (int j = 0; j < nombresColumnas.size(); j++) {
		                    Cell cell = row.getCell(j);
		                    if (cell != null) {
		                        String cellValue = cell.getStringCellValue();
		                        rowObject.put(nombresColumnas.get(j), cellValue);
		                    }
		                }
		                Long dniEmployee = Long.valueOf(rowObject.toMap().get("DNI").toString());
		               if ( !this.employeeNoReactiveService.findByDniBoolean((Employee.builder().dni(dniEmployee).build())) ) {
		            	   rowsArray.put(rowObject);
		               }

		            }
		        }
		        
		        json.put("rows", rowsArray);
		        return json;
		    });
		
		/*JSONArray fila = new JSONArray();
		
		Mono.fromCallable( () -> {
			// Obtener el objeto S3
	        S3Object s3Object = amazonS3.getObject	(new GetObjectRequest(nameBucket, urlS3Excel));
			
			Workbook bookExcel = WorkbookFactory.create(s3Object.getObjectContent());

			JSONArray nombresColumnas = new JSONArray();
			Row filaCabecera = bookExcel.getSheetAt(0).getRow(0); //Se obtiene la primera fila para armar el json
			
			for (int i = 0; i < bookExcel.getNumberOfSheets(); i++) { //convierte todo el excel en string
			    Sheet sheet = bookExcel.getSheetAt(i);
			    for (Row row : sheet) {
			        for (Cell cell : row) {
			            if (cell.getCellType() != CellType.BLANK) {
			                cell.setCellType(CellType.STRING);
			            }
			        }
			    }
			}

			for (int i = 0; i < filaCabecera.getLastCellNum(); i++) {
				nombresColumnas.put(filaCabecera.getCell(i).getStringCellValue());
			}
			JSONObject rowObject =  new JSONObject();
			return Flux.range(1, bookExcel.getSheetAt(0).getLastRowNum()).flatMap( p -> { 
				Row cell = bookExcel.getSheetAt(0).getRow(p); //esta fila recorre todas las filas hacia abajo
				
				Flux.range(0, nombresColumnas.length()).doOnNext( rows -> { //esta linea recorre la fila 
					
					if (cell != null) {
						rowObject.put( nombresColumnas.getString(rows).toUpperCase() , cell.getCell(rows) );
					}
				}).subscribe();
				return Mono.just(rowObject.toString());
			} ).doOnNext(p -> {
				fila.put(p);
			}).subscribe();
		}).subscribe();
		LOG.info(fila.toString());
		return Mono.just(fila.toString());*/
	}
	
	
	public Mono<Map<String,Object>> sendShippingUploadS3(RecordShippingEmployee recordShippingEmployee){
		String nameInitialFolder = recordShippingEmployee.getIdFolderS3();

		this.listJsonExcelWithS3(recordShippingEmployee.getPathFileExcelS3()).doOnNext( listJsonObject -> {
			
			listJsonObject.forEach( jsonObject -> {
				this.sendBoletaWithS3Upload(jsonObject, nameInitialFolder).subscribe();
			});

		}).subscribe();
		
		Map<String,Object> data = new HashMap<>();
		data.put("prueba", "data");
		
		return Mono.just(data);
	}

	@Override
	public Mono<List<JSONObject>> listJsonExcelWithS3(String urlExcel) {
		return Mono.fromCallable(() -> {
		    // Obtener el objeto S3
		    S3Object s3Object = amazonS3.getObject(new GetObjectRequest(nameBucket, urlExcel));

		    // Crear el libro de Excel
		    Workbook bookExcel = WorkbookFactory.create(s3Object.getObjectContent());

		    // Crear la lista de nombres de columnas
		    Row filaCabecera = bookExcel.getSheetAt(0).getRow(0);
		    List<String> nombresColumnas = new ArrayList<>();
		    for (int i = 0; i < filaCabecera.getLastCellNum(); i++) {
		        nombresColumnas.add(filaCabecera.getCell(i).getStringCellValue());
		    }

		    // Convertir todas las celdas a texto
		    for (int i = 0; i < bookExcel.getNumberOfSheets(); i++) {
		        Sheet sheet = bookExcel.getSheetAt(i);
		        for (Row row : sheet) {
		            for (Cell cell : row) {
		                if (cell.getCellType() != CellType.BLANK) {
		                    cell.setCellType(CellType.STRING);
		                }
		            }
		        }
		    }

		    // Crear una lista de objetos JSONObject
		    List<JSONObject> rowsList = new ArrayList<>();
		    for (int i = 1; i <= bookExcel.getSheetAt(0).getLastRowNum(); i++) {
		        Row row = bookExcel.getSheetAt(0).getRow(i);
		        if (row != null) {
		            JSONObject rowObject = new JSONObject();
		            for (int j = 0; j < nombresColumnas.size(); j++) {
		                Cell cell = row.getCell(j);
		                if (cell != null) {
		                    String cellValue = cell.getStringCellValue();
		                    rowObject.put(nombresColumnas.get(j), cellValue);
		                }
		            }
		            rowsList.add(rowObject);
		        }
		    }
		    return rowsList;
		});
	}

	
}
