package com.example.openeyes.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.openeyes.R;
import com.example.openeyes.databinding.ItemReportedDefectBinding;
import com.example.openeyes.model.Defect;

import java.util.ArrayList;

public class ReportedDefectAdapter extends RecyclerView.Adapter<ReportedDefectAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Defect> itemsTexts;
    private ArrayList<String> itemsImages;

    public ReportedDefectAdapter(Context context, ArrayList<Defect> itemsTexts, ArrayList<String> itemsImages) {
        this.context = context;
        this.itemsTexts = itemsTexts;
        this.itemsImages = itemsImages;
    }

    @NonNull
    @Override
    public ReportedDefectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReportedDefectBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this.context), R.layout.item_reported_defect, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportedDefectAdapter.ViewHolder holder, int position) {
        Defect itemText = itemsTexts.get(position);
        String itemImage = itemsImages.get(position);

        holder.binding.ratingBarReportedDefect.setRating(itemText.getRate());
        holder.binding.txtReportedDefectLikes.setText(itemText.getLikes() + "");
        holder.binding.txtReportedDefectCategory.setText(itemText.getCategory());
        holder.binding.txtReportedDefectLocation.setText(itemText.getLocation());
        holder.binding.txtReportedDefectDescription.setText(itemText.getDescription());
        Glide
            .with(this.context)
            .load(itemImage)
            .placeholder(context.getDrawable(R.drawable.png_image_holder))
            .into(holder.binding.imgReportedDefect);

        if (position == itemsTexts.size() - 1)
            holder.binding.viewDefects3.setVisibility(View.VISIBLE);
        else
            holder.binding.viewDefects3.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return itemsTexts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemReportedDefectBinding binding;

        public ViewHolder(ItemReportedDefectBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
