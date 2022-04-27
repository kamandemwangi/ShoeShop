package com.globalshops.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.globalshops.R;

import java.util.List;

public class AddStockImagesRecyclerAdapter extends RecyclerView.Adapter<AddStockImagesRecyclerAdapter.AddStockImagesViewHolder> {
    private static final String TAG = "AddStockImagesRecyclerAdapter";
    private List<Uri> uriList;
    private final Context context;

    public AddStockImagesRecyclerAdapter(List<Uri> uriList, Context context) {
        this.uriList = uriList;
        this.context = context;
    }

    @NonNull
    @Override
    public AddStockImagesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_add_shoe_image_preview_holder, viewGroup, false);
        return new AddStockImagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddStockImagesViewHolder addStockImagesViewHolder, int i) {
        RequestOptions requestOptions = new RequestOptions()
                .error(R.drawable.outline_error_24)
                .fitCenter();

        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(uriList.get(i))
                .placeholder(R.drawable.add_shoe_placeholder)
                .circleCrop()
                .into(addStockImagesViewHolder.addImagePreview);
    }

    @Override
    public int getItemCount() {
        return uriList.size();
    }

    public class AddStockImagesViewHolder extends RecyclerView.ViewHolder{
        private ImageView addImagePreview;
        private ImageButton removeShoeButton;

        public AddStockImagesViewHolder(@NonNull View itemView) {
            super(itemView);
            addImagePreview = itemView.findViewById(R.id.add_shoe_image_preview);
            removeShoeButton = itemView.findViewById(R.id.icon_remove_shoe);

            removeShoeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeShoe();
                }
            });

        }

        private void removeShoe(){
            if (uriList.size() > 0){
                uriList.remove(getAdapterPosition());
                notifyDataSetChanged();

            }
        }
    }
}
