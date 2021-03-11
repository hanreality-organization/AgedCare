package com.punuo.sip;

import com.aill.androidserialport.SerialPort;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by han.chen.
 * Date on 2021/2/23.
 **/
public class DirectionControlUtil {
    private FileOutputStream mOutputStream;

    public DirectionControlUtil() {
        try {
            SerialPort serialPort = new SerialPort(new File("/dev/" + "ttyMT1"), 2400, 0);
            mOutputStream = (FileOutputStream) serialPort.getOutputStream();
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public void sendData(byte[] writeBytes) {
        if (mOutputStream != null) {
            try {
                mOutputStream.write(writeBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
