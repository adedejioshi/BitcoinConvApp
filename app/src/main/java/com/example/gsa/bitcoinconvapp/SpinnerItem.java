package com.example.gsa.bitcoinconvapp;

/**
 * Created by GSA on 10/23/2017.
 * Custom Spinner item with images and texts.
 */
public class SpinnerItem {
    private String shortName, fullName;
    private int imageResource;

    public SpinnerItem(String shortName,String fullName,int imageResource) {
        this.shortName = shortName;
        this.fullName = fullName;
        this.imageResource = imageResource;
    }

    public String getShortName() {
        return this.shortName;
    }
    public String getFullName() {
        return this.fullName;
    }
    public int getImageResource(){
        return this.imageResource;
    }
}
