package com.bxtdata.interview.interview.task1.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Datum1.class, name = "1"),
        @JsonSubTypes.Type(value = Datum103.class, name = "103"),
        @JsonSubTypes.Type(value = Datum104.class, name = "104"),
        @JsonSubTypes.Type(value = Datum104.class, name = "105"),
})
public class BaseDatum {

}
