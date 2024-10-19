package com.example.multitreadreadfile.Error;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;

@Data
@AllArgsConstructor
//@NoArgsConstructor
public class ErrorResponse {
    public static long counter=0;
    @JsonProperty("FILE-NAME")
    private  String fileName;
    @JsonProperty("RECORD-NUMBER")
    private String recordNumber;
    @JsonProperty("ERROR-CODE")
    private long code;
    @JsonProperty("ERROR-CLASSIFICATION-NAME")
    private String errorCategory;
    @JsonProperty("ERROR-DESCRIPTION")
    private String description;
    @JsonProperty("ERROR-DATE")
    private Date errorDate;


    public ErrorResponse(){
        this.counter=++counter;
    }

}
