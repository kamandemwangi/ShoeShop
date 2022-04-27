package com.globalshops.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.globalshops.R;
import com.globalshops.models.CustomChipObject;
import com.globalshops.models.Shoe;
import com.globalshops.models.ShoeSizeQuantity;
import com.globalshops.utils.CheckableChipList;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomChipRecyclerAdapter extends RecyclerView.Adapter<CustomChipRecyclerAdapter.CustomChipViewHolder>{
    private List<String> chipList;
    private List<String> checkedList;
    private OnContainerListener onContainerListener;

    public CustomChipRecyclerAdapter(List<String> chipList, List<String> checkedList, OnContainerListener onContainerListener) {
        this.chipList = chipList;
        this.checkedList = checkedList;
        this.onContainerListener = onContainerListener;
    }

    @NonNull
    @Override
    public CustomChipViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_chip_view_holder, viewGroup, false);
        return new CustomChipViewHolder(view, onContainerListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomChipViewHolder holder, int position) {
        holder.chipTextViewHolder.setText(chipList.get(position));
    }

    @Override
    public int getItemCount() {
        return chipList.size();
    }



    public class CustomChipViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private MaterialCardView container;
        private MaterialTextView chipTextViewHolder;
        private OnContainerListener onContainerListener;



        public CustomChipViewHolder(@NonNull View itemView, OnContainerListener onContainerListener) {
            super(itemView);
            container = itemView.findViewById(R.id.chip_container);
            chipTextViewHolder = itemView.findViewById(R.id.chip_text_holder);

            itemView.setOnClickListener(this);
            this.onContainerListener = onContainerListener;


        }

        @Override
        public void onClick(View view) {
            if (!container.isChecked()){
                container.setChecked(true);
            }else {
                container.setChecked(false);
            }

            onContainerListener.onContainerClick(addCheckedChips());
        }

        private int addCheckedChips(){
            if (container.isChecked()){
                checkedList.add(chipList.get(getAdapterPosition()));
            }else {
                for (int i = 0; i < checkedList.size(); i++){
                    if (checkedList.get(i).equals(chipList.get(getAdapterPosition()))){
                        checkedList.remove(checkedList.get(i));
                        return i;
                    }
                }
            }
            return -1;
        }
    }
    public interface OnContainerListener{
        List<HashMap<String, String>> onContainerClick(int removedChipIndex);
    }
}
