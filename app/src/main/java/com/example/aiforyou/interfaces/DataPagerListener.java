package com.example.aiforyou.interfaces;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiforyou.fragments.workspaces.WorkSpaceFragment;

public class DataPagerListener extends RecyclerView.OnScrollListener {
    private final WorkSpaceFragment.QueryBarListener listener;

    public DataPagerListener(WorkSpaceFragment.QueryBarListener listener) {
        this.listener = listener;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if(dy < -30) {
            listener.showQueryBar(false);
        }
        else if(dy > 30){
            listener.showQueryBar(true);
        }
    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();

        int firstVisible = manager.findFirstCompletelyVisibleItemPosition();
        int visibleCount = manager.getChildCount();
        int totalCount = manager.getItemCount();

        if(firstVisible + visibleCount >= totalCount && firstVisible == 0) {
            listener.showQueryBar(false);
        }
        else if(firstVisible + visibleCount >= totalCount) {
            listener.showQueryBar(false);
        }
        else if(firstVisible == 0) {
            listener.showQueryBar(true);
        }
    }
}