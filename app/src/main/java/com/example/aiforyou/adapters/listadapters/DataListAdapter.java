package com.example.aiforyou.adapters.listadapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiforyou.mytools.Vector;

public class DataListAdapter extends RecyclerView.Adapter<DataListAdapter.DataViewHolder> {
    private String[] data;

    public DataListAdapter(String[] data) {
        this.data = data;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return new DataViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        holder.bind(data[position]);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    static class DataViewHolder extends RecyclerView.ViewHolder {
        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void bind(String text) {
            ((TextView)(itemView)).setText(text);
        }
    }
}
