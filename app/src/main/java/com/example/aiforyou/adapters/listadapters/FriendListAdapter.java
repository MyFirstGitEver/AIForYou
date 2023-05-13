package com.example.aiforyou.adapters.listadapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aiforyou.R;
import com.example.aiforyou.activities.MainActivity;
import com.example.aiforyou.custom.ShareDTO;
import com.example.aiforyou.custom.WorkBoxDTO;

import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendBoxViewHolder> {
    private List<ShareDTO> boxes;
    private final MainActivity.OnFriendBoxListener onFriendBoxListener;
    public FriendListAdapter(List<ShareDTO> boxes, MainActivity.OnFriendBoxListener onFriendBoxListener) {
        this.boxes = boxes;
        this.onFriendBoxListener = onFriendBoxListener;
    }

    @NonNull
    @Override
    public FriendBoxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendBoxViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.a_friend_box, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FriendBoxViewHolder holder, int position) {
        holder.bind(boxes.get(position));
    }

    @Override
    public int getItemCount() {
        return boxes.size();
    }

    public void addNewShare(ShareDTO share) {
        for(int i=0;i<boxes.size();i++) {
            ShareDTO loadedShare = boxes.get(i);

            if(loadedShare.getTenNguoiGui().equals(share.getTenNguoiGui())) {
                loadedShare.setTenProject(share.getTenProject());
                loadedShare.setIdProject(share.getIdProject());
                notifyItemChanged(i);

                boxes.remove(i);
                boxes.add(0, loadedShare);

                notifyItemMoved(i, 0);
                return;
            }
        }

        boxes.add(0, share);
        notifyItemInserted(0);
    }

    class FriendBoxViewHolder extends RecyclerView.ViewHolder {
        private final ImageView userImg;
        private final TextView userNameTxt, sharedWorkTxt;
        private ConstraintLayout container;

        public FriendBoxViewHolder(@NonNull View itemView) {
            super(itemView);

            userImg = itemView.findViewById(R.id.userImg);

            userNameTxt = itemView.findViewById(R.id.userNameTxt);
            sharedWorkTxt = itemView.findViewById(R.id.sharedWorkTxt);

            container = itemView.findViewById(R.id.container);
        }

        void bind(ShareDTO share) {
            Glide.with(itemView.getContext()).load(share.getAnhNguoiGui()).into(userImg);

            userNameTxt.setText(share.getTenNguoiGui());
            sharedWorkTxt.setText(String.format("shared %s", share.getTenProject()));

            container.setOnClickListener(v -> onFriendBoxListener.onFriendBox(boxes.get(getBindingAdapterPosition())));
        }
    }
}
