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
import com.example.aiforyou.custom.ServiceDTO;
import com.example.aiforyou.fragments.ServiceFragment;

import java.util.List;

public class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.ServiceViewHolder> {
    private final List<ServiceDTO> services;
    private final ServiceFragment.OnServiceClickListener onServiceClickListener;

    public ServiceListAdapter(List<ServiceDTO> services, ServiceFragment.OnServiceClickListener onServiceClickListener) {
        this.services = services;
        this.onServiceClickListener = onServiceClickListener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ServiceViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.a_service, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        holder.bind(services.get(position));
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public void removeAt(int position) {
        services.remove(position);
        notifyItemRemoved(position);
    }

    class ServiceViewHolder extends RecyclerView.ViewHolder {
        private final ImageView serviceImg;
        private final TextView priceTxt, serviceTxt;
        private final ConstraintLayout container;

        private final View.OnClickListener onClickListener = (v)
                -> onServiceClickListener.onServiceClick(
                        services.get(getBindingAdapterPosition()), getBindingAdapterPosition());

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);

            serviceImg = itemView.findViewById(R.id.serviceImg);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            serviceTxt = itemView.findViewById(R.id.serviceNameTxt);

            container = itemView.findViewById(R.id.container);
        }

        void bind(ServiceDTO service) {
            Glide.with(itemView.getContext()).load(service.getImageUrl()).into(serviceImg);

            serviceTxt.setText(service.getTenService());
            priceTxt.setText(String.format("%s $", service.getPrice()));

            container.setOnClickListener(onClickListener);
        }
    }
}