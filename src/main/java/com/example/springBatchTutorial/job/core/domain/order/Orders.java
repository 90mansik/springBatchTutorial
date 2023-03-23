package com.example.springBatchTutorial.job.core.domain.order;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Getter
@ToString
@Entity
@Table(name = "ORDERS")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String orderItem;
    private Integer price;
    private Date orderDate;

}
