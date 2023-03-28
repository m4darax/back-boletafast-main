package com.boletafast.main.app.aws;

import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.boletafast.main.app.models.documents.Employee;
import com.boletafast.main.app.models.documents.RecordShippingEmployee;
import com.boletafast.main.app.models.documents.ShippingRecord;
import com.boletafast.main.app.models.services.EmployeeService;
import com.boletafast.main.app.models.services.ShippingRecordService;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class S3ServiceImpl implements S3Service {
	
	private static final Logger LOG = LoggerFactory.getLogger(S3Service.class);

	@Value("${aws.s3.bucket}")
	private String nameBucket;
	
	@Value("${aws.s3.region}")
	private String awsRegion;
	
	@Autowired
	private AmazonS3 amazonS3;
	
	@Autowired
	private ShippingRecordService shippingRecordService;
	
	@Autowired
	private EmployeeService employeeService;
	
	@Override
	public Mono<Void> uploadFileS3(ByteArrayResource byteArrayResource, ShippingRecord shippingRecord) {

		byte[] bytes = byteArrayResource.getByteArray();
	    InputStream inputStream = new ByteArrayInputStream(bytes);
	    ObjectMetadata metadata = new ObjectMetadata();
	    ObjectMetadata metadataPdf = new ObjectMetadata();
	    metadataPdf.setContentType("application/pdf");
	    metadata.setContentLength(bytes.length);
	    PutObjectRequest request = new PutObjectRequest(nameBucket, shippingRecord.getPathFileBoleta(), inputStream, metadata)
	    		.withCannedAcl(CannedAccessControlList.PublicReadWrite)
	    		.withMetadata(metadataPdf);
	    CompletableFuture<PutObjectResult> futureResult = CompletableFuture.supplyAsync(() -> amazonS3.putObject(request));
	    
	    return Mono.fromFuture(futureResult).doOnSuccess(result -> {
	        
	        try {
	        	LOG.info("ARCHIVO: " + shippingRecord.getNameFileBoleta() + " CARGADO");
	        	String urlPublicS3 = amazonS3.getUrl(nameBucket, shippingRecord.getPathFileBoleta()).toString();
	        	shippingRecord.setPathFileFullBoletaS3Public(urlPublicS3);
	        	this.shippingRecordService.save(shippingRecord).doOnNext( shi -> {
	        		
	        		Employee employee = new Employee();
	        		employee.setDni(shi.getDniEmployee());
	        		this.employeeService.findByDni(employee).doOnNext( emp -> {
	        			
	        			emp.setPathFileFullBoletaS3Public(urlPublicS3);
	  
	        			this.employeeService.save(emp).subscribe();
	        			
	        		}).subscribe();
	        	}).subscribe();
	        	
	        	
	            inputStream.close();
	        } catch (IOException e) {
	            LOG.error("Error al cerrar el stream de entrada", e);
	        }
	    }).doOnError(throwable -> {
	        LOG.error("Error al cargar el archivo " + shippingRecord.getNameFileBoleta() + " a S3", throwable);
	    }).then();
		
		
	/*	ByteArrayResource resource = byteArrayResource;
		

		// obtener un Flux<DataBuffer> a partir del ByteArrayResource
		Flux<DataBuffer> dataBufferFlux = DataBufferUtils.read(resource, new DefaultDataBufferFactory(), 1024);

	return	dataBufferFlux.flatMap( databu -> {
			
			byte[] bytes = new byte[databu.readableByteCount()];
			databu.read(bytes);
			InputStream inputStream = new ByteArrayInputStream(bytes);
			
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(bytes.length);
			
			Map<String, Object> res = new HashMap<>();
			
			res.put("input", inputStream);
			res.put("metadata", metadata);
			
			return Flux.just(res);
		}).doOnComplete( () -> {
			LOG.info("COMPLETADO");
		}).flatMap( p ->  {
			
			InputStream input = (InputStream) p.get("input");
			ObjectMetadata metadata =  (ObjectMetadata) p.get("metadata");
			
			PutObjectRequest request = new PutObjectRequest(nameBucket, shippingRecord.getPathFileBoleta(), input, metadata);
			
			CompletableFuture<PutObjectResult> futureResult = CompletableFuture.supplyAsync( () -> {
				return amazonS3.putObject(request);
			});
			
			Mono.fromFuture(futureResult).doOnSuccess( resultSuccess -> {
				if (resultSuccess != null) {
					LOG.info("ARCHIVO:" + shippingRecord.getNameFileBoleta() + " CARGADO");
				};
			}).doOnError(throwable -> {
			    LOG.error(throwable.getMessage());
			})
			.subscribe();
			return Flux.empty();
		});*/
	}

	@Override
	public Flux<Void> uploadFileS3Old(ByteArrayResource byteArrayResource, ShippingRecord shippingRecord) {
		LOG.info("METODO: uploadFileS3");
		// Obtener un Flux<DataBuffer> a partir del ByteArrayResource
		Flux<DataBuffer> dataBufferFlux = DataBufferUtils.read(byteArrayResource, new DefaultDataBufferFactory(), 1024)
				.doOnError(Throwable::printStackTrace);

		//File file = new File("src/main/resources/boletas/" + shippingRecord.getNameFileBoleta());
		File file = new File(getClass().getClassLoader().getResource("boletas/" + shippingRecord.getNameFileBoleta()).getFile());
		LOG.info(file.getAbsolutePath());
		// Escribir los datos en el archivo de forma asincrónica
		return Flux.using(() -> new FileOutputStream(file),
				outputStream -> DataBufferUtils.write(dataBufferFlux, outputStream), outputStream -> {
					try {
						outputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				})/*
					 * .doFinally(signalType -> { if (signalType == SignalType.ON_COMPLETE) {
					 * System.out.println("Flujo completado sin errores"); } else if (signalType ==
					 * SignalType.ON_ERROR) {
					 * System.out.println("Ha ocurrido un error en el flujo"); } else if (signalType
					 * == SignalType.CANCEL) { System.out.println("Flujo cancelado"); }})
					 */.doOnComplete(() -> {
					LOG.info("archivo: " + shippingRecord.getNameFileBoleta() + " cargado");
					// Crear una solicitud de carga de archivo
					PutObjectRequest request = new PutObjectRequest(nameBucket, shippingRecord.getPathFileBoleta(),
							file);
					// Realizar la operación de carga de archivo en Amazon S3 de forma asincrónica
					try {
						// PutObjectResult result = amazonS3.putObject(request);
						CompletableFuture<PutObjectResult> futureResult = CompletableFuture.supplyAsync(() -> {
						    // Llamada asíncrona a amazonS3.putObject(request)
						    return amazonS3.putObject(request);
						});

						Mono.fromFuture(futureResult).doOnSuccess( result -> {
							if (result != null) {
								LOG.info("ARCHIVO ELIMINADO");
								file.delete();
							};
						}).subscribe();
					} catch (AmazonServiceException e) {
						LOG.info(e.getMessage());
					} catch (AmazonClientException e) {
						LOG.info(e.getMessage());
					} catch (Exception e) {
						LOG.info(e.getMessage());
					} finally {

					}
				}).flatMap(p -> Flux.empty());
	}

	@Override
	public Mono<Void> uploadFileS3(ByteArrayResource byteArrayResource, RecordShippingEmployee recordShippingEmployee) {
		
		
		
		
		return Mono.empty();
	}

}
