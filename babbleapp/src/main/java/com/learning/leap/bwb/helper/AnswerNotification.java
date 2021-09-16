package com.learning.leap.bwb.helper;

import java.util.Date;



public class AnswerNotification {
    public Date mAnswerTime;
    public int mAnswerBucket;
    private int mAnswerHour;
    private int mAnswerMin;


    public int getAnswerHour() {
        return mAnswerHour;
    }

    public void setAnswerHour(int answerHour) {
        mAnswerHour = answerHour;
    }

    public int getAnswerMin() {
        return mAnswerMin;
    }

    public void setAnswerMin(int answerMin) {
        mAnswerMin = answerMin;
    }


    public void setAnswerBucket(int answerBucket) {
        mAnswerBucket = answerBucket;
    }
}
