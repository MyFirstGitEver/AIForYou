package com.example.aiforyou.adapters.listadapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiforyou.R;
import com.example.aiforyou.custom.ProjectDTO;
import com.example.aiforyou.custom.ReceiptDTO;
import com.example.aiforyou.fragments.mainfragments.AccountFragment;
import com.example.aiforyou.interfaces.DetailsItem;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

public class DetailsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<DetailsItem> details;
    private final View.OnClickListener seeMoreClickListener;
    private final AccountFragment.OnDeleteProjectListener onDeleteProjectListener;

    public DetailsListAdapter(List<DetailsItem> details,
                              View.OnClickListener seeMoreClickListener,
                              AccountFragment.OnDeleteProjectListener onDeleteProjectListener) {
        this.details = details;
        this.seeMoreClickListener = seeMoreClickListener;
        this.onDeleteProjectListener = onDeleteProjectListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case 0:
                return new ReceiptViewHolder(inflater.inflate(R.layout.a_receipt, parent, false));
            case 1:
                return new ProjectDetailsViewHolder(inflater.inflate(R.layout.a_project_controller, parent, false));
            case 2:
                return new SeeMoreViewHolder(inflater.inflate(R.layout.see_more_layout, parent, false));
            default:
                return new LoadingViewHolder(inflater.inflate(R.layout.a_loading, parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(details.get(position) == null) {
            return -1;
        }
        if(details.get(position) instanceof ReceiptDTO) {
            return 0;
        }
        else if(details.get(position) instanceof ProjectDTO){
            return 1;
        }
        else {
            return 2;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ReceiptViewHolder) {
            ((ReceiptViewHolder)holder).bind((ReceiptDTO) details.get(position));
        }
        else if(holder instanceof ProjectDetailsViewHolder){
            ((ProjectDetailsViewHolder)holder).bind((ProjectDTO) details.get(position));
        }
        else if(holder instanceof SeeMoreViewHolder){
            ((SeeMoreViewHolder)holder).bind();
        }
    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    public void loadProjects(List<ProjectDTO> details) {
        this.details.clear();
        this.details.addAll(details);
        notifyDataSetChanged();
    }

    public void loadReceipts(ReceiptDTO[] receipts) {
        this.details.clear();
        Collections.addAll(this.details, receipts);

        this.details.add(new DetailsItem() {});
        notifyDataSetChanged();
    }

    public void deleteAt(int position) {
        details.remove(position);
        notifyItemRemoved(position);
    }

    public void showLoad() {
        details.clear();
        details.add(null);
        notifyDataSetChanged();
    }

    static class ReceiptViewHolder extends RecyclerView.ViewHolder {
        private final ImageView modelImg, statusImg;
        private final TextView modelTxt, dateTxt;

        public ReceiptViewHolder(@NonNull View itemView) {
            super(itemView);

            modelImg = itemView.findViewById(R.id.modelImg);
            statusImg = itemView.findViewById(R.id.statusImg);

            modelTxt = itemView.findViewById(R.id.modelTxt);
            dateTxt = itemView.findViewById(R.id.dateTxt);
        }

        void bind(ReceiptDTO receipt) {
            if(receipt.isExpired()) {
                statusImg.setImageResource(R.drawable.ic_outdated);
                statusImg.setColorFilter(R.color.red);
            }
            else {
                statusImg.setImageResource(R.drawable.ic_check);
                statusImg.setColorFilter(R.color.blue);
            }

            if(receipt.isPeriodic()) {
                modelImg.setImageResource(R.drawable.ic_rent);
            }
            else if(receipt.getName().equals("Statistics")) {
                modelImg.setImageResource(R.drawable.ic_statistics);
            }
            else {
                modelImg.setImageResource(R.drawable.ic_ai);
            }

            modelTxt.setText(receipt.getName());
            dateTxt.setText(new SimpleDateFormat("dd-MM-yyyy").format(receipt.getBuyDate()));
        }
    }

    class ProjectDetailsViewHolder extends RecyclerView.ViewHolder {
        private final ImageView modelImg;
        private final TextView modelTxt, dateTxt;
        private final ImageButton deleteBtn;

        public ProjectDetailsViewHolder(@NonNull View itemView) {
            super(itemView);

            modelImg = itemView.findViewById(R.id.modelImg);

            modelTxt = itemView.findViewById(R.id.modelTxt);
            dateTxt = itemView.findViewById(R.id.dateTxt);

            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }

        void bind(ProjectDTO project) {
            modelTxt.setText(project.getName());

            if(project.getType() == ProjectDTO.ProjectType.STATS) {
                modelImg.setImageResource(R.drawable.ic_statistics);
            }
            else {
                modelImg.setImageResource(R.drawable.ic_ai);
            }

            dateTxt.setText(new SimpleDateFormat("yyyy-MM-dd").format(project.getDate()));

            deleteBtn.setOnClickListener(v -> onDeleteProjectListener.onDeleteProjectListener(getBindingAdapterPosition()));
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class SeeMoreViewHolder extends RecyclerView.ViewHolder {
        private final Button seeMoreBtn;

        public SeeMoreViewHolder(@NonNull View itemView) {
            super(itemView);

            seeMoreBtn = itemView.findViewById(R.id.seeMoreBtn);
        }

        void bind() {
            seeMoreBtn.setOnClickListener(seeMoreClickListener);
        }
    }
}