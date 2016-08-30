package ip.cynic.pullrecycler;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;


/**
 * Created by cynic on 2016/8/29.
 */
public class PullRecyclerView extends RecyclerView implements SwipeRefreshLayout.OnRefreshListener {
    private final static int LinearLayoutManagerType = 0;
    private final static int GridLayoutManagerType = 1;
    private final static int StaggeredGridLayoutManagerType = 2;
    private static final String TAG = "PullRecyclerView";
    private int mLastVisibleItemPosition;
    private int layoutManagerType = -1;
    private int[] lastPositions;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private boolean isLoadingMore;
    private PullRefreshAdapter mAdapter;
    private int oldItemCount;
    private boolean isRangeChange;

    public PullRecyclerView(Context context) {
        this(context, null);
    }

    public PullRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullRecyclerView);
        isRangeChange = a.getBoolean(R.styleable.PullRecyclerView_is_range_changed, false);
        Log.e(TAG, "isRangeChange " + isRangeChange);
        a.recycle();
    }

    public void setSwipeRefreshLayout(SwipeRefreshLayout s) {
        mSwipeRefreshLayout = s;
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (!(adapter instanceof PullRefreshAdapter)) {
            throw new IllegalArgumentException("adapter must be extends PullRefreshAdapter");
        }
        oldItemCount = adapter.getItemCount();
        mAdapter = (PullRefreshAdapter) adapter;
        mAdapter.setPullOnFootClickListener(new PullRefreshAdapter.PullOnFootClickListener() {
            @Override
            public void onClickFootListen() {
                mAdapter.mFootHolder.reload();
                if (mRefreshListener != null) {
                    isLoadingMore = true;
                    mRefreshListener.loadMore();
                }
            }
        });
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManagerType == -1) {
            if (layoutManager instanceof LinearLayoutManager) {
                layoutManagerType = LinearLayoutManagerType;
            } else if (layoutManager instanceof GridLayoutManager) {
                layoutManagerType = GridLayoutManagerType;
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                layoutManagerType = StaggeredGridLayoutManagerType;
            } else {
                throw new RuntimeException("not support LayoutManagerType");
            }
        }

        switch (layoutManagerType) {
            case LinearLayoutManagerType:
                mLastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case GridLayoutManagerType:
                mLastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;

            case StaggeredGridLayoutManagerType:
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;

                if (lastPositions == null) {
                    lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                }
                staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                mLastVisibleItemPosition = findMax(lastPositions);
                break;

            default:
                break;
        }
    }

    private boolean isSetVisibleFull;

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        LayoutManager layoutManager = getLayoutManager();
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        if (totalItemCount > visibleItemCount && !isSetVisibleFull) {
            mAdapter.setVisibleFull(true);
            isSetVisibleFull = true;
        }
        if ((state == RecyclerView.SCROLL_STATE_IDLE
                || state == RecyclerView.SCROLL_STATE_SETTLING)
                && mLastVisibleItemPosition + 1 == mAdapter.getItemCount()
                && !isLoadingMore
                && totalItemCount > visibleItemCount) {
            if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
                mAdapter.notifyItemRemoved(mAdapter.getItemCount());
                return;
            }
            Log.e(TAG, "isLoadingMore");
            isLoadingMore = true;
            if (mRefreshListener != null) {
                mRefreshListener.loadMore();
            }
        }
    }

    private boolean isLoadOrOnRefresh;

    /**
     * 完成刷新时调用
     */
    public void onRefreshComplete(boolean success) {
        if (success && isLoadingMore) {
            isLoadingMore = false;
            if (oldItemCount == mAdapter.getItemCount()) {
                mAdapter.mFootHolder.isLast();
            } else {
                //瀑布流中建议使用局部刷新
                if(!isRangeChange) {
                    mAdapter.notifyDataSetChanged();
                    mAdapter.notifyItemRemoved(mAdapter.getItemCount());
                } else { //防止有大量图片刷新时白屏
                    mAdapter.notifyItemRangeChanged(oldItemCount - 1, mAdapter.getItemCount() - 1);
                    oldItemCount = mAdapter.getItemCount();
                }

            }
        } else if (success && !isLoadingMore) {
            oldItemCount = mAdapter.getItemCount();
            mSwipeRefreshLayout.setRefreshing(false);
            mAdapter.notifyDataSetChanged();
        } else {
            if (isLoadingMore) {
                isLoadingMore = false;
                isLoadOrOnRefresh = true;
                mAdapter.notifyItemRemoved(mAdapter.getItemCount());
            } else {
                isLoadOrOnRefresh = false;
                mSwipeRefreshLayout.setRefreshing(false);
            }
            Snackbar.make(this, R.string.pull_load_error, Snackbar.LENGTH_LONG).setAction(R.string.pull_reload, new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isLoadOrOnRefresh) {
                        isLoadingMore = true;
                        mAdapter.notifyItemInserted(mAdapter.getItemCount());
                        PullRecyclerView.this.smoothScrollToPosition(mAdapter.getItemCount());
                        mRefreshListener.loadMore();
                    } else {
                        mSwipeRefreshLayout.setRefreshing(true);
                        mRefreshListener.onRefresh();
                    }
                }
            }).show();
        }
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    private PullRecyclerRefreshListener mRefreshListener;

    public void setOnPullRefreshListener(PullRecyclerRefreshListener listener) {
        mRefreshListener = listener;
    }

    public interface PullRecyclerRefreshListener {
        void loadMore();

        void onRefresh();
    }

    @Override
    public void onRefresh() {
        if (mSwipeRefreshLayout == null) {
            throw new NullPointerException("call onRefresh method before must be setSwipeRefreshLayout() method");
        }
        if (mRefreshListener != null) {
            mRefreshListener.onRefresh();
        }
    }

    private static final int TYPE_FOOT = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_NULL = 2;

    public abstract static class PullRefreshAdapter<T> extends Adapter<ViewHolder> {

        protected List<T> datas;

        boolean isVisibleFull;
        protected BaseFootHolder mFootHolder;


        public void setVisibleFull(boolean b) {
            isVisibleFull = b;
        }

        public boolean isFoot(int position) {
            return position + 1 == getItemCount();
        }

        @Override
        public int getItemCount() {
            if(datas != null) {
                return datas.size() == 0 ? 0 : datas.size() + 1;
            }
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            if (position + 1 == getItemCount() && isVisibleFull) {
                return TYPE_FOOT;
            } else if (position + 1 == getItemCount() && !isVisibleFull) {
                return TYPE_NULL;
            } else {
                return TYPE_ITEM;
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType == TYPE_FOOT) {
                BaseFootHolder viewHolder = onFootHolder(parent, viewType);
                if(viewHolder !=null) {
                    mFootHolder = viewHolder;
                } else {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer, parent, false);
                    mFootHolder = new FootHolder(view);
                }

                return mFootHolder;
            } else if (viewType == TYPE_ITEM) {
                return onItemHolder(parent, viewType);
            } else if (viewType == TYPE_NULL) {
                TextView t = new TextView(parent.getContext());
                return new EmptyHolder(t);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            if (getItemViewType(position) == TYPE_ITEM) {
                holder.itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onClickItemListen(position);
                        }
                    }
                });
            } else if (getItemViewType(position) == TYPE_FOOT) {
                holder.itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOnFootClickListener != null) {
                            mOnFootClickListener.onClickFootListen();
                        }
                    }
                });
            }
        }

        public class FootHolder extends BaseFootHolder {
            private ProgressBar mPb;
            private TextView mTvLoad;

            public FootHolder(View itemView) {
                super(itemView);
                mPb = (ProgressBar) itemView.findViewById(R.id.pull_recycle_progressBar);
                mTvLoad = (TextView) itemView.findViewById(R.id.pull_recycle_tv_load);
            }

            @Override
            public void isLast() {
                mPb.setVisibility(View.GONE);
                mTvLoad.setText(R.string.pull_no_datas);
            }

            @Override
            public void reload() {
                mPb.setVisibility(View.VISIBLE);
                mTvLoad.setText(R.string.loading);
            }
        }

        public class EmptyHolder extends ViewHolder {

            public EmptyHolder(View itemView) {
                super(itemView);
            }

        }

        public abstract class BaseFootHolder extends ViewHolder {

            public BaseFootHolder(View itemView) {
                super(itemView);
            }

            public abstract void isLast();
            public abstract void reload();
        }

        protected abstract ViewHolder onItemHolder(ViewGroup parent, int viewType);
        protected BaseFootHolder onFootHolder(ViewGroup parent, int viewType) {
            return null;
        }

        private PullOnItemClickListener mOnItemClickListener;

        public void setOnItemClickListener(PullOnItemClickListener listener) {
            mOnItemClickListener = listener;
        }

        public interface PullOnItemClickListener {
            void onClickItemListen(int position);
        }

        private PullOnFootClickListener mOnFootClickListener;

        public interface PullOnFootClickListener {
            void onClickFootListen();
        }

        public void setPullOnFootClickListener(PullOnFootClickListener listener) {
            mOnFootClickListener = listener;
        }
    }
}
