package com.globalshops.ui.bottom_navigation;

import static android.app.Activity.RESULT_OK;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.globalshops.R;
import com.globalshops.models.Shoe;
import com.globalshops.models.UploadImagesTask;
import com.globalshops.ui.adapters.AddStockImagesRecyclerAdapter;
import com.globalshops.ui.adapters.CustomChipRecyclerAdapter;
import com.globalshops.ui.adapters.CustomShoeColorChipRecyclerAdapter;
import com.globalshops.ui.viewmodels.AddShoeVieModel;
import com.globalshops.utils.InputValidation;
import com.globalshops.utils.VerticalSpacingItemDecorator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class AddStockFragment extends Fragment implements View.OnClickListener, CustomChipRecyclerAdapter.OnContainerListener, CustomShoeColorChipRecyclerAdapter.OnContainerListener {
    private static final String TAG = "AddStockFragment";
    private static final int PICK_IMAGE_REQUEST = 1;
    private List<Uri> shoeUriList = new ArrayList<>();
    private AddStockImagesRecyclerAdapter recyclerAdapter;
    private List<String> shoeSizesList = new ArrayList<>();
    private List<String> shoeColorsList = new ArrayList<>();
    private List<String> checkedShoesSizes = new ArrayList<>();
    private List<String> checkedShoeColors = new ArrayList<>();
    private List<String> imageNames = new ArrayList<>();
    private List<HashMap<String, String>> shoeSiZeQuantityList = new ArrayList<>();
    private List<HashMap<String, String>> shoeColorQuantityList = new ArrayList<>();
    private CustomChipRecyclerAdapter shoeSizeRecyclerAdapter;
    private CustomShoeColorChipRecyclerAdapter shoeColorsRecyclerAdapter;
    private AlertDialog dialog;



    //ui
     private ProgressBar shoeSizeProgressBar;
     private ProgressBar shoeColorProgressBar;
     private ImageButton addShoeImagePrevButton;
     private MaterialTextView imageExceedTextError;
     private RecyclerView recyclerview;
     private TextInputLayout shoeNameTextInputLayout;
     private TextInputLayout shoeDescTextInputLayout;
     private TextInputLayout quantityTextInputLayout;
     private MaterialButton uploadButton;
     private RecyclerView shoeSizesRecyclerView;
     private RecyclerView shoeColorsRecyclerView;
     private MaterialTextView unSelectedShoeSizeTextView;
     private MaterialTextView unSelectedShoeColorTextView;
     private MaterialTextView unSelectedGenderTextView;
     private MaterialButton addQuantityButton;
     private TextInputLayout shoePriceTagTextInputLayout;
     private ChipGroup genderChipGroup;
     private Chip maleChip;
     private Chip femaleChip;
     private Chip uniSexChip;
     private ProgressBar uploadProgressBar;

    @Inject
    FirebaseAuth auth;
    private AddShoeVieModel vieModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vieModel = new ViewModelProvider(this).get(AddShoeVieModel.class);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_stock, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shoeSizeProgressBar = view.findViewById(R.id.shoe_size_progressBar);
        shoeColorProgressBar = view.findViewById(R.id.shoe_colors_progressBar);
        addShoeImagePrevButton = view.findViewById(R.id.image_button_select_shoe_images);
        imageExceedTextError = view.findViewById(R.id.image_exceed_error_text_view);
        recyclerview = view.findViewById(R.id.add_image_preview_recyclerView);
        imageExceedTextError = view.findViewById(R.id.image_exceed_error_text_view);
        shoePriceTagTextInputLayout = view.findViewById(R.id.add_stock_shoe_price_tag);
        shoeNameTextInputLayout = view.findViewById(R.id.add_stock_shoe_name);
        shoeDescTextInputLayout = view.findViewById(R.id.add_stock_shoe_desc);
        uploadButton = view.findViewById(R.id.upload_shoe_button);
        shoeSizesRecyclerView = view.findViewById(R.id.shoe_sizes_recycler_view);
        shoeColorsRecyclerView = view.findViewById(R.id.shoe_colors_recycler_view);
        unSelectedShoeSizeTextView = view.findViewById(R.id.unSelected_shoe_sizes_textView);
        unSelectedShoeColorTextView = view.findViewById(R.id.unSelected_shoe_color_textView);
        unSelectedGenderTextView = view.findViewById(R.id.unSelected_gender_textView_error);
        genderChipGroup = view.findViewById(R.id.gender_chip_group);
        maleChip = view.findViewById(R.id.male_chip);
        femaleChip = view.findViewById(R.id.female_chip);
        uniSexChip = view.findViewById(R.id.unisex_chip);
        uploadProgressBar = view.findViewById(R.id.upload_shoe_progressBar);

        isLoggedIn();
        initializeRecyclerView();
        addClickListeners();
        populateShoeSizeChips();
        populateShoeColorsChip();
    }

    private void initializeRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerview.setLayoutManager(layoutManager);
        recyclerAdapter = new AddStockImagesRecyclerAdapter(shoeUriList, getContext());
        recyclerview.setAdapter(recyclerAdapter);
        VerticalSpacingItemDecorator verticalSpacingItemDecorator = new VerticalSpacingItemDecorator(10);
        recyclerview.addItemDecoration(verticalSpacingItemDecorator);
    }

    private void initializeShoeSizesRecyclerView(){
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 5);
        shoeSizesRecyclerView.setLayoutManager(gridLayoutManager);
        shoeSizeRecyclerAdapter = new CustomChipRecyclerAdapter(shoeSizesList, checkedShoesSizes, this);
        shoeSizesRecyclerView.setAdapter(shoeSizeRecyclerAdapter);
        VerticalSpacingItemDecorator verticalSpacingItemDecorator = new VerticalSpacingItemDecorator(10);
        shoeSizesRecyclerView.addItemDecoration(verticalSpacingItemDecorator);

    }

    private void initializeShoeColorsRecyclerView(){
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 5);
        shoeColorsRecyclerView.setLayoutManager(gridLayoutManager);
        shoeColorsRecyclerAdapter = new CustomShoeColorChipRecyclerAdapter(shoeColorsList, checkedShoeColors, this);
        shoeColorsRecyclerView.setAdapter(shoeColorsRecyclerAdapter);
        VerticalSpacingItemDecorator verticalSpacingItemDecorator = new VerticalSpacingItemDecorator(10);
        shoeColorsRecyclerView.addItemDecoration(verticalSpacingItemDecorator);

    }
    private void addClickListeners(){
        addShoeImagePrevButton.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
    }

    private void isLoggedIn(){
       if (auth.getCurrentUser() == null){
           NavHostFragment.findNavController(AddStockFragment.this).navigate(R.id.add_stock_to_login_fragment);
       }
    }


    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
        && data != null){
            ClipData clipData = data.getClipData();
            if (clipData == null){
                shoeUriList.add(data.getData());
                recyclerAdapter.notifyDataSetChanged();

            }else {
                for (int i = 0; i < clipData.getItemCount(); i++){
                    shoeUriList.add(clipData.getItemAt(i).getUri());
                    recyclerAdapter.notifyDataSetChanged();
                }
            }
            Log.d(TAG, "Size: "+shoeUriList.get(0));

        }

    }

    
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.image_button_select_shoe_images:{
                openFileChooser();
                break;
            }
            case R.id.upload_shoe_button:{
                addShoeImagesToStorage();
                break;
            }

        }
    }

    private void populateShoeSizeChips(){
        shoeSizeProgressBar.setVisibility(View.VISIBLE);
        vieModel.getShoeSizes().observe(getViewLifecycleOwner(), new Observer<Shoe>() {
            @Override
            public void onChanged(Shoe shoe) {
                if (shoeSizesList.size() > 0){
                    shoeSizesList.clear();
                }
                if (shoe != null){
                    initializeShoeSizesRecyclerView();
                    for (int i = 0; i < shoe.getShoeSizesList().size(); i++){
                        shoeSizesList.add(shoe.getShoeSizesList().get(i));
                        shoeSizeProgressBar.setVisibility(View.GONE);
                    }
                    shoeSizeRecyclerAdapter.notifyDataSetChanged();
                }else {
                    shoeSizeProgressBar.setVisibility(View.GONE);
                }
            }

        });


    }

    private void populateShoeColorsChip(){
        shoeColorProgressBar.setVisibility(View.VISIBLE);
        vieModel.getShoeColors().observe(getViewLifecycleOwner(), new Observer<Shoe>() {
            @Override
            public void onChanged(Shoe shoe) {
                if (shoeColorsList.size() > 0){
                    shoeColorsList.clear();
                }
                if (shoe != null){
                    initializeShoeColorsRecyclerView();
                   for (int i = 0; i < shoe.getShoeColorsList().size(); i++){
                       shoeColorsList.add(shoe.getShoeColorsList().get(i));
                       shoeColorProgressBar.setVisibility(View.GONE);
                   }
                }else {
                    shoeColorProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void addShoeImagesToStorage() {
        uploadProgressBar.setVisibility(View.VISIBLE);
        String shoeProductId = String.valueOf(System.currentTimeMillis());
        String shoePriceTag = shoePriceTagTextInputLayout.getEditText().getText().toString().trim();
        String shoeName = shoeNameTextInputLayout.getEditText().getText().toString().trim();
        String shoeDesc = shoeDescTextInputLayout.getEditText().getText().toString().trim();
        if (shoeUriList.size() == 0) {
            imageExceedTextError.setText("Add at least one shoe image");
            imageExceedTextError.setVisibility(View.VISIBLE);
            uploadProgressBar.setVisibility(View.GONE);
            return;
        } else if (shoeUriList.size() > 4) {
            imageExceedTextError.setText("Remove images to at most 4");
            imageExceedTextError.setVisibility(View.VISIBLE);
            uploadProgressBar.setVisibility(View.GONE);
            return;
        } else if (InputValidation.isValidName(shoePriceTag, shoePriceTagTextInputLayout)
                |InputValidation.isValidName(shoeName, shoeNameTextInputLayout)
                | InputValidation.isValidName(shoeDesc, shoeDescTextInputLayout)) {
            imageExceedTextError.setVisibility(View.GONE);
            uploadProgressBar.setVisibility(View.GONE);
            return;

        } else if (getSelectedGender() == null) {
            unSelectedGenderTextView.setVisibility(View.VISIBLE);
            uploadProgressBar.setVisibility(View.GONE);
            return;
        } else if (checkedShoesSizes.size() == 0) {
            unSelectedShoeSizeTextView.setVisibility(View.VISIBLE);
            unSelectedGenderTextView.setVisibility(View.GONE);
            uploadProgressBar.setVisibility(View.GONE);
            return;
        } else if (checkedShoeColors.size() == 0) {
            unSelectedShoeColorTextView.setVisibility(View.VISIBLE);
            unSelectedShoeSizeTextView.setVisibility(View.GONE);
            uploadProgressBar.setVisibility(View.GONE);
            return;
        } else {
            for (int i = 0; i < shoeUriList.size(); i++) {

                String imageName = String.valueOf(System.currentTimeMillis());
                String imageFileExtension = getFileExtension(shoeUriList.get(i));
                String fullImageName = imageName +"."+ imageFileExtension;
                if (shoeUriList.size() != 0) {
                    if (imageNames.size() > 0){
                        imageNames.clear();
                    }
                    imageNames.add(fullImageName);
                    vieModel.uploadImagesToStorage(shoeUriList.get(i), fullImageName, shoeUriList.size())
                            .observe(getViewLifecycleOwner(), new Observer<UploadImagesTask>() {
                                @Override
                                public void onChanged(UploadImagesTask uploadImagesTask) {
                                    if (uploadImagesTask.isImagesUploaded()) {
                                        uploadShoeDetailsToDb(shoePriceTag,shoeName, shoeDesc, getSelectedGender(), shoeProductId, uploadImagesTask.getImagesUrl(), imageNames);
                                        uploadProgressBar.setVisibility(View.GONE);
                                        uploadButton.setEnabled(false);

                                         unSelectedShoeColorTextView.setVisibility(View.GONE);
                                    }
                                }
                            });

                } else {

                }
            }

        }
    }

    private void uploadShoeDetailsToDb(String shoePriceTag, String shoeName, String shoeDesc, String selectedGender, String shoeProductId, List<String> imagesUrls, List<String> imageNames){

                Shoe shoe = new Shoe(shoePriceTag,
                        shoeName,
                        shoeDesc,
                        selectedGender,
                        shoeProductId,
                        checkedShoesSizes,
                        checkedShoeColors,
                        imagesUrls,
                        cleanShoeSizesQuantityList(),
                        cleanShoeColorQuantityList(),
                        imageNames);
                vieModel.uploadShoeToDb(shoe);
    }
    @Override
    public List<HashMap<String, String>> onContainerClick(int removedChipIndex) {
        addShoeSizeQuantity(removedChipIndex);
        return shoeSiZeQuantityList;
    }

    @Override
    public List<String> onShoeColorContainerClick(int removedChipIndex) {
        addShoeColorQuantity(removedChipIndex);
        return checkedShoeColors;
    }

    private void addShoeSizeQuantity(int removedChipIndex){
        int index = checkedShoesSizes.size() - 1;
         if (removedChipIndex != -1){
           shoeSiZeQuantityList.remove(removedChipIndex);
        }else {
             shoeSiZeQuantityList.add(addQuantityDialog(checkedShoesSizes.get(index)));
        }
        Log.d(TAG, "addShoeSizeQuantity: "+shoeSiZeQuantityList);
    }

    private void addShoeColorQuantity(int removedChipIndex){
        int index = checkedShoeColors.size() - 1;

        if (removedChipIndex != -1){
            shoeColorQuantityList.remove(removedChipIndex);
        }else {
            shoeColorQuantityList.add(addQuantityDialog(checkedShoeColors.get(index)));
        }

    }
    private String getSelectedGender(){
        if (maleChip.isChecked()){
            return "Male";
        }else if (femaleChip.isChecked()) {
            return "Female";
        }else if (uniSexChip.isChecked()) return "Unisex";
        else return null;
    }

    private HashMap<String, String> addQuantityDialog(String selectedItem){
        HashMap<String, String> selectedTypeQuantity = new HashMap<>();
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_add_quantity, null);
        quantityTextInputLayout = view.findViewById(R.id.add_stock_shoe_quantity);
        addQuantityButton = view.findViewById(R.id.add_quantity_button);


        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getActivity());

        dialogBuilder
                .setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

         addQuantityButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 String quantity = quantityTextInputLayout.getEditText().getText().toString();
                 if (InputValidation.isValidName(quantity, quantityTextInputLayout)){
                     return;
                 }else {
                     selectedTypeQuantity.put(selectedItem, quantity);
                     dialog.dismiss();
                 }
             }
         });
         return selectedTypeQuantity;
    }

    private List<HashMap<String, String>> cleanShoeSizesQuantityList(){
        HashMap<String, String> zeroValueMap = new HashMap<>();
        for (int i = 0; i < shoeSiZeQuantityList.size(); i++){
            if (shoeSiZeQuantityList.get(i).isEmpty()){
                shoeSiZeQuantityList.remove(i);
                zeroValueMap.put(checkedShoesSizes.get(i), "0");
                shoeSiZeQuantityList.add(zeroValueMap);
            }
        }
        return shoeSiZeQuantityList;
    }

    private List<HashMap<String, String>> cleanShoeColorQuantityList(){
        HashMap<String, String> zeroValueMap = new HashMap<>();
        for (int i = 0; i < shoeColorQuantityList.size(); i++){
            if (shoeColorQuantityList.get(i).isEmpty()){
                shoeColorQuantityList.remove(i);
                zeroValueMap.put(checkedShoeColors.get(i), "0");
                shoeColorQuantityList.add(zeroValueMap);
            }
        }
        return shoeColorQuantityList;
    }
}
