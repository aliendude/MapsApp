package com.example.pedro.endogen;

public class Message {

    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_LOG = 1;
    public static final int TYPE_ACTION = 2;

    private int mType;
    private String mMessage;
    private String mUsername;
    private String mTimeCreated;

    private Message() {}

    public int getType() {
        return mType;
    };

    public String getmTimeCreated() {
        return mTimeCreated;
    }

    public void setmTimeCreated(String mTimeCreated) {
        this.mTimeCreated = mTimeCreated;
    }
    public String getMessage() {
        return mMessage;
    };

    public String getUsername() {
        return mUsername;
    };


    public static class Builder {
        private final int mType;
        private String mUsername;
        private String mMessage;
        private String mTimeCreated;

        public Builder(int type) {
            mType = type;
        }

        public Builder username(String username) {
            mUsername = username;
            return this;
        }

        public Builder message(String message) {
            mMessage = message;
            return this;
        }
        public Builder timecreated(String timecreated){
            mTimeCreated = timecreated;
            return this;
        }
        public Message build() {
            Message message = new Message();
            message.mType = mType;
            message.mUsername = mUsername;
            message.mMessage = mMessage;
            message.mTimeCreated= mTimeCreated;
            return message;
        }
    }
}
