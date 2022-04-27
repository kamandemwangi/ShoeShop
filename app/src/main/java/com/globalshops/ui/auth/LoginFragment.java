package com.globalshops.ui.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.globalshops.R;
import com.globalshops.ui.viewmodels.BusinessAccountViewModel;
import com.globalshops.utils.InputValidation;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class LoginFragment extends Fragment implements View.OnClickListener {
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private MaterialButton next;
    private MaterialButton newBusiness;
    private MaterialButton forgotPassword;
    private ProgressBar loginProgressBar;

    private TextInputLayout resetEmailTextInputLayout;
    private MaterialButton sendRequestLinkButton;


    private BusinessAccountViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(BusinessAccountViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailTextInputLayout = view.findViewById(R.id.log_in_email_textField);
        passwordTextInputLayout = view.findViewById(R.id.log_in_password_textField);
        next = view.findViewById(R.id.log_in_next_button);
        newBusiness = view.findViewById(R.id.log_in_create_new_business);
        forgotPassword = view.findViewById(R.id.log_in_forgot_password_button);
        loginProgressBar = view.findViewById(R.id.create_account_progress_bar);

        addClickListeners();

    }

    private void addClickListeners(){
        next.setOnClickListener(this);
        newBusiness.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
    }

    private void authenticateUser(){
        loginProgressBar.setVisibility(View.VISIBLE);
        String email = emailTextInputLayout.getEditText().getText().toString();
        String password =passwordTextInputLayout.getEditText().getText().toString();

        if (InputValidation.isValidEmail(email, emailTextInputLayout)
        |InputValidation.isValidPassword(password, passwordTextInputLayout)){
            return;
        }else {
            viewModel.login(email, password).observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (aBoolean){
                        loginProgressBar.setVisibility(View.GONE);

                        //navigation
                        NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.login_fragment_to_add_stock_fragment);
                    }
                    loginProgressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private void sendPasswordResetEmail(View view){
        resetEmailTextInputLayout = view.findViewById(R.id.send_reset_email_address);
        String email = resetEmailTextInputLayout.getEditText().getText().toString().trim();

        viewModel.sendPasswordResetEmail(email);
    }
    private void showSendResetPasswordEmailDialog(){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_send_password_reset_email,null);

        resetEmailTextInputLayout = view.findViewById(R.id.send_reset_email_address);
        sendRequestLinkButton = view.findViewById(R.id.send_reset_email_request_link_button);

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());
        AlertDialog dialog;

        dialogBuilder.setTitle("Recover your account")
                .setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        sendRequestLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = resetEmailTextInputLayout.getEditText().getText().toString().trim();
                if (InputValidation.isValidEmail(email, resetEmailTextInputLayout)){
                    return;
                }else {
                    sendPasswordResetEmail(view);
                    dialog.dismiss();
                }

            }
        });

    }

    private void showResisterShopDialog(){
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());
        AlertDialog dialog;

        dialogBuilder.setTitle("Request for a business account")
                .setMessage("Send as an email at 22johnmwangi@gmail.com or a text to 0748637041 requesting for a business account creation with your details(email).");
        dialog = dialogBuilder.create();
        dialog.show();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.log_in_next_button:{
                authenticateUser();
                break;
            }
            case R.id.log_in_create_new_business:{
                showResisterShopDialog();
                break;
            }
            case R.id.log_in_forgot_password_button:{
                showSendResetPasswordEmailDialog();
                break;
            }
        }
    }
}