package com.example.aiforyou.adapters.pageradapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiforyou.R;
import com.example.aiforyou.custom.ProjectDTO;
import com.example.aiforyou.dialogs.CreateNewProjectDialog;

import java.util.Date;

public class CreateProjectPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final ProjectDTO form = new ProjectDTO();
    private final CreateNewProjectDialog.OnFillingFormListener onFillingFormListener;

    public CreateProjectPagerAdapter(CreateNewProjectDialog.OnFillingFormListener onFillingFormListener) {
        this.onFillingFormListener = onFillingFormListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0) {
            return new ProjectNameViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.project_name_form, parent, false));
        }
        else {
            return new ProjectTypeViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.project_type_form, parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ProjectNameViewHolder) {
            ((ProjectNameViewHolder)holder).bind();
        }
        else {
            ((ProjectTypeViewHolder)holder).bind();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public class ProjectNameViewHolder extends RecyclerView.ViewHolder {
        private final EditText projectNameEditTxt;
        private final Button okBtn;

        public ProjectNameViewHolder(@NonNull View itemView) {
            super(itemView);

            projectNameEditTxt = itemView.findViewById(R.id.projectNameEditTxt);
            okBtn = itemView.findViewById(R.id.okBtn);
        }

        public void bind() {
            okBtn.setOnClickListener(v -> {
                form.setName(projectNameEditTxt.getText().toString());
                onFillingFormListener.onFillingForm(form, 0);
            });
        }
    }

    public class ProjectTypeViewHolder extends RecyclerView.ViewHolder {
        private final Spinner spinner;
        private final Button okBtn;

        public ProjectTypeViewHolder(@NonNull View itemView) {
            super(itemView);

            spinner = itemView.findViewById(R.id.spinner);
            okBtn = itemView.findViewById(R.id.okBtn);
        }

        public void bind() {
            okBtn.setOnClickListener(v -> {
                form.setType(ProjectDTO.ProjectType.values()[spinner.getSelectedItemPosition()]);
                form.setDate(new Date());
                onFillingFormListener.onFillingForm(form, 1);
            });
        }
    }
}