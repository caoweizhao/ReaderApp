package com.example.caoweizhao.readerapp.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by caoweizhao on 2018-1-24.
 */

public class OCR_result {

    /**
     * log_id : 2471272194
     * words_result_num : 2
     * words_result : [{"words":" TSINGTAO"},{"words":"青島睥酒"}]
     */

    @SerializedName("log_id")
    private long logId;
    @SerializedName("words_result_num")
    private int wordsResultNum;
    @SerializedName("words_result")
    private List<WordsResultBean> wordsResult;

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public int getWordsResultNum() {
        return wordsResultNum;
    }

    public void setWordsResultNum(int wordsResultNum) {
        this.wordsResultNum = wordsResultNum;
    }

    public List<WordsResultBean> getWordsResult() {
        return wordsResult;
    }

    public void setWordsResult(List<WordsResultBean> wordsResult) {
        this.wordsResult = wordsResult;
    }

    public static class WordsResultBean {
        /**
         * words :  TSINGTAO
         */

        @SerializedName("words")
        private String words;

        public String getWords() {
            return words;
        }

        public void setWords(String words) {
            this.words = words;
        }
    }
}
