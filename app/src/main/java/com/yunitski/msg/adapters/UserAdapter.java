package com.yunitski.msg.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.avatarImageView.setImageResource(currentUser.getAvatarMockupResource());
        holder.userNameTextView.setText(currentUser.getName());
        lastMessage(currentUser.getId(), holder.userLastMessageTextView);

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{

        public ImageView avatarImageView;
        public TextView userNameTextView, userLastMessageTextView;

        public UserViewHolder(@NonNull View itemView, OnUserClickListener listener) {
            super(itemView);

            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            userLastMessageTextView = itemView.findViewById(R.id.userLastMessageTextView);
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
    private void lastMessage(String userId, TextView lastMsg){
        lastMess = "default";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("messages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    MSGmessage message = dataSnapshot.getValue(MSGmessage.class);
                    if (message.getRecipient().equals(firebaseUser.getUid()) && message.getSender().equals(userId) ||
                            message.getRecipient().equals(userId) && message.getSender().equals(firebaseUser.getUid())){
                        lastMess = message.getText();
                    }
                }
                if ("default".equals(lastMess)) {
                    lastMsg.setText("Нет сообщений");
                } else if (lastMess instanceof String){
                    lastMsg.setText(lastMess);
                } else {
                    lastMsg.setText("Фото");
                }
                lastMess = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
