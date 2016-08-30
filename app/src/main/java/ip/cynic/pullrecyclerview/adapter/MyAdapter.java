package ip.cynic.pullrecyclerview.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ip.cynic.pullrecycler.PullRecyclerView;
import ip.cynic.pullrecyclerview.R;


/**
 * Created by cynic on 2016/8/29.
 *
 */
public class MyAdapter extends PullRecyclerView.PullRefreshAdapter {

    private List<String> datas;

    public MyAdapter(List<String> datas) {
        this.datas = datas;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(holder instanceof ItemHolder) {
            ((ItemHolder) holder).refreshDataUI(datas.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (datas != null) {
            return datas.size() == 0 ? 0 : datas.size() + 1;
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onItemHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ItemHolder(view);
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        private final TextView mTextView;

        public ItemHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.text);
        }

        public void refreshDataUI(String data) {
            mTextView.setText(data);
        }

    }



}
