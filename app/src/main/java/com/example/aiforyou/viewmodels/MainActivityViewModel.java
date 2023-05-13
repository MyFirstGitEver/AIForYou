package com.example.aiforyou.viewmodels;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

import com.example.aiforyou.custom.ProjectDTO;
import com.example.aiforyou.custom.ShareDTO;
import com.example.aiforyou.custom.UserDTO;
import com.example.aiforyou.entities.ProjectEntity;

import java.util.List;

public class MainActivityViewModel extends ViewModel {
    private UserDTO user;
    private List<ShareDTO> shares = null;
    private List<ShareDTO> notifcations = null;

    public UserDTO getUser() {
        return user;
    }

    public List<ShareDTO> getShares() {
        return shares;
    }

    public List<ShareDTO> getNotifcations() {
        return notifcations;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public ProjectDTO getProject(int id) {
        return user.getProjects().get(id);
    }

    public void saveUriAt(int position, Uri uri) {
        getProject(position).setUri(uri);
    }

    public void save(int position, ProjectEntity project) {
        getProject(position).setId(project.getId());
        getProject(position).setDiaChiExcel(project.getDiaChiExcel());
    }

    public void addNewProject(ProjectDTO project) {
        user.getProjects().add(project);
    }

    public ProjectDTO deleteAt(int position) {
        ProjectDTO deleted = user.getProjects().get(position);
        user.getProjects().remove(position);

        return deleted;
    }

    public int registerShared(ProjectDTO shared) {
        List<ProjectDTO> projects = user.getProjects();

        for(int i=0;i<projects.size();i++) {
            if(projects.get(i).getId().equals(shared.getId())) {
                return i;
            }
        }

        projects.add(shared);

        return projects.size() - 1;
    }

    public void addNewNotification(ShareDTO newShare) {
        notifcations.add(0, newShare);
    }

    public void setShares(List<ShareDTO> shares) {
        this.shares = shares;
    }

    public void setNotifcations(List<ShareDTO> notifcations) {
        this.notifcations = notifcations;
    }
}