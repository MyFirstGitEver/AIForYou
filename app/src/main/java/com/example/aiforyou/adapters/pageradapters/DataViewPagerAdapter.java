package com.example.aiforyou.adapters.pageradapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiforyou.mytools.statisticscanvas.ExcelReader;
import com.example.aiforyou.adapters.listadapters.DataListAdapter;
import com.example.aiforyou.fragments.workspaces.WorkSpaceFragment;
import com.example.aiforyou.interfaces.DataPagerListener;

public class DataViewPagerAdapter extends RecyclerView.Adapter<DataViewPagerAdapter.ColumnDataViewHolder> {
    private final int colCount;
    private final ExcelReader reader;
    private String currentQuery;
    private final DataPagerListener dataPagerListener;
    private final WorkSpaceFragment.QueryBarListener queryBarListener;

    public DataViewPagerAdapter(
            ExcelReader reader,
            WorkSpaceFragment.QueryBarListener listener,
            String currentQuery,
            int colCount) {
        this.reader = reader;
        this.colCount = colCount;
        this.currentQuery = currentQuery;

        dataPagerListener = new DataPagerListener(listener);
        this.queryBarListener = listener;
    }

    @NonNull
    @Override
    public DataViewPagerAdapter.ColumnDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView dataList = new RecyclerView(parent.getContext());
        dataList.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return new ColumnDataViewHolder(dataList);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewPagerAdapter.ColumnDataViewHolder holder, int position) {
        try {
            holder.bind(position);
        } catch (Exception e) {
            currentQuery = "";

            try {
                holder.bind(position);
            } catch (Exception ex) {
                queryBarListener.invalidQuery();
            }
        }
    }

    @Override
    public int getItemCount() {
        return colCount;
    }

    class ColumnDataViewHolder extends RecyclerView.ViewHolder {
        public ColumnDataViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void bind(int position) throws Exception {
            ((RecyclerView) itemView).setLayoutManager(new LinearLayoutManager(itemView.getContext()));


            String[] data = reader.query(0, currentQuery, reader.getColName(0, position));

            if(data.length == 0) {
                throw new Exception();
            }

            ((RecyclerView) itemView).setAdapter(new DataListAdapter(data));
            ((RecyclerView) itemView).addOnScrollListener(dataPagerListener);
        }
    }

    public void query(String query) {
        currentQuery = query;

        for(int i=0;i<colCount;i++) {
            notifyItemChanged(i); // apply this query globally
        }
    }

    public String getCurrentQuery() {
        return currentQuery;
    }
}