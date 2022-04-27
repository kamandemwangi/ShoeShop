package com.globalshops.ui.shoe_details;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.globalshops.R;
import com.globalshops.models.Shoe;
import com.globalshops.ui.viewmodels.StockInfoViewModel;
import com.globalshops.utils.InputValidation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ShoeFullDetailsFragment extends Fragment implements View.OnClickListener {
    private  List<String> imageNames = new ArrayList<>();
    private ImageView shoeImageView;
    private TextInputLayout shoePriceTag;
    private TextInputLayout shoeName;
    private TextInputLayout shoeProductId;
    private TextInputLayout shoeShortDesc;

    private MaterialCardView shoeSizesCardNav;
    private MaterialCardView shoeColorsNav;
    private MaterialButton updateShoeDetails;
    private MaterialButton removeShoeDetails;
    private ProgressBar updateShoeDetailsProgressBar;
    private Chip maleChip;
    private Chip femaleChip;
    private Chip unisexChip;

    private StockInfoViewModel viewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(StockInfoViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_shoe_full_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shoeImageView = view.findViewById(R.id.layout_shoe_detail_image_view);
        shoePriceTag = view.findViewById(R.id.shoe_details_shoe_price_tag);
        shoeName = view.findViewById(R.id.shoe_details_shoe_name);
        shoeProductId = view.findViewById(R.id.shoe_details_shoe_price_productId);
        shoeShortDesc = view.findViewById(R.id.shoe_details_shoe_short_desc);
        shoeSizesCardNav = view.findViewById(R.id.shoes_sizes_card_view);
        shoeColorsNav = view.findViewById(R.id.shoes_colors_card_view);
        updateShoeDetails = view.findViewById(R.id.button_update_shoe_details);
        removeShoeDetails = view.findViewById(R.id.delete_shoe);
        updateShoeDetailsProgressBar = view.findViewById(R.id.update_shoe_details_progressBar);
        maleChip = view.findViewById(R.id.male_chip);
        femaleChip = view.findViewById(R.id.female_chip);
        unisexChip = view.findViewById(R.id.unisex_chip);

        addClickListenersEvents();
        getSelectedShoe();
    }

    private void getSelectedShoe(){
        viewModel.getShoeList().observe(getViewLifecycleOwner(), new Observer<List<Shoe>>() {
            @Override
            public void onChanged(List<Shoe> shoes) {
                if (shoes != null){
                    for (Shoe shoe : shoes){
                        if (shoe.getProductId().equals(getIncomingShoeProductId())){
                            shoePriceTag.getEditText().setText(shoe.getShoePriceTag());
                            shoeName.getEditText().setText(shoe.getName());
                            shoeProductId.getEditText().setText(shoe.getProductId());

                            initializeChips(shoe.getGender());
                            shoeShortDesc.getEditText().setText(shoe.getShortDesc());

                            loadShoeImage(shoe.getImageUrls().get(0));

                            if (imageNames.size() > 0){
                                imageNames.clear();
                            }
                            for (int i = 0; i < shoe.getImageNames().size(); i++){
                                imageNames.add(shoe.getImageNames().get(i));
                            }

                           return;
                        }
                    }
                }
            }
        });
    }

    private void loadShoeImage(String url){
        RequestOptions requestOptions = new RequestOptions()
                .error(R.drawable.outline_error_24)
                .fitCenter();

        Glide.with(getContext())
                .setDefaultRequestOptions(requestOptions)
                .load(url)
                .placeholder(R.drawable.add_shoe_placeholder)
                .circleCrop()
                .into(shoeImageView);
    }
    private String getIncomingShoeProductId(){
        return getArguments().getString("selected_shoe_product_id");
    }

    private void initializeChips(String dbChip){
        if (dbChip.equals("Male")){
            maleChip.setChecked(true);
            return;
        }else if (dbChip.equals("Female")){
            femaleChip.setChecked(true);
            return;
        }else if (dbChip.equals("Unisex")){
            unisexChip.setChecked(true);
        }
    }

    private void updateShoeDetails(){
        updateShoeDetailsProgressBar.setVisibility(View.VISIBLE);
        String priceTag = shoePriceTag.getEditText().getText().toString().trim();
        String shoeTitle = shoeName.getEditText().getText().toString().trim();
        String shortDesc = shoeShortDesc.getEditText().getText().toString().trim();

        if (InputValidation.isValidName(priceTag, shoePriceTag)
        |InputValidation.isValidName(shoeTitle, shoeName)
        |InputValidation.isValidName(shortDesc, shoeShortDesc)){
            updateShoeDetailsProgressBar.setVisibility(View.GONE);
            return;
        }else if (getSelectedGender() == null){
            Toast.makeText(getContext(), "Select updating gender", Toast.LENGTH_SHORT).show();
            updateShoeDetailsProgressBar.setVisibility(View.GONE);
            return;
        }else {
            Shoe shoe = new Shoe(priceTag, shoeTitle, shortDesc, getSelectedGender());
            viewModel.updateShoeDetails(shoe, getIncomingShoeProductId()).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (aBoolean){
                        updateShoeDetailsProgressBar.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private void deleteShoe(){
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(getActivity());
        alertDialogBuilder.setTitle("Delete shoe")
                .setMessage("Delete shoe full details")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        viewModel.deleteShoe(getIncomingShoeProductId(), imageNames).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                            @Override
                            public void onChanged(Boolean aBoolean) {
                                if (aBoolean){
                                    NavHostFragment.findNavController(ShoeFullDetailsFragment.this).navigate(R.id.shoe_full_details_fragment_to_shoe_list_fragment);
                                    updateShoeDetailsProgressBar.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                }).show();
    }



    private String getSelectedGender(){
        if (maleChip.isChecked()){
            return "Male";
        }else if (femaleChip.isChecked()){
            return "Female";
        }else if (unisexChip.isChecked()){
            return "Unisex";
        }else return null;
    }
    @Override
    public void onClick(View view) {
       switch (view.getId()){
           case R.id.shoes_sizes_card_view:{
               NavHostFragment.findNavController(ShoeFullDetailsFragment.this).navigate(R.id.shoe_full_details_fragment_to_shoe_sizes_details, putSelectedShoeProdId());
               break;
           }
           case R.id.shoes_colors_card_view:{
               NavHostFragment.findNavController(ShoeFullDetailsFragment.this).navigate(R.id.shoe_full_details_to_shoe_colors_fragment, putSelectedShoeProdId());
               break;
           }
           case R.id.button_update_shoe_details:{
               updateShoeDetails();
               break;
           }
           case R.id.delete_shoe:{
               deleteShoe();
               break;
           }
       }
    }

    private Bundle putSelectedShoeProdId(){
        Bundle bundle = new Bundle();
        bundle.putString("selected_shoe_product_id", getIncomingShoeProductId());

        return bundle;
    }

    private void addClickListenersEvents(){
        shoeSizesCardNav.setOnClickListener(this);
        shoeColorsNav.setOnClickListener(this);
        updateShoeDetails.setOnClickListener(this);
        removeShoeDetails.setOnClickListener(this);
    }
}