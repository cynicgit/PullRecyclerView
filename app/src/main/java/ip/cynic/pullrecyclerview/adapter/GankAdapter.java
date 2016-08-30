package ip.cynic.pullrecyclerview.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import ip.cynic.pullrecycler.adapter.StaggeredGridAdapter;
import ip.cynic.pullrecyclerview.R;
import ip.cynic.pullrecyclerview.bean.GankGirl;

/**
 * Created by cynic on 2016/8/30.
 */
public class GankAdapter extends StaggeredGridAdapter<GankGirl> {


    public GankAdapter(List<GankGirl> datas) {
        this.datas = datas;
    }

    @Override
    protected RecyclerView.ViewHolder onItemHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_girl, parent, false);
        return new GirlHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(holder instanceof GirlHolder)
            ((GirlHolder) holder).refreshDataUI(position);
    }


    public class GirlHolder extends RecyclerView.ViewHolder {

        TextView mTextView;
        ImageView mImageView;

        public GirlHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.grid_des);
            mImageView = (ImageView) itemView.findViewById(R.id.grid_img);
        }

        public void refreshDataUI(int position) {
            GankGirl gankGirl = datas.get(position);
            ViewGroup.LayoutParams params = mImageView.getLayoutParams();
            if(mTextView.getTag() != null) {
                params.height = (int) mTextView.getTag();
            } else {
                params.height = (int) (200 + Math.random() * 400);
            }
            mImageView.setLayoutParams(params);
            mTextView.setText(gankGirl.time);
            mTextView.setTag(params.height);
            Glide.with(itemView.getContext()).load(gankGirl.image_url).into(mImageView);
        }

    }
}
