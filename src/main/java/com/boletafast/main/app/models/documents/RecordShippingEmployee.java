package com.boletafast.main.app.models.documents;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Document(value = "record_shipping_employee")
public class RecordShippingEmployee {
	
	@Id
	private String id;
	
	@Field(value = "dni_employee")
	private Long dniEmployee;
	
	@Field(value = "path_file_excel_s3")
	private String pathFileExcelS3;
	
	@Field(value = "date_send")
	private Date dateSend;
	
	@Field(value = "id_folder_s3")
	private String idFolderS3;
}
