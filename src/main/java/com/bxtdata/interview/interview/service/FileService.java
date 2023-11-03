package com.bxtdata.interview.interview.service;

import com.bxtdata.interview.interview.pojo.body.UploadingBody;

public interface FileService {

    String saveBase64Image(UploadingBody body);

}
