package com.university.ecom_app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;
    private String brand;
    private BigDecimal price;
    private String category;

    //this will make the date appear in dd-mm-yyyy format in the front end by saving as this in the DB
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date releaseDate;
    private boolean availability;
    private int quantity;

    private String imageName;
    private String imageType;

    @Lob //LargeObject = Lob...used when storing large objects like images in databases
    private byte[] imageData;

}

