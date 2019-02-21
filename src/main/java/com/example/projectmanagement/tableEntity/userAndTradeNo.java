package com.example.projectmanagement.tableEntity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "user_out_trade_no")
public class userAndTradeNo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String out_trade_no;
}
