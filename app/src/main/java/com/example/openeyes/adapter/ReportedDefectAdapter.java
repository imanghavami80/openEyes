package com.example.openeyes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.openeyes.R;
import com.example.openeyes.databinding.ItemReportedDefectBinding;
import com.example.openeyes.model.Defect;

import java.util.ArrayList;

public class ReportedDefectAdapter extends RecyclerView.Adapter<ReportedDefectAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Defect> items;

    public ReportedDefectAdapter(Context context, ArrayList<Defect> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ReportedDefectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReportedDefectBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this.context), R.layout.item_reported_defect, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportedDefectAdapter.ViewHolder holder, int position) {
        Defect item = items.get(position);

        holder.binding.ratingBarReportedDefect.setRating(item.getRate());
        holder.binding.txtReportedDefectLikes.setText(item.getLikes() + "");
        holder.binding.txtReportedDefectCategory.setText(item.getCategory());
        holder.binding.txtReportedDefectLocation.setText(item.getLocation());
        holder.binding.txtReportedDefectDescription.setText(item.getDescription());

        if (position == items.size() - 1)
            holder.binding.viewDefects3.setVisibility(View.VISIBLE);
        else
            holder.binding.viewDefects3.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemReportedDefectBinding binding;

        public ViewHolder(ItemReportedDefectBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
