package com.example.arvindrathour.scrolllayout;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipyRefreshLayout.OnRefreshListener {
    /**
     * The dismiss time for {@link SwipyRefreshLayout}
     */
    public static final int DISMISS_TIMEOUT = 2000;

    public static  int PageNO = 1;

    ListView mListView;
    SwipyRefreshLayout swipyRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLayout();

    }

    private void initLayout() {
        mListView = (ListView) findViewById(R.id.listview);
        swipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipyrefreshlayout);


        mListView.setAdapter(new DummyListViewAdapter(this,getDummyStrings()));
        Log.d("page no", " "+PageNO++);
        swipyRefreshLayout.setOnRefreshListener(this);
        //set refresh scroll direction
        swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);
    }

    /**
     * Called when the {@link com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout}
     * is in refresh mode. Just for example purpose.
     */

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
//        Log.d("MainActivity", "Refresh triggered at "
//                + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));
        Log.d("page no", " "+PageNO++);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mListView.setAdapter(new DummyListViewAdapter(MainActivity.this,getDummyStrings()));
                //Hide the refresh after 2sec
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("tag","time out");
                        swipyRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }, DISMISS_TIMEOUT);
    }




    public static List<String> getDummyStrings() {
        List<String> dummyStrings = new ArrayList<>();

        dummyStrings.add("");
        dummyStrings.add("");
        dummyStrings.add("");
        dummyStrings.add("");
        dummyStrings.add("");
        dummyStrings.add("");
        dummyStrings.add("");
        dummyStrings.add("");
        dummyStrings.add("");
        dummyStrings.add("");
        dummyStrings.add("");
        dummyStrings.add("");
        dummyStrings.add("");
        dummyStrings.add("");
        dummyStrings.add("");
        dummyStrings.add("");
        dummyStrings.add("");
        dummyStrings.add("");
        dummyStrings.add("");
        dummyStrings.add("");

        return dummyStrings;
    }

}
