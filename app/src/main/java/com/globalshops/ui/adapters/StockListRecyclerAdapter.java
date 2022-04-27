package com.globalshops.ui.adapters;

import android.content.Context;
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
import com.globalshops.models.Shoe;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class StockListRecyclerAdapter extends RecyclerView.Adapter<StockListRecyclerAdapter.StockListViewHolder> {
    private Context context;
    private List<Shoe> shoeList;
    private OnShoeClickInterface onShoeClickInterface;

    public StockListRecyclerAdapter(Context context, List<Shoe> shoeList, OnShoeClickInterface onShoeClickInterface) {
        this.context = context;
        this.shoeList = shoeList;
        this.onShoeClickInterface = onShoeClickInterface;
    }

    @NonNull
    @Override
    public StockListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_shoe_stock_list_view_holder, viewGroup, false);
        return new StockListViewHolder(view, onShoeClickInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull StockListViewHolder holder, int i) {
        RequestOptions requestOptions = new RequestOptions()
                .error(R.drawable.outline_error_24)
                .fitCenter();

        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(shoeList.get(i).getImageUrls().get(0))
                .placeholder(R.drawable.add_shoe_placeholder)
                .circleCrop()
                .into(holder.shoeImageView);
        holder.shoeName.setText(shoeList.get(i).getName());
        holder.shoeProductId.setText(shoeList.get(i).getProductId());
    }

    @Override
    public int getItemCount() {
        return shoeList.size();
    }

    public class StockListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView shoeImageView;
        private MaterialTextView shoeName;
        private MaterialTextView shoeProductId;
        private ImageButton nextButton;
        private OnShoeClickInterface onShoeClickInterface;

        public StockListViewHolder(@NonNull View itemView, OnShoeClickInterface onShoeClickInterface) {
            super(itemView);
            shoeImageView = itemView.findViewById(R.id.stock_list_image_view);
            shoeName = itemView.findViewById(R.id.shoe_title);
            shoeProductId = itemView.findViewById(R.id.shoe_product_id);
            nextButton = itemView.findViewById(R.id.stock_list_next_image_button);

            this.onShoeClickInterface = onShoeClickInterface;

            itemView.setOnClickListener(this);
            shoeImageView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            onShoeClickInterface.onShoeClick(getAdapterPosition());

            switch (view.getId()){
                case R.id.stock_list_next_image_button:{
                    onShoeClickInterface.onShoeClick(getAdapterPosition());
                    break;
                }
            }

        }
    }
    public interface OnShoeClickInterface{
        void onShoeClick(int position);

    }
}
