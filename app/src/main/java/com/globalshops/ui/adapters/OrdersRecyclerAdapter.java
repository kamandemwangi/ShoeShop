package com.globalshops.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.globalshops.R;
import com.globalshops.models.Shoe;
import com.globalshops.ui.viewmodels.StockInfoViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class OrdersRecyclerAdapter extends RecyclerView.Adapter<OrdersRecyclerAdapter.OrdersRecyclerViewHolder> {
    private List<Shoe> shoeList;
    private Context context;
    private StockInfoViewModel viewModel;
    private LifecycleOwner lifecycleOwner;

    public OrdersRecyclerAdapter(List<Shoe> shoeList, Context context, StockInfoViewModel viewModel, LifecycleOwner lifecycleOwner) {
        this.shoeList = shoeList;
        this.context = context;
        this.viewModel = viewModel;
        this.lifecycleOwner = lifecycleOwner;
    }

    @NonNull
    @Override
    public OrdersRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_orders_view_holder, parent, false);
        return new OrdersRecyclerViewHolder(view, viewModel);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersRecyclerViewHolder holder, int position) {
        RequestOptions requestOptions = new RequestOptions()
                .error(R.drawable.outline_error_24)
                .fitCenter();

        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(shoeList.get(position).getImageUrlAt0())
                .placeholder(R.drawable.add_shoe_placeholder)
                .circleCrop()
                .into(holder.shoeImageView);
        holder.shoeNameTextView.setText(shoeList.get(position).getName());
        holder.shoePriceTagTextView.setText("Ksh. "+shoeList.get(position).getShoePriceTag());
        holder.shoeQuantityTextView.setText("Quantity "+shoeList.get(position).getQuantity());
        holder.shoeSizeTextView.setText("Size: "+shoeList.get(position).getShoeSize());
        holder.shoeColorTextView.setText("Color: "+shoeList.get(position).getShoeColor());
        holder.shoeOrderNumberTextView.setText("Order number: "+shoeList.get(position).getOrderNumber());

        holder.orderStatus(position);
        holder.showOrderStatus(position);
    }

    @Override
    public int getItemCount() {
        return shoeList.size();
    }

    public class OrdersRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView shoeImageView;
        private MaterialTextView shoeNameTextView;
        private MaterialTextView shoePriceTagTextView;
        private MaterialTextView shoeQuantityTextView;
        private MaterialTextView shoeSizeTextView;
        private MaterialTextView shoeColorTextView;
        private MaterialTextView shoeOrderNumberTextView;
        private MaterialTextView shoeOrderStatusTextView;
        private MaterialButton acceptOrderButton;
        private MaterialButton declineOrderButton;

        private StockInfoViewModel viewModel;

        public OrdersRecyclerViewHolder(@NonNull View itemView, StockInfoViewModel viewModel) {
            super(itemView);

            shoeImageView = itemView.findViewById(R.id.orders_image_view);
            shoeNameTextView = itemView.findViewById(R.id.orders_shoe_title);
            shoePriceTagTextView = itemView.findViewById(R.id.orders_shoe_price_tag);
            shoeQuantityTextView = itemView.findViewById(R.id.orders_shoe_quantity);
            shoeSizeTextView = itemView.findViewById(R.id.orders_shoe_Size);
            shoeColorTextView = itemView.findViewById(R.id.orders_shoe_color);
            shoeOrderNumberTextView = itemView.findViewById(R.id.orders_shoe_order_number);
            shoeOrderStatusTextView = itemView.findViewById(R.id.orders_shoe_order_status);
            acceptOrderButton = itemView.findViewById(R.id.accept_orders_button);
            declineOrderButton = itemView.findViewById(R.id.decline_orders_button);

            this.viewModel = viewModel;
            addClickListeners();
        }

        private void orderStatus(int position){
            if (shoeList.get(position).getOrderStatus() != null){
                acceptOrderButton.setEnabled(false);
                declineOrderButton.setEnabled(false);
            }

        }

        private void showOrderStatus(int position){
            if (shoeList.get(position).getOrderStatus() == null){
                shoeOrderStatusTextView.setText("Pending order, waiting your action");
            }else {
                shoeOrderStatusTextView.setText("Orders status: "+shoeList.get(position).getOrderStatus());
            }
        }

        private void acceptDeclineOrders(String oderStatus){
            viewModel.updateOrderStatus(shoeList.get(getAdapterPosition()).getOrderNumber(), oderStatus)
                    .observe(lifecycleOwner, new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {
                            if (aBoolean){
                                acceptOrderButton.setEnabled(false);
                                declineOrderButton.setEnabled(false);
                            }
                        }
                    });
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.accept_orders_button:{
                    acceptDeclineOrders("accepted");
                    break;

                }
                case R.id.decline_orders_button:{
                    acceptDeclineOrders("declined");
                    break;
                }
            }
        }

        public void addClickListeners(){
            acceptOrderButton.setOnClickListener(this);
            declineOrderButton.setOnClickListener(this);
        }
    }

}
