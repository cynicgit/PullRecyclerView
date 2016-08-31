# PullRecyclerView
RecyclerView上拉刷新

1. 布局
      
		<android.support.v4.widget.SwipeRefreshLayout
	        android:id="@+id/swipe_refresh"
	        android:layout_width="match_parent"
	        android:layout_height="match_parent">
	        <!--is_range_changed 参数为使用局部刷新 建议item中有图片的使用该参数-->
	        <ip.cynic.pullrecycler.PullRecyclerView
	            app:is_range_changed="true"
	            android:id="@+id/recycler_view"
	            android:layout_width="match_parent"
	            android:layout_height="match_parent"/>
	
	    </android.support.v4.widget.SwipeRefreshLayout>

2. adapter 继承 PullRefreshAdapter，瀑布流继承 StaggeredGridAdapter。 PullRefreshAdapter 已实现item点击事件实现 PullOnItemClickListener 接口即可。
3. 代码设置 
      
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

  ![nomral](https://github.com/cynicgit/PullRecyclerView/blob/master/img/nomarl.gif)    
  ![gank](https://github.com/cynicgit/PullRecyclerView/blob/master/img/gank.gif)
