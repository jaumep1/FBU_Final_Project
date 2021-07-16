package com.example.fbu_final_project.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Tag")
public class Tag extends ParseObject {

    public static final String KEY_TAG = "tag";
    public String getTag() {
        return getString("tag");
    }


}
