package com.boletafast.main.app.models.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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

}
