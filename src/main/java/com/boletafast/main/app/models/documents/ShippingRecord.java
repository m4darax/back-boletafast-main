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
    private Integer anio;
    private String Month;
    
    //@Indexed(unique = true)
    @Field(name = "dni_employee")
    private Long dniEmployee;
    
    @Field(name = "path_file_boleta")
    private String pathFileBoleta;
    
    @Field(name = "name_file_boleta")
    private String nameFileBoleta;

    @Field(name = "path_file_full_boleta_s3_public")
    private String  pathFileFullBoletaS3Public;
    
    @Field(name = "path_file_s3_not_name")
    private String pathFileS3NotName;

    @Field(name = "id_send_record_shipping_employee")
    private String idSendRecordShippingEmployee;
}
