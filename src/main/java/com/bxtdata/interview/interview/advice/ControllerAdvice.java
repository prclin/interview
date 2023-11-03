package com.bxtdata.interview.interview.advice;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler({RuntimeException.class})
    public Map<String,String> exceptionHandler(RuntimeException e){
        e.printStackTrace();
        HashMap<String, String> map = new HashMap<>();
        map.put("msg",e.getMessage());
        return map;
    }

}
