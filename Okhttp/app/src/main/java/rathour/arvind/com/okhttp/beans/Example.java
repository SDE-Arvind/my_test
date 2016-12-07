package rathour.arvind.com.okhttp.beans;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Arvind Kumar on 06-Dec-16.
 */

public class Example {


    @SerializedName("page")
    private String mPage;

    @SerializedName("per_page")
    private int mPerPage;

    @SerializedName("total")
    private int mTotal;

    @SerializedName("total_pages")
    private int mTotalPages;

    @SerializedName("data")
    private List<DataBean> mData;

    public String getPage() {
        return mPage;
    }

    public void setPage(String iPage) {
        this.mPage = iPage;
    }

    public int getPerPage() {
        return mPerPage;
    }

    public void setPerPage(int iPerPage) {
        this.mPerPage = iPerPage;
    }

    public int getTotal() {
        return mTotal;
    }

    public void setTotal(int iTotal) {
        this.mTotal = iTotal;
    }

    public int getTotalPages() {
        return mTotalPages;
    }

    public void setTotalPages(int iTotalPages) {
        this.mTotalPages = iTotalPages;
    }

    public List<DataBean> getData() {
        return mData;
    }

    public void setData(List<DataBean> data) {
        this.mData = data;
    }

    public static class DataBean {
        /**
         * id : 4
         * mFirstName : eve
         * last_name : holt
         * avatar : https://s3.amazonaws.com/uifaces/faces/twitter/marcoramires/128.jpg
         */
        @SerializedName("id")
        private int mId;
        @SerializedName("first_name")
        private String mFirstName;
        @SerializedName("last_name")
        private String mLastName;
        @SerializedName("avatar")
        private String mAvatar;

        public int getId() {
            return mId;
        }

        public void setId(int iId) {
            this.mId = iId;
        }

        public String getFirstName() {
            return mFirstName;
        }

        public void setFirstName(String iFirstName) {
            this.mFirstName = iFirstName;
        }

        public String getLastName() {
            return mLastName;
        }

        public void setLasName(String iLastName) {
            this.mLastName = iLastName;
        }

        public String getAvatar() {
            return mAvatar;
        }

        public void setAvatar(String iAvatar) {
            this.mAvatar = iAvatar;
        }
    }
}
