package com.globalshops.ui.shoe_details;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.globalshops.R;
import com.globalshops.models.Shoe;
import com.globalshops.ui.adapters.ShoeSizesDetailsRecyclerAdapter;
import com.globalshops.ui.viewmodels.StockInfoViewModel;
import com.globalshops.utils.VerticalSpacingItemDecorator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ShoeSizesDetailsFragment extends Fragment {
    private static final String SHOE_SIZES_LIST_FIELD = "shoeSizesList";
    private static final String SHOE_SIZES_QUANTITY_FIELD ="shoeSizeQuantityList";
    private List<String> shoeSizesList = new ArrayList<>();
    private List<HashMap<String, String>> shoeQuantity = new ArrayList<>();

    private StockInfoViewModel viewModel;

    private RecyclerView recyclerView;
    private ShoeSizesDetailsRecyclerAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(StockInfoViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shoe_sizes_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.shoe_details_sizes_recyclerView);

        initializeRecyclerView();
        observeShoeSizes();
    }

    private void initializeRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ShoeSizesDetailsRecyclerAdapter(getViewLifecycleOwner(), shoeSizesList, shoeQuantity, viewModel, getIncomingShoeProdId(), SHOE_SIZES_LIST_FIELD, SHOE_SIZES_QUANTITY_FIELD);
        recyclerView.setAdapter(adapter);
        VerticalSpacingItemDecorator verticalSpacingItemDecorator = new VerticalSpacingItemDecorator(10);
        recyclerView.addItemDecoration(verticalSpacingItemDecorator);
    }

    private void observeShoeSizes(){
       viewModel.getShoeList().observe(getViewLifecycleOwner(), new Observer<List<Shoe>>() {
           @Override
           public void onChanged(List<Shoe> shoes) {
               if (shoes != null){
                   for (Shoe shoe : shoes){
                       if (shoeSizesList.size() > 0){
                           shoeSizesList.clear();
                       }
                       if (shoeQuantity.size() > 0){
                           shoeQuantity.clear();
                       }
                       if (shoe.getProductId().equals(getIncomingShoeProdId())){
                           for (int i = 0; i < shoe.getShoeSizesList().size(); i++){
                               shoeSizesList.add(shoe.getShoeSizesList().get(i));
                           }
                           for (int j = 0; j < shoe.getShoeSizeQuantityList().size(); j++){
                               shoeQuantity.add(shoe.getShoeSizeQuantityList().get(j));
                           }
                           return;
                       }
                   }
               }
           }
       });
    }

    private String getIncomingShoeProdId(){
        return getArguments().getString("selected_shoe_product_id");
    }


}