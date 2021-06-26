 package com.yunitski.msg.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yunitski.msg.R;
import com.yunitski.msg.data.MSGmessage;
import com.yunitski.msg.data.User;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private ArrayList<User> users;

    private OnUserClickListener listener;

    private String lastMess;

    private String lastMessageTime;

    private boolean showIcon;

    private boolean isMy;

    private boolean video;
    private boolean photo;


    public interface OnUserClickListener{
        void onUserClick(int position);
    }

    public void setOnUserClickListener(OnUserClickListener listener){
        this.listener = listener;
    }

    public UserAdapter(ArrayList<User> users){
        this.users = users;
    }
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        UserViewHolder viewHolder = new UserViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        User currentUser = users.get(position);

        //holder.avatarImageView.setImageResource(currentUser.getAvatarMockupResource());
        Glide.with(holder.avatarImageView).load(currentUser.getProfilePhotoUrl()).into(holder.avatarImageView);
        holder.userNameTextView.setText(currentUser.getName());
        lastMessage(currentUser.getId(), holder.userLastMessageTextView, holder.messagesToRearImageView, holder.lastMessageTimeTextView);

    }


    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{

        public ImageView avatarImageView, messagesToRearImageView;
        public TextView userNameTextView, userLastMessageTextView, lastMessageTimeTextView;

        public UserViewHolder(@NonNull View itemView, OnUserClickListener listener) {
            super(itemView);

            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            userLastMessageTextView = itemView.findViewById(R.id.userLastMessageTextView);
            messagesToRearImageView = itemView.findViewById(R.id.messagesToRearImageView);
            lastMessageTimeTextView = itemView.findViewById(R.id.lastMessageTimeTextView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onUserClick(position);
                        }
                    }
                }
            });
        }
    }

    private void lastMessage(String userId, TextView lastMsg, ImageView imageView, TextView lmTime){
        lastMess = "default";
        showIcon = false;
        isMy = false;
        lastMessageTime = "";
        video = false;
        photo = false;
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("messages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    MSGmessage message = dataSnapshot.getValue(MSGmessage.class);
                    if (message.getRecipient().equals(firebaseUser.getUid()) && message.getSender().equals(userId) && !message.isDeleted() ||
                            message.getRecipient().equals(userId) && message.getSender().equals(firebaseUser.getUid()) && !message.isDeleted()){
                        lastMess = message.getText();

                        showIcon = message.isRead();

                        isMy = message.getRecipient().equals(firebaseUser.getUid()) && message.getSender().equals(userId);

                        lastMessageTime = message.getTime();
                        photo = message.getVideoUrl() == null;
                        video = message.getImageUrl() == null;
                    }
                }
                if ("default".equals(lastMess)) {
                    lastMsg.setText("Нет сообщений");
                    lastMsg.setTextColor(Color.BLACK);
                } else if (lastMess instanceof String){
                    lastMsg.setText(lastMess);
                    lastMsg.setTextColor(Color.BLACK);
                } else if (photo){
                    lastMsg.setText("Фото");
                    lastMsg.setTextColor(Color.BLUE);
                } else if (video){
                    lastMsg.setText("Видео");
                    lastMsg.setTextColor(Color.BLUE);
                }
                if (!showIcon && isMy){
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    imageView.setVisibility(View.INVISIBLE);
                }
                lmTime.setText(lastMessageTime);
                lastMess = "default";
                showIcon = false;
                isMy = false;
                lastMessageTime = "";
                photo = false;
                video = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void sortList(){

    }
}
