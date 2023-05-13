package com.example.aiforyou.adapters.listadapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiforyou.R;
import com.example.aiforyou.custom.ShareDTO;

import java.util.List;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.NotificationViewHolder> {
    private final List<ShareDTO> notification;

    public NotificationListAdapter(List<ShareDTO> notification) {
        this.notification = notification;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.a_notification, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.bind(notification.get(position));
    }

    @Override
    public int getItemCount() {
        return notification.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        private final TextView notificationTxt, dateTxt;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            notificationTxt = itemView.findViewById(R.id.notificationTxt);
            dateTxt = itemView.findViewById(R.id.dateTxt);
        }

        public void bind(ShareDTO share) {
            notificationTxt.setText(String.format("%sby%s", share.getTenProject(), share.getTenNguoiGui()));
            dateTxt.setText(share.getSentDate().toString());
        }
    }
}
