package com.globalshops.ui.bottom_navigation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.globalshops.R;
import com.globalshops.models.Shoe;
import com.globalshops.ui.adapters.StockListRecyclerAdapter;
import com.globalshops.ui.viewmodels.StockInfoViewModel;
import com.globalshops.utils.VerticalSpacingItemDecorator;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StockFragment extends Fragment implements StockListRecyclerAdapter.OnShoeClickInterface {
    private static final String TAG = "StockFragment";
    private List<Shoe> shoeList = new ArrayList<>();

    public StockInfoViewModel viewModel;

    private StockListRecyclerAdapter adapter;
    //ui
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(StockInfoViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stock_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.shoe_stock_list_recyclerView);
        initializeRecyclerview();
        observeShoe();
    }

    private void initializeRecyclerview(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new StockListRecyclerAdapter(getContext(), shoeList, this);
        recyclerView.setAdapter(adapter);
        VerticalSpacingItemDecorator verticalSpacingItemDecorator = new VerticalSpacingItemDecorator(10);
        recyclerView.addItemDecoration(verticalSpacingItemDecorator);
    }

    private void observeShoe(){
        viewModel.getShoeList().observe(getViewLifecycleOwner(), new Observer<List<Shoe>>() {
            @Override
            public void onChanged(List<Shoe> shoes) {
                if (shoes != null){
                    Log.d(TAG, "onChanged: "+shoes.size());
                    if (shoeList.size() > 0){
                        shoeList.clear();
                    }
                    for (int i = 0; i < shoes.size(); i++){
                        shoeList.add(shoes.get(i));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
    @Override
    public void onShoeClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("selected_shoe_product_id", shoeList.get(position).getProductId());
        NavHostFragment.findNavController(StockFragment.this).navigate(R.id.stock_list_fragment_to_full_details, bundle);
    }
}