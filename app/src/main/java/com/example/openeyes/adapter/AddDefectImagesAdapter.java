package com.example.openeyes.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.openeyes.R;
import com.example.openeyes.databinding.ItemDefectImageBinding;
import com.example.openeyes.utility.SnackBarHandler;

import java.util.ArrayList;

public class AddDefectImagesAdapter extends RecyclerView.Adapter<AddDefectImagesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Uri> items;

    public AddDefectImagesAdapter(Context context, ArrayList<Uri> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public AddDefectImagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDefectImageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this.context), R.layout.item_defect_image, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AddDefectImagesAdapter.ViewHolder holder, int position) {
        Uri item = items.get(position);

        holder.binding.imgAddDefectImage.setImageURI(item);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SnackBarHandler.snackBarHideAction(context, holder.binding.getRoot(), context.getString(R.string.hold_to_delete));

            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                items.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(0, getItemCount());
                return true;

            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemDefectImageBinding binding;

        public ViewHolder(ItemDefectImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
