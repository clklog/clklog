package com.zcunsoft.clklog.sysmgmt.domains;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 操作记录实体表.
 */
@Entity
@Table(name = "sys_operrecord")
@Data
public class OperRecords {

    /**
     * 记录ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * 操作时间.
     */
    private Timestamp opertime;

    /**
     * 操作者.
     */
    private String user;

    /**
     * 行为.
     */
    @Column(name = "action", length = 4000)
    private String action;
}
