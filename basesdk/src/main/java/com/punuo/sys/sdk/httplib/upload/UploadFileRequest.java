package com.punuo.sys.sdk.httplib.upload;


import com.punuo.sys.sdk.httplib.BaseRequest;

import okhttp3.MediaType;

/**
 * Created by han.chen.
 * Date on 2019/5/27.
 **/
public class UploadFileRequest extends BaseRequest<UploadResult> {

    public UploadFileRequest() {
        setRequestType(RequestType.UPLOAD);
        setRequestPath("/xiaoyupeihu/public/index.php/users/updateUserPic");
        contentType(MediaType.parse("image/*"));
    }
}
