package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.footer.FooterHolder;
import cn.ucai.fulicenter.utils.ImageUtils;

/**
 * Created by sks on 2016/8/1.
 */
public class GoodAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = GoodAdapter.class.getSimpleName();
    Context mContext;
    List<NewGoodBean> mNewGoodsList;
    boolean isMore;
    final static int FooterType = 0;
    final static int ItemType = 1;
    String FooterText;

    public void setFooterText(String footerText) {
        FooterText = footerText;
        notifyDataSetChanged();
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public GoodAdapter(Context context, List<NewGoodBean> list) {
        mContext = context;
        mNewGoodsList = new ArrayList<>();
        mNewGoodsList.addAll(list);
        soryByAddTime();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder = null;
        Log.e(TAG, "viewType=" + viewType);
        switch (viewType) {
            case FooterType:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_footer, parent, false);
                holder = new FooterHolder(view);
                break;
            case ItemType:
                view = LayoutInflater.from(mContext).inflate(R.layout.new_good, parent, false);
                holder = new GoodViewHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position)==FooterType) {
            ((FooterHolder) holder).tvFooterText.setText(FooterText);
            return;
        }
        NewGoodBean newGoodBean = mNewGoodsList.get(position);
        GoodViewHolder viewHolder = (GoodViewHolder) holder;
        NewGoodBean good = mNewGoodsList.get(position);
        Log.e(TAG, "good="+good.toString());
        Log.e(TAG, "GoodsThumb="+good.getGoodsThumb());
        ImageUtils.setGoodThumb(mContext,viewHolder.ivGoodThumb,good.getGoodsThumb());
        viewHolder.tvGoodName.setText(newGoodBean.getGoodsName());
        viewHolder.tvGoodPrice.setText(newGoodBean.getCurrencyPrice());
    }

    @Override
    public int getItemCount() {
        return mNewGoodsList==null?1:mNewGoodsList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return FooterType;
        } else {
            return ItemType;
        }
    }

    public void initData(List<NewGoodBean> list) {
        mNewGoodsList.clear();
        mNewGoodsList.addAll(list);
        soryByAddTime();
        notifyDataSetChanged();
    }

    public void addAllList(List<NewGoodBean> newGoodList) {
        soryByAddTime();
        mNewGoodsList.addAll(newGoodList);
    }

    class GoodViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        ImageView ivGoodThumb;
        TextView tvGoodName;
        TextView tvGoodPrice;

        public GoodViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.layout_good);
            ivGoodThumb = (ImageView) itemView.findViewById(R.id.niv_good_thumb);
            tvGoodName = (TextView) itemView.findViewById(R.id.tv_good_name);
            tvGoodPrice = (TextView) itemView.findViewById(R.id.tv_good_price);
        }
    }

    private void soryByAddTime() {
        Collections.sort(mNewGoodsList, new Comparator<NewGoodBean>() {
            @Override
            public int compare(NewGoodBean good1, NewGoodBean good2) {
                return (int) (Long.valueOf(good2.getAddTime()) - Long.valueOf(good1.getAddTime()));
            }
        });
    }
}
