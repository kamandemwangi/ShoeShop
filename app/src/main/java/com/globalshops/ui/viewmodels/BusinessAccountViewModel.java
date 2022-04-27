package com.globalshops.ui.viewmodels;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.globalshops.models.BusinessAccount;
import com.globalshops.models.UploadProfileImageTask;
import com.globalshops.repositories.BusinessAccountRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class BusinessAccountViewModel extends ViewModel {

    private BusinessAccountRepository repository;

    @Inject
    public BusinessAccountViewModel(BusinessAccountRepository repository) {
        this.repository = repository;
    }

    public LiveData<Boolean> login(String email, String password){
        return repository.login(email, password);
    }
    public void sendPasswordResetEmail(String email){
        repository.sendPasswordResetEmail(email);
    }

    public void addBusinessDetails(BusinessAccount businessAccount){
        repository.addBusinessDetails(businessAccount);
    }

    public LiveData<BusinessAccount> getBusinessAccountDetails(){
        return repository.getBusinessAccountDetails();
    }
    public void updateShopEmail(String email){
        repository.updateAuthUserEmail(email);
    }

    public LiveData<Boolean> reAuthenticateUser(String email, String password){
        return repository.reAuthenticateUser(email, password);
    }

    public LiveData<Boolean> changePassword(String password){
        return repository.changePassword(password);
    }

    public LiveData<UploadProfileImageTask> uploadImage(Uri uri, String imageName){
        return repository.uploadImage(uri, imageName);
    }

    public void addShopIdToUtils(){
        repository.addShopIDToUtils();
    }
}
