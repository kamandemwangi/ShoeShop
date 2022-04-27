package com.globalshops.repositories;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.globalshops.models.Shoe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockInfoRepository {
    private static final String TAG = "StockIfoRepository";
    private MediatorLiveData<List<Shoe>> shoeMediatorLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Boolean> isRowUpdated = new MediatorLiveData<>();
    private MediatorLiveData<Boolean> isRowRemoved = new MediatorLiveData<>();
    private MediatorLiveData<Boolean> isShoeUpdated = new MediatorLiveData<>();
    private MediatorLiveData<Boolean> isShoeDeleted = new MediatorLiveData<>();
    private List<Shoe> shoeList = new ArrayList<>();
    private List<Shoe> ordersList = new ArrayList<>();
    private MediatorLiveData<List<Shoe>> _orders = new MediatorLiveData<>();
    private MediatorLiveData<Boolean> isOrderStatusUpdated = new MediatorLiveData<>();
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Context context;

    public StockInfoRepository(FirebaseAuth auth, FirebaseFirestore db, FirebaseStorage storage, Context context) {
        this.auth = auth;
        this.db = db;
        this.storage = storage;
        this.context = context;
    }

    public LiveData<List<Shoe>> getShoes(){
        String shopUniqueId = auth.getCurrentUser().getUid();
        db.collection("shops")
                .document(shopUniqueId)
                .collection("products")
                .orderBy("productId", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (shoeList.size() > 0){
                            shoeList.clear();
                        }
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                            Shoe shoe = snapshot.toObject(Shoe.class);
                            shoeList.add(shoe);
                            shoeMediatorLiveData.postValue(shoeList);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return shoeMediatorLiveData;
    }

    public LiveData<Boolean> updateShoeSizesSingleRow(Map<String, Object> oldValue, Map<String, Object> newValue, String shoeProductId, String field){
        String shopUniqueId = auth.getCurrentUser().getUid();
        db.collection("shops")
                .document(shopUniqueId)
                .collection("products")
                .document(shoeProductId)
                .update(field, FieldValue.arrayRemove(oldValue))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            arrayUnionShoeSizesQuantity(newValue, shoeProductId, field);
                            isRowUpdated.postValue(true);
                        }else {
                            isRowUpdated.postValue(false);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return isRowUpdated;

    }

    private void arrayUnionShoeSizesQuantity(Map<String, Object> map, String shoeProductId, String field){
        String shopUniqueId = auth.getCurrentUser().getUid();
        db.collection("shops")
                .document(shopUniqueId)
                .collection("products")
                .document(shoeProductId)
                .update(field, FieldValue.arrayUnion(map))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Successful update: "+shoeProductId, Toast.LENGTH_SHORT).show();
                        }
                        else Toast.makeText(context, "Failed to update: "+shoeProductId, Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void reArrangeSelectedShoeSizeArray(String arrayValue, String shoeProductId, String field){
        String shopUniqueId = auth.getCurrentUser().getUid();

        db.collection("shops")
                .document(shopUniqueId)
                .collection("products")
                .document(shoeProductId)
                .update(field, FieldValue.arrayRemove(arrayValue))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            selectedShoeSizesArrayUnion(arrayValue, shoeProductId, field);
                        }else {

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectedShoeSizesArrayUnion(String arrayValue, String shoeProductId, String field){
        String shopUniqueId = auth.getCurrentUser().getUid();

        db.collection("shops")
                .document(shopUniqueId)
                .collection("products")
                .document(shoeProductId)
                .update(field, FieldValue.arrayUnion(arrayValue))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                        } else{
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public LiveData<Boolean> removeSingleRowShoeSizesQuantity(Map<String, Object> map, String shoeProductId, String field, String shoeSizesListField, String value){
        String shopUniqueId = auth.getCurrentUser().getUid();
        db.collection("shops")
                .document(shopUniqueId)
                .collection("products")
                .document(shoeProductId)
                .update(field, FieldValue.arrayRemove(map))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            removeSingleRowShoeSizesKey(shoeProductId, shoeSizesListField, value);
                            Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                            isRowRemoved.postValue(true);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return isRowRemoved;
    }
    private void removeSingleRowShoeSizesKey(String shoeProductId, String shoeSizesListField, String value){
        String shopUniqueId = auth.getCurrentUser().getUid();
        db.collection("shops")
                .document(shopUniqueId)
                .collection("products")
                .document(shoeProductId)
                .update(shoeSizesListField, FieldValue.arrayRemove(value))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public LiveData<Boolean> updateShoeDetails(Shoe shoe, String shoeProductId){
        String shopUniqueId = auth.getCurrentUser().getUid();
        db.collection("shops")
                .document(shopUniqueId)
                .collection("products")
                .document(shoeProductId)
                .update("shoePriceTag", shoe.getShoePriceTag(),
                        "name", shoe.getName(),
                        "gender", shoe.getGender(),
                        "shortDesc", shoe.getShortDesc())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Details updated", Toast.LENGTH_SHORT).show();
                            isShoeUpdated.postValue(true);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return isShoeUpdated;
    }

    public LiveData<Boolean> deleteShoe(String shoeProductId, List<String> imageNames){
        String shopUniqueId = auth.getCurrentUser().getUid();
        db.collection("shops")
                .document(shopUniqueId)
                .collection("products")
                .document(shoeProductId)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            deleteShoeImages(imageNames);
                            Toast.makeText(context, "Shoe delete", Toast.LENGTH_SHORT).show();
                            isShoeDeleted.postValue(true);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return isShoeDeleted;
    }

    private void deleteShoeImages(List<String> imageNames){
        String shopUniqueId = auth.getCurrentUser().getUid();
        StorageReference reference = storage.getReference().child(shopUniqueId+"/shoe_images/");
        Log.d(TAG, "deleteShoeImages: "+imageNames.get(0));

        for (int i = 0; i < imageNames.size(); i++){
            reference.child(imageNames.get(i))
                    .delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(context, "Images Delete", Toast.LENGTH_SHORT).show();
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

    public LiveData<List<Shoe>> getOrders(){
        if (ordersList.size() > 0){
            ordersList.clear();
        }
        String shopId = auth.getCurrentUser().getUid();
        db.collection("shops")
                .document(shopId)
                .collection("orders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                Shoe shoe = snapshot.toObject(Shoe.class);
                               ordersList.add(shoe);
                               _orders.postValue(ordersList);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return _orders;
    }
    public LiveData<Boolean> updateOrderStatus(String orderNumber, String orderStatus){
        String shopId = auth.getCurrentUser().getUid();
        db.collection("shops")
                .document(shopId)
                .collection("orders")
                .document(orderNumber)
                .update("orderStatus", orderStatus)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            isOrderStatusUpdated.postValue(true);
                            Toast.makeText(context, "Order "+orderStatus, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return isOrderStatusUpdated;
    }
}
