package com.punuo.sys.app.agedcare.request;

import com.punuo.sys.app.agedcare.db.FamilyMember;
import com.punuo.sys.sdk.httplib.BaseRequest;

import java.util.List;

/**
 * Created by han.chen.
 * Date on 2021/2/22.
 **/
public class GetAddressBookRequest extends BaseRequest<List<FamilyMember>> {

    public GetAddressBookRequest() {
        setRequestPath("/users/getAddressbook");
    }
}
