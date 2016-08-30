package ip.cynic.pullrecyclerview;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ip.cynic.pullrecycler.PullRecyclerView;
import ip.cynic.pullrecyclerview.adapter.GankAdapter;
import ip.cynic.pullrecyclerview.bean.GankGirl;
import ip.cynic.pullrecyclerview.network.NetworkClient;
import ip.cynic.pullrecyclerview.service.impl.GirlServiceImpl;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class GankActivity extends AppCompatActivity {

    private static final String TAG = "GankActivity";
    private List<GankGirl> datas = new ArrayList<>();

    private Observer<List<GankGirl>> mObserver = new Observer<List<GankGirl>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, e.getMessage());
            mPullRecyclerView.onRefreshComplete(false);
        }

        @Override
        public void onNext(List<GankGirl> gankGirls) {
            datas.addAll(gankGirls);
            page ++;
            mPullRecyclerView.onRefreshComplete(true);
        }
    };
    private PullRecyclerView mPullRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int page = 1;
    GankAdapter gankAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gank);
        mPullRecyclerView = (PullRecyclerView) findViewById(R.id.recycler_view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mPullRecyclerView.setSwipeRefreshLayout(mSwipeRefreshLayout);
        mPullRecyclerView.setLayoutManager(layoutManager);
        gankAdapter = new GankAdapter(datas);
        mPullRecyclerView.setAdapter(gankAdapter);
        NetworkClient.getGankApi()
                .getGankGirls(20, page)
                .map(GirlServiceImpl.GankResultMapFunc.getInstance())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mObserver);

        mPullRecyclerView.setOnPullRefreshListener(new PullRecyclerView.PullRecyclerRefreshListener() {
            @Override
            public void loadMore() {
                NetworkClient.getGankApi()
                        .getGankGirls(20, page)
                        .map(GirlServiceImpl.GankResultMapFunc.getInstance())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(mObserver);
            }

            @Override
            public void onRefresh() {
                NetworkClient.getGankApi()
                        .getGankGirls(20, 1)
                        .map(GirlServiceImpl.GankResultMapFunc.getInstance())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<List<GankGirl>>() {
                            @Override
                            public void call(List<GankGirl> gankGirls) {
                                datas.clear();
                                datas.addAll(gankGirls);
                                page = 2;
                                mPullRecyclerView.onRefreshComplete(true);
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Log.e(TAG, throwable.getMessage());
                                mPullRecyclerView.onRefreshComplete(false);
                            }
                        });
            }
        });
    }
}
