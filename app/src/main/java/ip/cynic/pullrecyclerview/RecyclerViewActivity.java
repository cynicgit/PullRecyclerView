package ip.cynic.pullrecyclerview;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import ip.cynic.pullrecycler.PullRecyclerView;
import ip.cynic.pullrecyclerview.adapter.MyAdapter;


public class RecyclerViewActivity extends AppCompatActivity {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private PullRecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intiData();
        mRecyclerView = (PullRecyclerView) findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(datas);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setSwipeRefreshLayout(mSwipeRefreshLayout);
        mRecyclerView.setOnPullRefreshListener(new PullRecyclerView.PullRecyclerRefreshListener() {
            @Override
            public void loadMore() {
                new Thread() {
                    @Override
                    public void run() {
                        SystemClock.sleep(2000);
                        refreshData();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mRecyclerView.onRefreshComplete(true);
                            }
                        });
                    }
                }.start();

            }

            @Override
            public void onRefresh() {
                mRecyclerView.onRefreshComplete(false);
            }
        });
        mAdapter.setOnItemClickListener(new PullRecyclerView.PullRefreshAdapter.PullOnItemClickListener() {
            @Override
            public void onClickItemListen(int position) {
                String s = datas.get(position);
                Snackbar.make(mRecyclerView, s, Snackbar.LENGTH_SHORT).show();
            }
        });

    }

    private List<String> datas = new ArrayList<>();

    private void intiData() {
        for (int i = 0; i < 15; i++) {
            datas.add("item " + i);
        }
    }

    private void refreshData() {
        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            datas.add("item " + i);
        }
        this.datas.addAll(datas);
    }
}
