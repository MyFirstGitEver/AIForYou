package com.example.aiforyou.viewmodels;

import androidx.lifecycle.ViewModel;

import com.example.aiforyou.custom.ServiceDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServiceFragmentViewModel extends ViewModel {
    private List<ServiceDTO> services = null;
    private ServiceDTO consideringServiceHolder = null;

    private int lastClickedService = 0;

    public List<ServiceDTO> getServices() {
        return services;
    }

    public ServiceDTO getConsideringServiceHolder() {
        return consideringServiceHolder;
    }

    public int getLastClickedService() {
        return lastClickedService;
    }

    public void setServices(ServiceDTO[] services) {
        this.services = new ArrayList<>();
        Collections.addAll(this.services, services);
    }

    public void setConsideringServiceHolder(ServiceDTO consideringServiceHolder) {
        this.consideringServiceHolder = consideringServiceHolder;
    }

    public void setLastClickedService(int lastClickedService) {
        this.lastClickedService = lastClickedService;
    }
}