package com.punuo.sys.app.agedcare.video;

import android.util.Log;

/**
 * Created by 23578 on 2019/7/31.
 */

public class LinkedNode {
    public static LinkedNode head = new LinkedNode(new StreamBufNode(-1));
    private LinkedNode next;
    private StreamBufNode streamBufNode;

    private LinkedNode() {
    }

    public LinkedNode(StreamBufNode streamBufNode) {
        this.streamBufNode = streamBufNode;
    }

    public LinkedNode getNext() {
        return next;
    }

    public void setNext(LinkedNode next) {
        this.next = next;
    }

    public StreamBufNode getStreamBufNode() {
        return streamBufNode;
    }

    public int getSize(){
        int len =0;
        LinkedNode temp = head;
        while (temp.getNext()!=null){
            len++;
            temp=temp.getNext();
        }
        return  len;
    }

    public void addBySeqNum(StreamBufNode streamBufNode){
        //Log.e("rtp", "addBySeqNum");
        LinkedNode temp = head;
        int seq = streamBufNode.getSeqNum();
        LinkedNode node = new LinkedNode(streamBufNode);
        if(head.getNext()==null){
           // Log.e("rtp", "addBySeqNum 1");
            head.setNext(node);
            return;
        }
        while (temp.getNext()!=null){
            if(temp.getStreamBufNode().getSeqNum() < seq){
                if (temp.getNext().getStreamBufNode().getSeqNum() > seq) {
                    break;
                }
            }
            temp = temp.getNext();
        }
      //  Log.e("rtp", "addBySeqNum 3");
        node.setNext(temp.getNext());
        temp.setNext(node);
    }
}
