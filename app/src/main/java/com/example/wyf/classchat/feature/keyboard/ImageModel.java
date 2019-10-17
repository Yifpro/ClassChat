package com.example.wyf.classchat.feature.keyboard;

/**
 * @author Administrator on 2018/2/24/024.
 */
public class ImageModel {

    private int res;
    private boolean isSelected;

    public ImageModel(int res, boolean isSelected) {
        this.res =  res;
        this.isSelected = isSelected;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
