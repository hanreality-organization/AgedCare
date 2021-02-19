package com.punuo.sip;

import org.zoolu.sip.address.NameAddress;

/**
 * Created by han.chen.
 * Date on 2019/4/23.
 **/
public interface ISipConfig {

    String getServerIp();

    int getUserPort();

    int getDevPort();

    NameAddress getUserServerAddress();

    NameAddress getUserRegisterAddress();

    NameAddress getUserNormalAddress();

    NameAddress getDevServerAddress();

    NameAddress getDevRegisterAddress();

    NameAddress getDevNormalAddress();

    void reset();
}
