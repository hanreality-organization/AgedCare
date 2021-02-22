package com.punuo.sip.dev;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.punuo.sip.SipConfig;
import com.punuo.sip.dev.request.BaseDevSipRequest;
import com.punuo.sip.dev.service.SipDevServiceManager;
import com.punuo.sip.user.service.SipUserServiceManager;

import org.zoolu.sip.message.Message;
import org.zoolu.sip.message.SipMethods;
import org.zoolu.sip.provider.SipProvider;
import org.zoolu.sip.provider.Transport;
import org.zoolu.sip.provider.TransportConnId;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

/**
 * Created by han.chen.
 * Date on 2019-08-12.
 **/
public class SipDevManager extends SipProvider {
    private static final String TAG = "SipDevManager";
    private static String[] PROTOCOLS = {"udp"};
    private static Context sContext;
    private ExecutorService mExecutorService;
    private static volatile SipDevManager sSipDevManager;
    private static HashMap<String, BaseDevSipRequest> mRequestMap;

    public static SipDevManager getInstance() {
        if (sContext == null) {
            throw new RuntimeException("context is null, please set context");
        }
        if (sSipDevManager == null) {
            synchronized (SipDevManager.class) {
                if (sSipDevManager == null) {
                    int hostPort = new Random().nextInt(5000) + 2000;
                    sSipDevManager = new SipDevManager(hostPort);
                }
            }
        }
        return sSipDevManager;
    }

    public static void setContext(Context context) {
        sContext = context.getApplicationContext();
        mRequestMap = new HashMap<>();
    }

    private SipDevManager(int host_port) {
        super(null, host_port, PROTOCOLS, null);
        mExecutorService = Executors.newFixedThreadPool(3);
    }

    public void addRequest(BaseDevSipRequest sipRequest) {
        if (sipRequest == null) {
            return;
        }
        Message message = sipRequest.build();
        if (message != null) {
            sendMessage(message);
            if (sipRequest.hasResponse()) {
                mRequestMap.put(sipRequest.getTargetResponse(), sipRequest);
                sipRequest.startCounting();
            }
        } else {
            Log.w(TAG, "build message is null");
        }
    }

    @Override
    public TransportConnId sendMessage(Message msg) {
        return sendMessage(msg, SipConfig.getServerIp(), SipConfig.getDevPort());
    }

    public TransportConnId sendMessage(final Message msg, final String destAddr, final int destPort) {
        Log.v(TAG, "<----------send sip message---------->");
        Log.v(TAG, msg.toString());
        TransportConnId id = null;
        try {
            id = mExecutorService.submit(new Callable<TransportConnId>() {
                @Override
                public TransportConnId call() throws Exception {
                    return sendMessage(msg, "udp", destAddr, destPort, 0);
                }
            }).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return id;
    }

    @Override
    public synchronized void onReceivedMessage(Transport transport, Message msg) {
        Log.v(TAG, "<----------received sip message---------->");
        Log.v(TAG, msg.toString());
        try {
            handleMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
            SipUserServiceManager.getInstance().handleRequest("error", "{}", msg);
        }
    }

    private void handleMessage(Message message) {
        if (message.isResponse()) {
            int code = message.getStatusLine().getCode();
            switch (code) {
                case 200:
                    parseMessage(message);
                    break;
                default:
                    SipDevServiceManager.getInstance().handleRequest("error", "{}", message);
                    break;
            }
        } else if (message.isNotify()) {
            parseMessage(message);
        } else if (message.isRequest()) {
            parseMessage(message);
        } else {
            SipDevServiceManager.getInstance().handleRequest("error", "{}", message);
        }
    }

    private void parseMessage(Message message) {
        String body = message.getBody();
        if (!TextUtils.isEmpty(body)) {
            XmlToJson xmlToJson = new XmlToJson.Builder(body).build();
            String parse = xmlToJson.toString();
            Log.d("SipRequest", "deliverResponse: \n" + parse);
            JsonElement data = null;
            try {
                data = new JsonParser().parse(parse);
                handle(message, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String method = message.getMethodId().toString();
            switch (method) { //不需要解析的可以加这里
                case SipMethods.ACK:
                case SipMethods.BYE:
                    return;
                default:
                    SipDevServiceManager.getInstance().handleRequest(method.toLowerCase(), "{}", message);
                    return;
            }
        }
    }

    private void handle(Message message, JsonElement data) {
        if (data == null) {
            return;
        }
        if (data.isJsonObject()) {
            JsonObject jsonObject = data.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
            Iterator iterator = entrySet.iterator();
            if (iterator.hasNext()) {
                Map.Entry<String, JsonElement> next = (Map.Entry<String, JsonElement>) iterator.next();
                SipDevServiceManager.getInstance().handleRequest(next.getKey(), next.getValue().toString(), message);
                BaseDevSipRequest sipRequest = mRequestMap.get(next.getKey());
                if (sipRequest != null) {
                    sipRequest.response();
                    mRequestMap.remove(next.getKey());
                }
            }
        }
    }
}
