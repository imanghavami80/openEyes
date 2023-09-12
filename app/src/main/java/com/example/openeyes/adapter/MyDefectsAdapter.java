package com.example.openeyes.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class MyDefectsAdapter extends RecyclerView.Adapter<MyDefectsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Defect2> items;
    private OnItemClickListener listener;

    public MyDefectsAdapter(Context context, ArrayList<Defect2> items, OnItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyDefectsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReportedDefectBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this.context), R.layout.item_reported_defect, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyDefectsAdapter.ViewHolder holder, int position) {
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onHoleItemClicked(item.getUuid(), item.getEmail());

            }
        });

        holder.binding.imgDeleteDefect.setVisibility(View.VISIBLE);
        holder.binding.imgDeleteDefect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog(item.getUuid(), item.getHaveImage(), item.getHaveAudio());

            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemReportedDefectBinding binding;

        public ViewHolder(ItemReportedDefectBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemClickListener {
        void onDeleteItemClicked(String defectUuid, int haveImage, int haveAudio);
        void onHoleItemClicked(String defectUuid, String defectEmail);
    }

    private void showDeleteDialog(String defectUuid, int haveImage, int haveAudio) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setMessage("Do you want to delete this defect?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            this.listener.onDeleteItemClicked(defectUuid, haveImage, haveAudio);

        });
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();

        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void clearData() {
        this.items.clear();
        notifyDataSetChanged();

    }
}
