package com.example.openeyes.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.openeyes.R;
import com.example.openeyes.databinding.ItemReportedDefectBinding;
import com.example.openeyes.model.Defect2;
import com.example.openeyes.view.activities.VoteDefectActivity;

import java.util.ArrayList;

public class ReportedDefectAdapter extends RecyclerView.Adapter<ReportedDefectAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Defect2> items;


    public ReportedDefectAdapter(Context context, ArrayList<Defect2> items) {
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
        Defect2 item = items.get(position);

        holder.binding.ratingBarReportedDefect.setRating(item.getRate());
        holder.binding.txtReportedDefectLikes.setText(item.getLikes() + "");
        holder.binding.txtReportedDefectCategory.setText(item.getCategory());
        holder.binding.txtReportedDefectLocation.setText(item.getLocation());
        holder.binding.txtReportedDefectDescription.setText(item.getDescription());
        Glide
                .with(this.context)
                .load(item.getFirstImage())
                .placeholder(context.getDrawable(R.drawable.png_image_holder))
                .into(holder.binding.imgReportedDefect);

        if (position == items.size() - 1)
            holder.binding.viewDefects3.setVisibility(View.VISIBLE);
        else
            holder.binding.viewDefects3.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, VoteDefectActivity.class));

            }
        });

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
