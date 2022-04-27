package com.globalshops.ui.viewmodels;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.globalshops.models.Shoe;
import com.globalshops.models.UploadImagesTask;
import com.globalshops.repositories.AddShoesRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddShoeVieModel extends ViewModel {
    private AddShoesRepository repository;

    @Inject
    public AddShoeVieModel(AddShoesRepository repository) {
        this.repository = repository;
    }

    public LiveData<Shoe> getShoeSizes(){
        return repository.getShoesSizes();
    }

    public LiveData<Shoe> getShoeColors(){
        return repository.getShoeColors();
    }

    public LiveData<UploadImagesTask> uploadImagesToStorage(Uri uri, String imageName, int imageUrlList){
        return repository.uploadImages(uri, imageName, imageUrlList);
    }
    public void uploadShoeToDb(Shoe shoe){
        repository.uploadShoeToDb(shoe);
    }
}
