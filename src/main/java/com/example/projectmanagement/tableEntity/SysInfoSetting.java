package com.example.projectmanagement.tableEntity;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;


@Data
@Entity
public class SysInfoSetting {
    @Id
    private String infoName;
    private String infoValue;
}
