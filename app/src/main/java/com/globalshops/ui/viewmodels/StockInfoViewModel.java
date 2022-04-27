package com.globalshops.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.globalshops.models.Shoe;
import com.globalshops.repositories.StockInfoRepository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class StockInfoViewModel extends ViewModel {

    private StockInfoRepository stockInfoRepository;

    @Inject
    public StockInfoViewModel(StockInfoRepository stockInfoRepository) {
        this.stockInfoRepository = stockInfoRepository;
    }

    public LiveData<List<Shoe>> getShoeList(){
        return stockInfoRepository.getShoes();
    }

    public LiveData<Boolean> updateShoeSizeQuantitySingleRow(Map<String, Object> oldValue, Map<String, Object> newValue, String shoeProductId, String field){
       return stockInfoRepository.updateShoeSizesSingleRow(oldValue, newValue, shoeProductId, field);
    }

    public void reArrangeSelectedShoeSizesArray(String arrayValue, String shoeProductId, String field){
       stockInfoRepository.reArrangeSelectedShoeSizeArray(arrayValue, shoeProductId, field);
    }

    public LiveData<Boolean> removeSingleShoeSizeQuantity(Map<String, Object> map, String shoeProductId, String field, String shoeSizesListField, String value){
       return stockInfoRepository.removeSingleRowShoeSizesQuantity(map, shoeProductId, field, shoeSizesListField, value);
    }

    public LiveData<Boolean> updateShoeDetails(Shoe shoe, String shoeProductId){
        return stockInfoRepository.updateShoeDetails(shoe, shoeProductId);
    }

    public LiveData<Boolean> deleteShoe(String shoeProductId, List<String> imageNames){
        return stockInfoRepository.deleteShoe(shoeProductId, imageNames);
    }
    public LiveData<List<Shoe>> getOrders(){
        return stockInfoRepository.getOrders();
    }

    public LiveData<Boolean> updateOrderStatus(String orderNumber, String orderStatus){
        return stockInfoRepository.updateOrderStatus(orderNumber, orderStatus);
    }
}
