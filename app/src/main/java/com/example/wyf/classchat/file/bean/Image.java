package com.example.wyf.classchat.file.bean;

/**
 * Created by WYF on 2017/10/30.
 */

public class Image extends AFile {
    private String path;
    private boolean isSelected;

    public Image(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
