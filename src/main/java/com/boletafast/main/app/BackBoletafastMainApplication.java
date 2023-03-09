package com.boletafast.main.app;

import com.boletafast.main.app.mail.SendMailService;
import com.boletafast.main.app.models.dao.ShippingRecordDao;
import com.boletafast.main.app.models.documents.ShippingRecord;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import reactor.core.publisher.Mono;



import java.io.File;
import java.io.FileInputStream;

@EnableFeignClients
@SpringBootApplication
public class BackBoletafastMainApplication implements CommandLineRunner {

	@Autowired
	private SendMailService sendMailService;
	
    @Autowired
    private ShippingRecordDao shippingRecordDao;

    public static void main(String[] args) {
        SpringApplication.run(BackBoletafastMainApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
    	
    	sendMailService.sendMail().subscribe();

        ShippingRecord shippingRecord = new ShippingRecord();
        shippingRecord.setAddressee("d");
        shippingRecord.setEmail("dd");
        shippingRecord.setSender("bbbb");
        shippingRecordDao.save(shippingRecord).subscribe();

        Mono.just(shippingRecord).flatMap( s -> this.shippingRecordDao.save(s)).subscribe( p -> System.out.println(p.toString()));

        FileInputStream archivoExcel = new FileInputStream(new File("D:\\dato.xlsx"));
        Workbook libroExcel = new XSSFWorkbook(archivoExcel);

        Sheet hojaExcel = libroExcel.getSheetAt(0); // Suponiendo que quieres leer la primera hoja

        JSONArray nombresColumnas = new JSONArray();
        Row filaCabecera = hojaExcel.getRow(0);

        for (int i = 0; i < filaCabecera.getLastCellNum(); i++) {
            nombresColumnas.put(filaCabecera.getCell(i).getStringCellValue());
        }

        JSONArray filas = new JSONArray();
        for (int i = 1; i <= hojaExcel.getLastRowNum(); i++) {
            Row fila = hojaExcel.getRow(i);
            JSONObject objetoFila = new JSONObject();

            for (int j = 0; j < nombresColumnas.length(); j++) {
                objetoFila.put(nombresColumnas.getString(j), fila.getCell(j).getStringCellValue());
            }

            filas.put(objetoFila);
        }

        String json = filas.toString();
        System.out.println(json);
    }
}
