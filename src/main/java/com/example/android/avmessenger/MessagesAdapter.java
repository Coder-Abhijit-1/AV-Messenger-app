package com.example.android.avmessenger;

import static com.example.android.avmessenger.ChatActivity.receiverIImage;
import static com.example.android.avmessenger.ChatActivity.senderImage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<MessageModelClass> messageAdapterArrayList;
    int ITEM_SEND = 1;
    int ITEM_RECEIVE = 2;

    public MessagesAdapter(Context context, ArrayList<MessageModelClass> messageAdapterArrayList) {
        this.context = context;
        this.messageAdapterArrayList = messageAdapterArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout,parent,false);
            return new senderViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_layout,parent,false);
            return new receiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModelClass messageModelClass = messageAdapterArrayList.get(position);

        if(holder.getClass() == senderViewHolder.class){
            senderViewHolder viewHolder = (senderViewHolder) holder;
            viewHolder.messageText.setText(messageModelClass.getMessage());
            Picasso.get().load(senderImage).into(viewHolder.shapeableImageView);
        }else{
            receiverViewHolder viewHolder = (receiverViewHolder) holder;
            viewHolder.messageText.setText(messageModelClass.getMessage());
            Picasso.get().load(receiverIImage).into(viewHolder.shapeableImageView);
        }

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        MessageModelClass messageModelClass = messageAdapterArrayList.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messageModelClass.getSenderID())){
            return ITEM_SEND;
        }else{
            return ITEM_RECEIVE;
        }
    }

    class senderViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView shapeableImageView;
        TextView messageText ;
        public senderViewHolder(@NonNull View itemView) {
            super(itemView);
            shapeableImageView = itemView.findViewById(R.id.profilerggg);
            messageText = itemView.findViewById(R.id.msgsendertyp);
        }
    }

    class receiverViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView shapeableImageView;
        TextView messageText ;
        public receiverViewHolder(@NonNull View itemView) {
            super(itemView);
            shapeableImageView = itemView.findViewById(R.id.pro);
            messageText = itemView.findViewById(R.id.recivertextset);
        }
    }

}
