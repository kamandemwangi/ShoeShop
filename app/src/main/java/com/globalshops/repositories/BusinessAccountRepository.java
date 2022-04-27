package com.globalshops.repositories;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.globalshops.models.BusinessAccount;
import com.globalshops.models.UploadImagesTask;
import com.globalshops.models.UploadProfileImageTask;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class BusinessAccountRepository {
    private MediatorLiveData<Boolean> isAuthenticated = new MediatorLiveData<>();
    private MediatorLiveData<BusinessAccount> _source = new MediatorLiveData<>();
    private MediatorLiveData<Boolean> isReAuthenticated = new MediatorLiveData<>();
    private MediatorLiveData<Boolean> isPasswordChanged = new MediatorLiveData<>();
    private MediatorLiveData<UploadProfileImageTask> uploadProfileImageTaskMediatorLiveData = new MediatorLiveData<>();
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Context context;

    public BusinessAccountRepository(FirebaseAuth auth, FirebaseFirestore db, FirebaseStorage storage, Context context) {
        this.auth = auth;
        this.db = db;
        this.context = context;
        this.storage = storage;
    }

    public LiveData<Boolean> login(String email, String password){
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            isAuthenticated.setValue(true);
                        }else {
                            isAuthenticated.setValue(false);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return isAuthenticated;
    }

    public LiveData<Boolean> reAuthenticateUser(String email, String password){
     FirebaseUser user = auth.getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, password);

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            isReAuthenticated.postValue(true);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                isReAuthenticated.postValue(false);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return isReAuthenticated;
    }
    public void sendPasswordResetEmail(String email){
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Email sent", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addBusinessDetails(BusinessAccount businessAccount){
        String doc = auth.getCurrentUser().getUid();
        businessAccount.setShopId(doc);

        db.collection("shops").document(doc)
                .collection("shop_info")
                .document(doc)
                .set(businessAccount, SetOptions.merge());
    }

    public LiveData<BusinessAccount> getBusinessAccountDetails(){
       String docRef = auth.getCurrentUser().getUid();
       db.collection("shops")
               .document(docRef)
               .collection("shop_info")
               .document(docRef)
               .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                   @Override
                   public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                       if (value != null && value.exists()){
                            BusinessAccount businessAccount = value.toObject(BusinessAccount.class);
                           _source.postValue(businessAccount);
                       }else {
                           _source.postValue(null);
                       }
                   }
               });
       return _source;
    }

    public void updateAuthUserEmail(String email){
        FirebaseUser shop = auth.getCurrentUser();

        shop.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            updateFirestoreEmail(email);
                            Toast.makeText(context, "Email updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateFirestoreEmail(String email){
        String doc = auth.getCurrentUser().getUid();
        DocumentReference docRef = db.collection("shops")
                .document(doc)
                .collection("shop_info")
                .document(doc);

        docRef.update("email", email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public LiveData<Boolean> changePassword(String password){
        FirebaseUser user = auth.getCurrentUser();
        user.updatePassword(password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            isPasswordChanged.postValue(true);
                            Toast.makeText(context, "Password changed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                isPasswordChanged.postValue(false);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
       return isPasswordChanged;
    }

    public LiveData<UploadProfileImageTask> uploadImage(Uri uri, String imageName) {
        String shopUniqueId = auth.getCurrentUser().getUid();
        StorageReference storageReference = storage.getReference().child(shopUniqueId+"/profile_image/" + imageName);
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
                                    UploadProfileImageTask uploadImageTask = new UploadProfileImageTask(imageUrl, true);
                                    uploadProfileImageTaskMediatorLiveData.postValue(uploadImageTask);
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
        return uploadProfileImageTaskMediatorLiveData;
    }

    public void addShopIDToUtils(){
        String shopId = auth.getCurrentUser().getUid();
        db.collection("utils")
                .document("shops_uid")
                .update("shopsUidList", FieldValue.arrayUnion(shopId));
    }
}
