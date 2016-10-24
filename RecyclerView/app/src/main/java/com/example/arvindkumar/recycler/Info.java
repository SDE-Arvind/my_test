package com.example.arvindkumar.recycler;

/**
 * Created by Arvind Kumar on 24-Oct-16.
 */

public class Info {

    String mName;
    String mMobile;

    public Info(String mName,String mMobile)
    {
        this.mMobile=mMobile;
        this.mName=mName;
    }
    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getMobile() {
        return mMobile;
    }

    public void setMobile(String mMobile) {
        this.mMobile = mMobile;
    }

}
