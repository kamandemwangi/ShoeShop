package com.globalshops.repositories;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.globalshops.models.Shoe;
import com.globalshops.models.UploadImagesTask;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class AddShoesRepository {
    private MediatorLiveData<Shoe> shoeSizeLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Shoe> shoeColorsLiveData = new MediatorLiveData<>();
    private MediatorLiveData<UploadImagesTask> uploadImagesTaskMediatorLiveData = new MediatorLiveData<>();
    List<String> imagesUrlList = new ArrayList<>();
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Context context;

    public AddShoesRepository(FirebaseAuth auth, FirebaseFirestore db, FirebaseStorage storage, Context context) {
        this.auth = auth;
        this.db = db;
        this.storage = storage;
        this.context = context;
    }

    public LiveData<Shoe> getShoesSizes() {
        db.collection("utils")
                .document("shoe_sizes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Shoe shoe = task.getResult().toObject(Shoe.class);
                            shoeSizeLiveData.postValue(shoe);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return shoeSizeLiveData;
    }

    public LiveData<Shoe> getShoeColors() {
        db.collection("utils")
                .document("shoe_colors")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Shoe shoe = task.getResult().toObject(Shoe.class);
                            shoeColorsLiveData.postValue(shoe);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return shoeColorsLiveData;
    }

    public LiveData<UploadImagesTask> uploadImages(Uri uri, String imageName, int imagesUriListSize) {
        String shopUniqueId = auth.getCurrentUser().getUid();
        StorageReference storageReference = storage.getReference().child(shopUniqueId+"/shoe_images/" + imageName);
        storageReference.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        if (taskSnapshot.getMetadata().getReference() != null) {

                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                        String imageUrl = uri.toString();
                                        imagesUrlList.add(imageUrl);
                                        UploadImagesTask uploadImagesTask = new UploadImagesTask(imagesUrlList, true);
                                        uploadImagesTaskMediatorLiveData.postValue(uploadImagesTask);
                                }
                            });
                    }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Storage:"+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                Toast.makeText(context, "Uploading", Toast.LENGTH_SHORT).show();
            }
        });
        return uploadImagesTaskMediatorLiveData;
    }

    public void uploadShoeToDb(Shoe shoe){
        String shopUniqueId = auth.getCurrentUser().getUid();
        db.collection("shops")
                .document(shopUniqueId)
                .collection("products")
                .document(shoe.getProductId())
                .set(shoe, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Shoe added", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
