package com.boletafast.main.app.models.services;

import com.boletafast.main.app.models.dao.ShippingRecordDao;
import com.boletafast.main.app.models.documents.ShippingRecord;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@Service
public class ShippingRecordServiceImpl implements  ShippingRecordService{

    @Autowired
    private ShippingRecordDao shippingRecordDao;

    @Override
    public Mono<ShippingRecord> findById(ShippingRecord shippingRecord) {
        return shippingRecordDao.findById(shippingRecord.getId());
    }

    @Override
    public Mono<ByteArrayResource> boletaPdf(ShippingRecord shippingRecord) {
        try {
            // Cargar el archivo .jasper
            JasperReport jasperReport = (JasperReport) JRLoader.loadObjectFromFile("C:\\Users\\cleve\\JaspersoftWorkspace\\MyReports\\Blank_A4.jasper");
//            JasperReport jasperReport = JasperCompileManager.compileReport("C:\\Users\\cleve\\JaspersoftWorkspace\\MyReports\\Blank_A4.jrxml");

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
            return Mono.just(pdfResource);

        } catch (JRException e) {
            e.printStackTrace();
            return Mono.empty();
        }
    }
}
