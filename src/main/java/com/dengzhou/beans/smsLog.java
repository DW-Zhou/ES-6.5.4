package com.dengzhou.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class smsLog {
    @JsonIgnore
    private Integer id;
    private Date createDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date sendDate;
    private String longCode;
    private String mobile;
    private String corpName;
    private String smsContent;
    private Integer state;
    private Integer operatorid;
    private String province;
    private String ipAdrr;
    private Integer replyTotal;
    private Integer fee;
}
