package com.yunitski.msg.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yunitski.msg.R;
import com.yunitski.msg.data.FileInProfile;

import java.util.ArrayList;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    private final List<FileInProfile> fileInProfileList;

    private OnFileClickListener listener;



    public interface OnFileClickListener{
        void onFileClick(int position);
    }

    public void setOnFileClickListener(OnFileClickListener listener){
        this.listener = listener;
    }

    public FileAdapter(ArrayList<FileInProfile> fileInProfileList) {
        this.fileInProfileList = fileInProfileList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_in_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileAdapter.ViewHolder holder, int position) {
        FileInProfile fileInProfile = fileInProfileList.get(position);
        holder.fileNameTextView.setText(fileInProfile.getFileName());
    }

    @Override
    public int getItemCount() {
        return fileInProfileList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView fileImageView;
        TextView fileNameTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fileImageView = itemView.findViewById(R.id.fileImageView);
            fileNameTextView = itemView.findViewById(R.id.fileNameTextView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onFileClick(position);
                        }
                    }
                }
            });
        }
    }
}
