package com.punuo.sip.dev.message;

import android.text.TextUtils;

import com.punuo.sip.dev.SipDevManager;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.message.MessageFactory;
import org.zoolu.sip.message.SipMethods;

/**
 * Author chzjy
 * Date 2016/12/19.
 */

public class SipDevMessageFactory extends MessageFactory {
    private static SipURL sipURL;
    private static final String CONTENT_TYPE = "application/xml";
    private static String viaAddress;
    private static int hostPort;
    private static NameAddress contact;

    public static void init() {
        viaAddress = SipDevManager.getInstance().getViaAddress();
        hostPort = SipDevManager.getInstance().getPort();
        sipURL = new SipURL(viaAddress, hostPort);
        contact = new NameAddress(new SipURL(viaAddress, hostPort));
    }

    public static Message createRegisterRequest(NameAddress to, NameAddress from, String body) {
        Message message = createRegisterRequest(SipDevManager.getInstance(), sipURL, to, from, contact);
        if (!TextUtils.isEmpty(body)) {
            message.setBody(CONTENT_TYPE, body);
        }
        return message;
    }

    public static Message createNotifyRequest(NameAddress to, NameAddress from, String body) {
        Message msg = createRequest(SipDevManager.getInstance(), SipMethods.NOTIFY, sipURL,
                to, from, contact, null);
        msg.setBody(CONTENT_TYPE, body);
        return msg;
    }

    public static Message createSubscribeRequest(NameAddress to, NameAddress from, String body) {
        Message message = createRequest(SipDevManager.getInstance(), SipMethods.SUBSCRIBE, sipURL,
                to, from, contact, null);
        if (!TextUtils.isEmpty(body)) {
            message.setBody(CONTENT_TYPE, body);
        }
        return message;
    }

    public static Message createOptionsRequest(NameAddress to, NameAddress from, String body) {
        Message msg = createRequest(SipDevManager.getInstance(), SipMethods.OPTIONS, sipURL,
                to, from, contact, null);
        if (!TextUtils.isEmpty(body)) {
            msg.setBody(CONTENT_TYPE, body);
        }
        return msg;
    }

    public static Message createInviteRequest(NameAddress to, NameAddress from, String body) {
        Message msg = createInviteRequest(SipDevManager.getInstance(), sipURL, to, from, contact, null);
        if (!TextUtils.isEmpty(body)) {
            msg.setBody(CONTENT_TYPE, body);
        }
        return msg;
    }

    public static Message createByeRequest(NameAddress to, NameAddress from) {
        return createRequest(SipDevManager.getInstance(), SipMethods.BYE, sipURL, to, from, contact, null);
    }

    public static Message createResponse(Message req, int code, String reason, String body) {
        if (TextUtils.isEmpty(body)) {
            body = "";
        }
        return createResponse(req, code, reason, null, null, CONTENT_TYPE, body);
    }
}
