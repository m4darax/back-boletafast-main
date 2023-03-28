package com.boletafast.main.app.models.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document("dto_employee_sendboleta")
public class DTOEmployeeSend {

	@Id
	private String id;
	
	@Field(name ="dni_employee")
	private Long dniEmployee;
	
	@Field(name = "addressee")
	private String addressee;
	
}
