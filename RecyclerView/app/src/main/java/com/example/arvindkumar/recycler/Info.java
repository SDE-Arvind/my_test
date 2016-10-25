package com.example.arvindkumar.recycler;

/**
 * Created by Arvind Kumar on 24-Oct-16.
 */

public class Info {

    String mName;
    String mMobile;

    public Info(String iName,String iMobile)
    {
        setName(iName);
        setMobile(iMobile);
    }
    public String getName() {
        return mName;
    }

    public void setName(String iName) {
        this.mName = iName;
    }

    public String getMobile() {
        return mMobile;
    }

    public void setMobile(String iMobile) {
        this.mMobile = iMobile;
    }

}
