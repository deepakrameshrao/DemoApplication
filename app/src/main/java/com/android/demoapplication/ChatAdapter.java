package com.android.demoapplication;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.demoapplication.entity.Message;

import java.util.ArrayList;

/**
 * Created by deepakrameshrao on 25/07/17.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    public static final int TYPE_DOCTOR = 1;
    public static final int TYPE_PATIENT = 2;


    public ArrayList<Message> messageArrayList;

    public ChatAdapter(ArrayList<Message> messageList) {
        messageArrayList = new ArrayList<>();
        messageArrayList.addAll(messageList);
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageArrayList.get(position);
        return message.isFromDoctor ? TYPE_DOCTOR : TYPE_PATIENT;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View chatView;
        if(viewType == TYPE_DOCTOR) {
            chatView =
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_in,
                            parent, false);
        } else {
            chatView =
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_out,
                            parent, false);
        }
        return new ViewHolder(chatView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String text = messageArrayList.get(position).message;
        holder.setMessage(text);
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView chatTextTv;

        public ViewHolder(View itemView) {
            super(itemView);
            chatTextTv = itemView.findViewById(R.id.textView);
        }

        public void setMessage(String text) {
            chatTextTv.setText(text);
        }
    }

    public void setMessages(ArrayList<Message> messageList) {
        messageArrayList = messageList;
    }
}
