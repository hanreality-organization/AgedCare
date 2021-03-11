package com.punuo.sys.app.agedcare.video;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by han.chen.
 * Date on 2021/3/11.
 **/
public class VideoNalBuffer {
    private ArrayBlockingQueue<NalBuffer> nalBufferQueue = new ArrayBlockingQueue<>(10);

    public boolean offerData(byte[] nal, int length) {
        return nalBufferQueue.offer(new NalBuffer(nal, length));
    }

    public NalBuffer pollData() {
        return nalBufferQueue.poll();
    }

    public void clear() {
        nalBufferQueue.clear();
    }

    public static class NalBuffer {
        public byte[] nal;
        public int nalLength;

        public NalBuffer(byte[] nal, int nalLength) {
            this.nal = nal;
            this.nalLength = nalLength;
        }
    }
}
