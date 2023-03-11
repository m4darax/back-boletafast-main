package com.boletafast.main.app.models.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(value = "shipping_record")
public class ShippingRecord {

    @Id
    private String id;
    
    private String sender;
    private String addressee;
    private String email;
    
    //@Indexed(unique = true)
    @Field(name = "dni_employee")
    private Integer dniEmployee;
    
    @Field(name = "path_file_boleta")
    private String pathFileBoleta;
    
    @Field(name = "name_file_boleta")
    private String nameFileBoleta;

}
