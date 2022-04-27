package com.globalshops.ui.adapters;


import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.globalshops.R;
import com.globalshops.ui.viewmodels.StockInfoViewModel;
import com.globalshops.utils.InputValidation;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class ShoeSizesDetailsRecyclerAdapter extends RecyclerView.Adapter<ShoeSizesDetailsRecyclerAdapter.ShoeSizesDetailsViewHolder> {
    private static final String TAG = "ShoeSizesDetailsRecyclerAdapter";
    private LifecycleOwner lifecycleOwner;
    private List<String> shoeSizeList;
    private List<HashMap<String, String>> shoeQuantityList;
    private StockInfoViewModel viewModel;
    private final String shoeProductId;
    private String shoeListField;
    private String shoeQuantityListField;


    public ShoeSizesDetailsRecyclerAdapter(LifecycleOwner lifecycleOwner, List<String> shoeSizeList, List<HashMap<String, String>> shoeQuantityList, StockInfoViewModel viewModel, String shoeProductId, String shoeListField, String shoeQuantityListField) {
        this.lifecycleOwner = lifecycleOwner;
        this.shoeSizeList = shoeSizeList;
        this.shoeQuantityList = shoeQuantityList;
        this.viewModel = viewModel;
        this.shoeProductId = shoeProductId;
        this.shoeListField = shoeListField;
        this.shoeQuantityListField = shoeQuantityListField;
    }

    @NonNull
    @Override
    public ShoeSizesDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_shoe_sizes_details_viewholder, viewGroup, false);
        return new ShoeSizesDetailsViewHolder(view, viewModel);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoeSizesDetailsViewHolder holder, int i) {
        holder.shoeSizeTextview.setText(shoeSizeList.get(i));
        holder.shoeQuantityTextInputLayout.getEditText().setText(shoeQuantityList.get(i).get(shoeSizeList.get(i)));
    }

    @Override
    public int getItemCount() {
        return shoeSizeList.size();
    }

    public class ShoeSizesDetailsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private MaterialTextView shoeSizeTextview;
        private TextInputLayout shoeQuantityTextInputLayout;
        private MaterialButton updateShoeSizesButton;
        private MaterialButton removeShoeButton;
        private ProgressBar progressBar;


        public StockInfoViewModel viewModel;


        public ShoeSizesDetailsViewHolder(@NonNull View itemView, StockInfoViewModel viewModel) {
            super(itemView);

            shoeSizeTextview = itemView.findViewById(R.id.shoe_size_text_view);
            shoeQuantityTextInputLayout = itemView.findViewById(R.id.shoe_details_quantity);
            updateShoeSizesButton = itemView.findViewById(R.id.update__shoe_quantity_button);
            removeShoeButton = itemView.findViewById(R.id.shoe_sizes_remove_shoe_button);
            progressBar = itemView.findViewById(R.id.shoe_sizes_details_progressBar);

            this.viewModel = viewModel;



            addClickListeners();
            //shoeQuantityTextInputLayout.getEditText().addTextChangedListener(shoeSizeQuantityTextWatcher);
        }

        private void updateShoeSizeQuantity(int position){
            progressBar.setVisibility(View.VISIBLE);
            Map<String, Object> newValue = new HashMap<>();
            String shoeSize = shoeSizeList.get(position);
            String shoeSizeQuantityTextField = shoeQuantityTextInputLayout.getEditText().getText().toString().trim();

            if (InputValidation.isValidShoeQuantity(shoeSizeQuantityTextField, shoeQuantityTextInputLayout)){
                progressBar.setVisibility(View.GONE);
                return;
            }else {
                newValue.put(shoeSize, shoeSizeQuantityTextField);
                viewModel.updateShoeSizeQuantitySingleRow(getShopMapObjectDbValue(position), newValue, shoeProductId, shoeQuantityListField).observe(lifecycleOwner, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        if (aBoolean) {
                            progressBar.setVisibility(View.GONE);
                            viewModel.reArrangeSelectedShoeSizesArray(shoeSize, shoeProductId, shoeListField);
                        }
                    }
                });

            }
        }

        private Map<String, Object> getShopMapObjectDbValue(int position){
            String shoeSize = shoeSizeList.get(position);
            String quantity = shoeQuantityList.get(position).get(shoeSize);
            Map<String, Object> map = new HashMap<>();
            map.put(shoeSize, quantity);
            return map;
        }

        private void removeShoeQuantityRow(int position){
            progressBar.setVisibility(View.VISIBLE);
            Map<String, Object> map = new HashMap<>();
            String shoeSize = shoeSizeList.get(position);
            String shoeSizeQuantityTextField = shoeQuantityTextInputLayout.getEditText().getText().toString().trim();
            map.put(shoeSize, shoeSizeQuantityTextField);

            viewModel.removeSingleShoeSizeQuantity(map, shoeProductId, shoeQuantityListField, shoeListField, shoeSize).observe(lifecycleOwner, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (aBoolean){
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
            notifyDataSetChanged();
        }
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.update__shoe_quantity_button:{
                    updateShoeSizeQuantity(getAdapterPosition());
                    break;
                }
                case R.id.shoe_sizes_remove_shoe_button:{
                    removeShoeQuantityRow(getAdapterPosition());
                    break;
                }
            }
        }

        private void addClickListeners(){
            updateShoeSizesButton.setOnClickListener(this);
            removeShoeButton.setOnClickListener(this);
        }
    }



}
