package com.boletafast.main.app;

import com.boletafast.main.app.aws.S3Service;
import com.boletafast.main.app.generationresources.GenerationReportService;
import com.boletafast.main.app.generationresources.GenerationReportServiceImpl;
import com.boletafast.main.app.mail.SendMailService;
import com.boletafast.main.app.models.dao.EmployeeDao;
import com.boletafast.main.app.models.dao.ShippingRecordDao;
import com.boletafast.main.app.models.documents.Employee;
import com.boletafast.main.app.models.documents.ShippingRecord;
import com.boletafast.main.app.models.services.EmployeeService;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ByteArrayResource;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.io.File;
import java.io.FileInputStream;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class BackBoletafastMainApplication implements CommandLineRunner {

	private static final Logger LOG = LoggerFactory.getLogger(GenerationReportServiceImpl.class);
	
	@Autowired
	private SendMailService sendMailService;
	
    @Autowired
    private ShippingRecordDao shippingRecordDao;
    
    @Autowired
    private GenerationReportService generationReportService;
    
    @Autowired
    private EmployeeDao employeeDao;
    
    @Autowired
    private S3Service s3Service;
    
    @Autowired
    private EmployeeService employeeService;

    public static void main(String[] args) {
        SpringApplication.run(BackBoletafastMainApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
    	
    	//sendMailService.sendMail().subscribe();
    	
    	Employee employee = new Employee();
    	employee.setDni(76687455L);
    	employee.setName("Clever");
    	employee.setFirstName("Rivera");
    	employee.setLastName("Mancilla");
    	employee.setPathJasperBoleta("C:\\Users\\cleve\\JaspersoftWorkspace\\MyReports\\Blank_A4.jasper");
    	
    	//Mono.just(employee).flatMap(p -> this.employeeDao.save(p)).subscribe();

        ShippingRecord shippingRecord = new ShippingRecord();
        shippingRecord.setAddressee("d");
        shippingRecord.setEmail("dd");
        shippingRecord.setSender("bbbb");
        shippingRecord.setDniEmployee(76687455L);
        //shippingRecordDao.save(shippingRecord).subscribe();

        //Mono.just(shippingRecord).flatMap( s -> this.shippingRecordDao.save(s)).subscribe( p -> System.out.println(p.toString()));
        
        //this.generationReportService.generateJsonReport().subscribe( p -> LOG.info(p.replace("\\", "")));
        
        
        Flux.just("Clever", "Jesusa", "Diego").doFinally(signalType -> { 
        	if (signalType == SignalType.ON_COMPLETE) {
			  System.out.println("Flujo completado sin errores"); } 
        	else if (signalType == SignalType.ON_ERROR) {
			  System.out.println("Ha ocurrido un error en el flujo"); } 
        	else if (signalType == SignalType.CANCEL) { System.out.println("Flujo cancelado"); }}).map( p -> {
        LOG.info("map: " + p);
        	return p;
        }).doOnNext(LOG::info);//.subscribe( p -> LOG.info("PROBANDO"));
        
        
       // this.s3Service.uploadFileS3().subscribe();
        
       // this.employeeService.findAll().subscribe( p -> LOG.info(p.toString()));
        
       /* Flux.just("Clever", "Jesusa", "Diego").flatMap( p -> {
        	LOG.info("STRING");
        	return Flux.just(1, 2, 4);
        			})
        .flatMap(p -> {
        	return Mono.empty();
        }).subscribe();
        
        Flux<Integer> numeros = Flux.just(1, 2, 3, 4, 5);
        numeros.flatMap(n -> Flux.just(n * n))
               .subscribe(System.out::println);
        
        
*/

        
    }
}
