package com.android.demoapplication.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by deepakrameshrao on 25/07/17.
 */

public class Message implements Parcelable {

    public int id;
    public String message;
    public boolean isFromDoctor;

    public Message() {

    }

    public Message(Parcel in) {
        id = in.readInt();
        message = in.readString();
        isFromDoctor = in.readByte() != 0;
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(message);
        parcel.writeByte((byte) (isFromDoctor ? 1 : 0));
    }
}
