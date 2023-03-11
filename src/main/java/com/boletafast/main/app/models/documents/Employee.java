package com.boletafast.main.app.models.documents;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document("employee")
public class Employee {

	@Indexed(unique = true)
	@Id
	private String id;
	
	@Indexed(unique = true)
	private Integer dni;
	
	private String name;
	
	@Field(name = "first_name")
	private String firstName;
	
	@Field(name = "last_name")
	private String lastName;

	@Field(name = "path_jasper_boleta")
	private String pathJasperBoleta;
	
	@Field(name = "date_create_employee")
	private Date dateCreateEmployee;
	
	@Field(name = "email")
	private String email;
	
}
