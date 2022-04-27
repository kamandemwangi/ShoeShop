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

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.globalshops.R;
import com.globalshops.models.BusinessAccount;
import com.globalshops.models.UploadProfileImageTask;
import com.globalshops.ui.viewmodels.BusinessAccountViewModel;
import com.globalshops.utils.InputValidation;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ShopDetailsFragment extends Fragment implements View.OnClickListener {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "Shop";
    @Inject
    public FirebaseAuth auth;

    private BusinessAccountViewModel viewModel;

    private TextInputLayout shopNameTextInputLayout;
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout phoneNumberTextInputLayout;
    private TextInputLayout streetTextInputLayout;
    private TextInputLayout buildingTextInputLayout;
    private TextInputLayout shopNumberTextInputLayout;
    private MaterialButton saveButton;
    private MaterialButton changePassword;
    private MaterialButton logout;
    private ProgressBar accountProfileProgressBar;
    private ProgressBar accountProfileMainLayoutProgressBar;
    private View mainLayout;
    private View progressBarLayout;
    private ImageView profileImage;
    private ImageButton addImageButton;
    private MaterialTextView unselectedProfileTextView;

    //reAuth ui
    private TextInputLayout reAuthTextInputLayoutEmail;
    private TextInputLayout reAuthTextInputLayoutPassword;
    private MaterialButton reAuthLoginButton;
    private ProgressBar reAuthProgressBar;
    private ProgressBar changePasswordProgressBar;

    //reAuth change password ui
    private TextInputLayout newPasswordTextInputLayout;
    private TextInputLayout repeatPasswordTextInputLayout;
    private MaterialButton changePasswordDialogButton;
    private MaterialButton cancelPasswordChange;


    private String dbShopName;
    private String dbShopEmail;
    private String dbShopPhone;
    private String dbShopStreet;
    private String dbShopBuilding;
    private String dbShopNumber;
    private String dbShopProfileImageUrl;
    private Uri profileImageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(BusinessAccountViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shopNameTextInputLayout = view.findViewById(R.id.shop_name_textField);
        emailTextInputLayout = view.findViewById(R.id.shop_email_textField);
        phoneNumberTextInputLayout = view.findViewById(R.id.shop_phone_textField);
        streetTextInputLayout = view.findViewById(R.id.shop_street_textField);
        buildingTextInputLayout = view.findViewById(R.id.shop_building_textField);
        shopNumberTextInputLayout = view.findViewById(R.id.shop_shop_number_textField);
        saveButton = view.findViewById(R.id.shop_details_save_button);
        changePassword = view.findViewById(R.id.shop_details_change_password_button);
        logout = view.findViewById(R.id.shop_details_log_out_button);
        accountProfileProgressBar = view.findViewById(R.id.account_profile_progressBar);
        mainLayout = view.findViewById(R.id.account_profile_main_layout);
        progressBarLayout = view.findViewById(R.id.account_profile_progressBar_layout);
        accountProfileMainLayoutProgressBar = view.findViewById(R.id.account_profile_main_layout_progressBar);

        shopNameTextInputLayout.getEditText().addTextChangedListener(shopNameTextWatcher);
        emailTextInputLayout.getEditText().addTextChangedListener(shopEmailTextWatcher);
        phoneNumberTextInputLayout.getEditText().addTextChangedListener(shopPhoneTextWatcher);
        streetTextInputLayout.getEditText().addTextChangedListener(shopStreetTextWatcher);
        buildingTextInputLayout.getEditText().addTextChangedListener(shopBuildingTextWatcher);
        shopNumberTextInputLayout.getEditText().addTextChangedListener(shopShopNumberTextWatcher);

        profileImage = view.findViewById(R.id.shop_profile_image);
        addImageButton = view.findViewById(R.id.add_profile_image_button);
        unselectedProfileTextView = view.findViewById(R.id.unselected_shop_profile_image_error_textView);


        addClickListeners();
        getShopDetails();

    }

    private void addClickListeners(){
        saveButton.setOnClickListener(this);
        changePassword.setOnClickListener(this);
        logout.setOnClickListener(this);
        addImageButton.setOnClickListener(this);
    }

    private void showReAuthenticateDialog(String newEmail){
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_reauthenticate_dialog, null);
        reAuthTextInputLayoutEmail = view.findViewById(R.id.log_in_email_textField);
        reAuthTextInputLayoutPassword = view.findViewById(R.id.log_in_password_textField);
        reAuthLoginButton = view.findViewById(R.id.log_in_next_button);
        reAuthProgressBar = view.findViewById(R.id.reAuth_password_progressBar);

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());
        AlertDialog dialog;

        dialogBuilder.setTitle("Authenticate to update email")
                .setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        reAuthLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reAuthProgressBar.setVisibility(View.VISIBLE);
                String email = reAuthTextInputLayoutEmail.getEditText().getText().toString();
                String password = reAuthTextInputLayoutPassword.getEditText().getText().toString();

                if (InputValidation.isValidEmail(email, reAuthTextInputLayoutEmail)
                |InputValidation.isValidPassword(password, reAuthTextInputLayoutPassword)){
                    return;
                }else {
                    viewModel.reAuthenticateUser(email, password).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {
                            if (aBoolean){
                                viewModel.updateShopEmail(newEmail);
                                addShopAccountDetails();
                                saveButton.setEnabled(false);
                                reAuthProgressBar.setVisibility(View.GONE);
                                dialog.dismiss();
                            }
                        }
                    });

                }
            }
        });


    }
    private void commitChanges(){
        String shopAuthEmail = auth.getCurrentUser().getEmail();
        String shopInputEmail = emailTextInputLayout.getEditText().getText().toString();
        if (InputValidation.isValidEmail(shopInputEmail, emailTextInputLayout)){
            return;
        }else {
            if (!shopAuthEmail.equals(shopInputEmail)) {
                showReAuthenticateDialog(shopInputEmail);
            }else {
                addShopAccountDetails();
            }

        }
    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){

                profileImageUri = data.getData();
                if (profileImageUri != null){
                    RequestOptions requestOptions = new RequestOptions()
                            .error(R.drawable.outline_error_24)
                            .fitCenter();

                    Glide.with(getContext())
                            .setDefaultRequestOptions(requestOptions)
                            .load(profileImageUri)
                             .placeholder(R.drawable.outline_account_circle_24)
                            .circleCrop()
                            .into(profileImage);

                }else {

            }

        }

    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void addShopAccountDetails(){
        accountProfileMainLayoutProgressBar.setVisibility(View.VISIBLE);
        String shopName = shopNameTextInputLayout.getEditText().getText().toString();
        String email = emailTextInputLayout.getEditText().getText().toString();
        String phoneNumber = phoneNumberTextInputLayout.getEditText().getText().toString();
        String street = streetTextInputLayout.getEditText().getText().toString();
        String building = buildingTextInputLayout.getEditText().getText().toString();
        String shopNumber = shopNumberTextInputLayout.getEditText().getText().toString();

        if (profileImageUri == null){
            unselectedProfileTextView.setText("Select shop profile image");
            unselectedProfileTextView.setVisibility(View.VISIBLE);
            return;
        }else if (InputValidation.isValidName(shopName, shopNameTextInputLayout)
        |InputValidation.isValidEmail(email, emailTextInputLayout)
        |InputValidation.isValidPhoneNumber(phoneNumber, phoneNumberTextInputLayout)
        |InputValidation.isValidName(street, streetTextInputLayout)
        |InputValidation.isValidName(building, buildingTextInputLayout)
        |InputValidation.isValidName(shopNumber, shopNumberTextInputLayout)){

            accountProfileMainLayoutProgressBar.setVisibility(View.GONE);
            unselectedProfileTextView.setVisibility(View.GONE);
            return;
        }else {
            String imageName = String.valueOf(System.currentTimeMillis());
            String imageFileExtension = getFileExtension(profileImageUri);
            String fullImageName = imageName +"."+ imageFileExtension;


            viewModel.uploadImage(profileImageUri, fullImageName).observe(getViewLifecycleOwner(), new Observer<UploadProfileImageTask>() {
                @Override
                public void onChanged(UploadProfileImageTask uploadProfileImageTask) {
                    if (uploadProfileImageTask != null){
                        String imageUrl = uploadProfileImageTask.getImageUrl();
                        BusinessAccount businessAccount = new BusinessAccount(shopName,email, phoneNumber, street, building, shopNumber, fullImageName, imageUrl);
                        viewModel.addBusinessDetails(businessAccount);
                        viewModel.addShopIdToUtils();
                    }
                }
            });

            accountProfileMainLayoutProgressBar.setVisibility(View.GONE);
            unselectedProfileTextView.setVisibility(View.GONE);
        }

    }
    private void getShopDetails(){
        accountProfileProgressBar.setVisibility(View.VISIBLE);
        progressBarLayout.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.GONE);
        viewModel.getBusinessAccountDetails().observe(getViewLifecycleOwner(), new Observer<BusinessAccount>() {
            @Override
            public void onChanged(BusinessAccount businessAccount) {
                if (businessAccount != null){
                    dbShopName = businessAccount.getName();
                    dbShopEmail = businessAccount.getEmail();
                    dbShopPhone = businessAccount.getPhone();
                    dbShopStreet = businessAccount.getStreet();
                    dbShopBuilding = businessAccount.getBusinessBuilding();
                    dbShopNumber = businessAccount.getShopNumber();
                    dbShopProfileImageUrl = businessAccount.getProfileImageUrl();


                    shopNameTextInputLayout.getEditText().setText(dbShopName);
                    emailTextInputLayout.getEditText().setText(dbShopEmail);
                    phoneNumberTextInputLayout.getEditText().setText(dbShopPhone);
                    streetTextInputLayout.getEditText().setText(dbShopStreet);
                    buildingTextInputLayout.getEditText().setText(dbShopBuilding);
                    shopNumberTextInputLayout.getEditText().setText(dbShopNumber);

                    RequestOptions requestOptions = new RequestOptions()
                            .error(R.drawable.outline_error_24)
                            .fitCenter();

                    Glide.with(getContext())
                            .setDefaultRequestOptions(requestOptions)
                            .load(dbShopProfileImageUrl)
                            .placeholder(R.drawable.outline_account_circle_24)
                            .circleCrop()
                            .into(profileImage);



                    accountProfileProgressBar.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.VISIBLE);
                    progressBarLayout.setVisibility(View.GONE);
                }else {
                    //Toast.makeText(getContext(), "Null user", Toast.LENGTH_SHORT).show();
                    accountProfileProgressBar.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.VISIBLE);
                    progressBarLayout.setVisibility(View.GONE);
                }

            }
        });
    }

    private void showReAuthChangePasswordDialog(){
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_reauthenticate_dialog, null);
        reAuthTextInputLayoutEmail = view.findViewById(R.id.log_in_email_textField);
        reAuthTextInputLayoutPassword = view.findViewById(R.id.log_in_password_textField);
        reAuthLoginButton = view.findViewById(R.id.log_in_next_button);
        reAuthProgressBar = view.findViewById(R.id.reAuth_password_progressBar);

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());
        AlertDialog dialog;

        dialogBuilder.setTitle("Authenticate to change password")
                .setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        reAuthLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reAuthProgressBar.setVisibility(View.VISIBLE);
                String email = reAuthTextInputLayoutEmail.getEditText().getText().toString();
                String password = reAuthTextInputLayoutPassword.getEditText().getText().toString();

                if (InputValidation.isValidEmail(email, reAuthTextInputLayoutEmail)
                        |InputValidation.isValidPassword(password, reAuthTextInputLayoutPassword)){
                    return;
                }else{
                    viewModel.reAuthenticateUser(email, password).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {
                            if (aBoolean){
                                showChangePasswordDialog();
                                dialog.dismiss();
                                reAuthProgressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }

    private void showChangePasswordDialog(){
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_change_password, null);
        newPasswordTextInputLayout = view.findViewById(R.id.new_password_textField);
        repeatPasswordTextInputLayout = view.findViewById(R.id.repeat_new_password_textField);
        changePasswordDialogButton = view.findViewById(R.id.change_password_button);
        cancelPasswordChange = view.findViewById(R.id.change_password_cancel_button);
        changePasswordProgressBar = view.findViewById(R.id.change_password_progressBar);

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());
        AlertDialog dialog;

        dialogBuilder
                .setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        changePasswordDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePasswordProgressBar.setVisibility(View.VISIBLE);
                String newPassword = newPasswordTextInputLayout.getEditText().getText().toString();
                String repeatPassword = repeatPasswordTextInputLayout.getEditText().getText().toString();

                if (InputValidation.isValidPassword(newPassword, newPasswordTextInputLayout)
                        |InputValidation.isValidPassword(repeatPassword, repeatPasswordTextInputLayout)
                        |InputValidation.isNewPasswordEqualsRepeatPassword(newPassword, repeatPassword, repeatPasswordTextInputLayout)){
                    changePasswordProgressBar.setVisibility(View.GONE);
                    return;
                }else {
                    viewModel.changePassword(newPassword).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {
                            if (aBoolean){
                                changePasswordProgressBar.setVisibility(View.GONE);
                                dialog.dismiss();
                                Log.d(TAG, "Password change: "+aBoolean);
                            }else {
                                changePasswordProgressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });

        cancelPasswordChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.shop_details_save_button:{
                commitChanges();
                break;
            }
            case R.id.shop_details_change_password_button:{
                showReAuthChangePasswordDialog();
                break;
            }
            case R.id.shop_details_log_out_button:{
                auth.signOut();
                NavHostFragment.findNavController(ShopDetailsFragment.this).navigate(R.id.shop_details_fragment_to_log_in_fragment);
                break;
            }
            case R.id.add_profile_image_button:{
                openFileChooser();
                break;
            }
        }
    }

    private boolean checkShopDetailsTextFieldsChanges(String dbData, String input){
        if (dbData == null){
            dbData = "null";
            if (!dbData.equals(input)) {
                return true;
            } else {
                return false;
            }
        }else {

            if (!dbData.equals(input)) {
                return true;
            } else {
                return false;
            }
        }
    }
    private TextWatcher shopNameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String shopName = shopNameTextInputLayout.getEditText().getText().toString().trim();

            if (InputValidation.isValidName(shopName, shopNameTextInputLayout)){
                saveButton.setEnabled(false);
                return;
            }else if (checkShopDetailsTextFieldsChanges(dbShopNumber, shopName)){
                saveButton.setEnabled(true);
            }else {
                saveButton.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private TextWatcher shopEmailTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String shopEmail = emailTextInputLayout.getEditText().getText().toString().trim();
            if (InputValidation.isValidEmail(shopEmail, emailTextInputLayout)){
                saveButton.setEnabled(false);
                return;
            }else if (checkShopDetailsTextFieldsChanges(dbShopNumber, shopEmail)){
                saveButton.setEnabled(true);
            }else {
                saveButton.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private TextWatcher shopPhoneTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String input = phoneNumberTextInputLayout.getEditText().getText().toString().trim();

            if (InputValidation.isValidPhoneNumber(input, phoneNumberTextInputLayout)){
                saveButton.setEnabled(false);
                return;
            }else if (checkShopDetailsTextFieldsChanges(dbShopPhone, input)){
                saveButton.setEnabled(true);
            }else {
                saveButton.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private TextWatcher shopStreetTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String input = streetTextInputLayout.getEditText().getText().toString().trim();

            if (InputValidation.isValidName(input, streetTextInputLayout)){
                saveButton.setEnabled(false);
                return;
            }else if (checkShopDetailsTextFieldsChanges(dbShopStreet, input)){
                saveButton.setEnabled(true);
            }else {
                saveButton.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private TextWatcher shopBuildingTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String input = buildingTextInputLayout.getEditText().getText().toString().trim();

            if (InputValidation.isValidName(input, buildingTextInputLayout)){
                saveButton.setEnabled(false);
                return;
            }else if (checkShopDetailsTextFieldsChanges(dbShopStreet, input)){
                saveButton.setEnabled(true);
            }else {
                saveButton.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private TextWatcher shopShopNumberTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String input = shopNumberTextInputLayout.getEditText().getText().toString().trim();

            if (InputValidation.isValidName(input, shopNumberTextInputLayout)){
                saveButton.setEnabled(false);
                return;
            }else if (checkShopDetailsTextFieldsChanges(dbShopNumber, input)){
                saveButton.setEnabled(true);
            }else {
                saveButton.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}