package com.boletafast.main.app.aws;

import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.boletafast.main.app.models.documents.ShippingRecord;

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
	
	@Override
	public Flux<Void> uploadFileS3(ByteArrayResource byteArrayResource, ShippingRecord shippingRecord) {

		// Obtener un Flux<DataBuffer> a partir del ByteArrayResource
		Flux<DataBuffer> dataBufferFlux = DataBufferUtils.read(byteArrayResource, new DefaultDataBufferFactory(), 1024)
				.doOnError(Throwable::printStackTrace);

		File file = new File("src/main/resources/boletas/" + shippingRecord.getNameFileBoleta());
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

}
