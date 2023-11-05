package com.bxtdata.interview.interview.controller;

import com.bxtdata.interview.interview.pojo.body.UploadingBody;
import com.bxtdata.interview.interview.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Validated
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/uploading")
    public Map<String, Object> upload(@RequestBody @Validated UploadingBody body) {
        HashMap<String, Object> map = new HashMap<>();
        String path = fileService.saveBase64Image(body);
        map.put("url", path);
        map.put("status", path.isEmpty());
        return map;
    }

}
