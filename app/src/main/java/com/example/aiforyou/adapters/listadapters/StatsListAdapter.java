package com.example.aiforyou.adapters.listadapters;

import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiforyou.R;
import com.example.aiforyou.mytools.statisticscanvas.ChartManager;
import com.example.aiforyou.mytools.statisticscanvas.stats.CategoryQuantifier;
import com.example.aiforyou.mytools.statisticscanvas.stats.SegmentQuantifier;
import com.example.aiforyou.fragments.workspaces.StatisticsFragment;

public class StatsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public enum StatsType {
        PIE,
        HIST1
    }

    private final Object[] stats;
    private final StatsType type;
    private StatisticsFragment.OnChangingRangeDisplayListener onChangingRangeDisplayListener;

    public StatsListAdapter(Object[] stats, StatsType type) {
        this.stats = stats;
        this.type = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == StatsType.PIE.ordinal()) {
            return new PieStatsViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.a_pie_stats, parent, false));
        }
        else {
            return new Hist1StatsViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.a_hist1_stats, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof PieStatsViewHolder) {
            ((PieStatsViewHolder) holder).bind(stats[position], position);
        }
        else {
            ((Hist1StatsViewHolder) holder).bind((int) stats[position]);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return type.ordinal();
    }

    @Override
    public int getItemCount() {
        return stats.length;
    }

    public StatsListAdapter setOnChangingRangeDisplayListener(
            StatisticsFragment.OnChangingRangeDisplayListener onChangingRangeDisplayListener) {
        this.onChangingRangeDisplayListener = onChangingRangeDisplayListener;

        return this;
    }

    static class PieStatsViewHolder extends RecyclerView.ViewHolder {
        private final ImageView rectImg;
        private final TextView categoryTxt;

        public PieStatsViewHolder(@NonNull View itemView) {
            super(itemView);

            rectImg = itemView.findViewById(R.id.rectImg);
            categoryTxt = itemView.findViewById(R.id.categoryTxt);
        }

        public void bind(Object item, int position) {
            if(item instanceof CategoryQuantifier) {
                CategoryQuantifier q = (CategoryQuantifier) item;

                categoryTxt.setText(String.format("%s(%s %%)", q.categoryName, q.value * 100));
                rectImg.setColorFilter(ChartManager.colors[position]);
            }
            else if(item instanceof String){
                String title = (String) item;

                categoryTxt.setText(String.format("%s", title));
                rectImg.setColorFilter(ChartManager.colors[position]);
            }
            else {
                SegmentQuantifier quantifier = (SegmentQuantifier) item;

                StringBuilder builder = new StringBuilder();
                builder.append(quantifier.getCategoryName());
                builder.append("(");

                CategoryQuantifier[] categoryQuantifiers = quantifier.getQuantifiers();
                for (int i=0;i<categoryQuantifiers.length;i++) {
                    CategoryQuantifier categoryQuantifier = categoryQuantifiers[i];

                    builder.append(categoryQuantifier.categoryName);
                    builder.append("-");

                    builder.append("<font color=")
                            .append(hexString(ChartManager.colors[i]))
                            .append(">")
                            .append(categoryQuantifier.value).append("</font>").append(", ");
                }

                builder.append(")");

                categoryTxt.setText(Html.fromHtml(builder.toString(), Html.FROM_HTML_MODE_LEGACY));
                rectImg.setColorFilter(Color.GRAY);
            }
        }

        private String hexString(int color) {
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);

            return String.format("#%02x%02x%02x", r, g, b);
        }
    }

    class Hist1StatsViewHolder extends RecyclerView.ViewHolder {
        private final TextView confirmTxt;
        private final SeekBar nBar;

        private final SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                stats[0] = (int) (5 + 5 * ((float) seekBar.getProgress() / 100));
                notifyItemChanged(0);

                onChangingRangeDisplayListener.onChangeRangeDisplay((int) stats[0]);
            }
        };

        public Hist1StatsViewHolder(@NonNull View itemView) {
            super(itemView);

            confirmTxt = itemView.findViewById(R.id.confirmTxt);
            nBar = itemView.findViewById(R.id.nBar);
        }

        void bind(int n) {
            confirmTxt.setText(String.format("Currently drawn on %d ranges", Math.abs(n)));
            nBar.setOnSeekBarChangeListener(listener);

            nBar.setProgress(Math.round((float)(Math.abs(n) - 5) / 5 * 100));
        }
    }
}