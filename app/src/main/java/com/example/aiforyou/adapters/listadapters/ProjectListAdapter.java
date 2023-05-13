package com.example.aiforyou.adapters.listadapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiforyou.R;
import com.example.aiforyou.custom.ProjectDTO;

import com.example.aiforyou.fragments.mainfragments.ProjectFragment;

import java.util.List;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ProjectViewHolder> {
    private final List<ProjectDTO> projects;
    private final ProjectFragment.ProjectClickListener projectClickListener;

    public ProjectListAdapter(List<ProjectDTO> projects, ProjectFragment.ProjectClickListener projectClickListener) {
        this.projects = projects;
        this.projectClickListener = projectClickListener;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProjectViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.a_project, parent, false));
    }

    public void addNewProject() {
        notifyItemInserted(projects.size());
    }

    public void deleteAt(int position) {
        notifyItemRemoved(position);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        if(position == 0) {
            holder.bind(null);
        }
        else {
            holder.bind(projects.get(position - 1));
        }
    }

    @Override
    public int getItemCount() {
        return projects.size() + 1;
    }

    public void saveUriAt(int position, Uri uri) {
        projects.get(position).setUri(uri);
    }

    public class ProjectViewHolder extends RecyclerView.ViewHolder {
        private final ImageView categoryImg, editOrAddImg;
        private final TextView toolNameTxt, dateTxt;
        private final ConstraintLayout container;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryImg = itemView.findViewById(R.id.categoryImg);
            editOrAddImg = itemView.findViewById(R.id.editOrAddImg);

            toolNameTxt = itemView.findViewById(R.id.toolNameTxt);
            dateTxt = itemView.findViewById(R.id.dateTxt);

            container = itemView.findViewById(R.id.container);
        }

        void bind(ProjectDTO project) {
            container.setOnClickListener((view) -> {
                int position = getBindingAdapterPosition();

                if(position == 0) {
                    projectClickListener.projectClick(null, getItemCount() - 1);
                }
                else {
                    projectClickListener.projectClick(projects.get(position - 1), position - 1);
                }
            });

            if(project == null) {
                editOrAddImg.setImageResource(R.drawable.ic_add);
                categoryImg.setImageResource(R.drawable.ic_create_project);

                dateTxt.setVisibility(View.GONE);
                toolNameTxt.setText(R.string.create_new_project_text);

                return;
            }

            dateTxt.setVisibility(View.VISIBLE);
            dateTxt.setText(project.getDate().toString());

            if(project.getSharedByName() != null) {
                toolNameTxt.setText(String.format("%s(shared by %s)", project.getName(), project.getSharedByName()));
            }
            else {
                toolNameTxt.setText(project.getName());
            }

            editOrAddImg.setImageResource(R.drawable.ic_edit);
            if(project.getType() == ProjectDTO.ProjectType.STATS) {
                categoryImg.setImageResource(R.drawable.ic_statistics);
            }
            else {
                categoryImg.setImageResource(R.drawable.ic_ai);
            }
        }
    }
}