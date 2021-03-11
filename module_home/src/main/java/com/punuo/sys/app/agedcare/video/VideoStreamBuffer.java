package com.punuo.sys.app.agedcare.video;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import jlibrtp.DataFrame;

/**
 * Created by han.chen.
 * Date on 2021/3/11.
 * 乱序重排
 **/
public class VideoStreamBuffer {

    private final LinkedList<DataFrame> mFrameStreamList = new LinkedList<>();

    public void appendDateFrame(DataFrame dataFrame) {
        mFrameStreamList.add(dataFrame);
        Collections.sort(mFrameStreamList, new Comparator<DataFrame>() {
            @Override
            public int compare(DataFrame o1, DataFrame o2) {
                int[] sequenceNumbers1 = o1.sequenceNumbers();
                int[] sequenceNumbers2 = o2.sequenceNumbers();
                if (sequenceNumbers1[0] > sequenceNumbers2[0]) {
                    return 1;
                } else if (sequenceNumbers1[0] < sequenceNumbers2[0]){
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }

    public DataFrame getDataFrame() {
        return mFrameStreamList.pollFirst();
    }

    public boolean isReady() {
        return mFrameStreamList.size() > 10;
    }
    public void clear() {
        mFrameStreamList.clear();
    }
}
