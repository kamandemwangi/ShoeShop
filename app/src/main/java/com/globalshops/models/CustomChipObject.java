package com.globalshops.models;

import android.hardware.lights.LightState;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Handler;

public class CustomChipObject {
   private List<String> chipList;
   private boolean isChecked;

    public CustomChipObject() {
    }

    public CustomChipObject(List<String> chipList, boolean isChecked) {
        this.chipList = chipList;
        this.isChecked = isChecked;
    }

    public List<String> getChipList() {
        return chipList;
    }

    public void setChipList(List<String> chipList) {
        this.chipList = chipList;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
