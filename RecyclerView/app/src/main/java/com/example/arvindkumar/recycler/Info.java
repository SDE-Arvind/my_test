package com.example.arvindkumar.recycler;

/**
 * Created by Arvind Kumar on 24-Oct-16.
 */

public class Info {

    String mName;
    String mMobile;

    public int getImage() {
        return mImage;
    }

    public void setImage(int mImage) {
        this.mImage = mImage;
    }

    int mImage;

//    public Info(String iName,String iMobile)
//    {
//        setName(iName);
//        setMobile(iMobile);
//    }

    public Info(int id)
       {
           setImage(id);
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
