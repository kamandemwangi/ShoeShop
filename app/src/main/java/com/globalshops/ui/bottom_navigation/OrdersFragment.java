package com.globalshops.ui.bottom_navigation;

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
import com.globalshops.ui.adapters.OrdersRecyclerAdapter;
import com.globalshops.ui.viewmodels.StockInfoViewModel;
import com.globalshops.utils.VerticalSpacingItemDecorator;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OrdersFragment extends Fragment {
    private List<Shoe> shoeList = new ArrayList<>();
    private RecyclerView recyclerView;
    private OrdersRecyclerAdapter adapter;

    private StockInfoViewModel viewModel;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(StockInfoViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.orders_recyclerView);
        observeOrders();
    }

    private void initializeRecyclerview(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new OrdersRecyclerAdapter(shoeList, getContext(), viewModel, getViewLifecycleOwner());
        recyclerView.setAdapter(adapter);
        VerticalSpacingItemDecorator verticalSpacingItemDecorator = new VerticalSpacingItemDecorator(10);
        recyclerView.addItemDecoration(verticalSpacingItemDecorator);

    }

    private void observeOrders(){
       viewModel.getOrders().observe(getViewLifecycleOwner(), new Observer<List<Shoe>>() {
           @Override
           public void onChanged(List<Shoe> shoes) {
               if (shoes != null){
                   for (Shoe shoe : shoes){
                       shoeList.add(shoe);
                   }
               }
               adapter.notifyDataSetChanged();
           }
       });

       initializeRecyclerview();
    }
}