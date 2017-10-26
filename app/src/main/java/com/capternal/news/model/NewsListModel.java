package com.capternal.news.model;

import java.io.Serializable;

/**
 * Created by jupitor on 25/10/17.
 */

public class NewsListModel implements Serializable {

    private int ViewType;
    private String object = "";
    private boolean showHeader = false;


    public NewsListModel(int viewType, String object, boolean showHeader) {
        ViewType = viewType;
        this.object = object;
        this.showHeader = showHeader;
    }

    public int getViewType() {
        return ViewType;
    }

    public void setViewType(int viewType) {
        ViewType = viewType;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public boolean isShowHeader() {
        return showHeader;
    }

    public void setShowHeader(boolean showHeader) {
        this.showHeader = showHeader;
    }
}

