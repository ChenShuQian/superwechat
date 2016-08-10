package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
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

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.GoodDetailsActivity;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.footer.FooterHolder;
import cn.ucai.fulicenter.utils.ImageUtils;

/**
 * Created by sks on 2016/8/1.
 */
public class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = CartAdapter.class.getSimpleName();
    Context mContext;
    List<CartBean> mNewGoodsList;
    boolean isMore;

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public CartAdapter(Context context, List<CartBean> list) {
        mContext = context;
        mNewGoodsList = new ArrayList<>();
        mNewGoodsList.addAll(list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder = null;
        Log.e(TAG, "viewType=" + viewType);
        view = LayoutInflater.from(mContext).inflate(R.layout.item_cart, parent, false);
        holder = new GoodViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CartBean goodDetails = mNewGoodsList.get(position);
        GoodViewHolder viewHolder = (GoodViewHolder) holder;
        final CartBean good = mNewGoodsList.get(position);
        ImageUtils.setGoodThumb(mContext,viewHolder.iv_cart_thumb,good.getGoods().getGoodsThumb());
        viewHolder.tv_cart_goodname.setText(goodDetails.getGoods().getGoodsName());
        viewHolder.tv_cart_good_count.setText(goodDetails.getCount());
        viewHolder.tv_cart_price.setText(goodDetails.getGoods().getCurrencyPrice());
        viewHolder.layout_good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, GoodDetailsActivity.class)
                .putExtra(D.GoodDetails.KEY_GOODS_ID,good.getGoodsId()));
            }
        });
    }

    @Override
    public int getItemCount() {
//        return mNewGoodsList.size();
        return mNewGoodsList==null?0:mNewGoodsList.size();
    }

    public void initData(List<CartBean> list) {
        Log.e(TAG, "list=" + list.toString());
        mNewGoodsList.clear();
        mNewGoodsList.addAll(list);
        notifyDataSetChanged();
    }

    public void addAllList(List<CartBean> newGoodList) {
        mNewGoodsList.addAll(newGoodList);
    }

    class GoodViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout_good;
        ImageView iv_cart_thumb;
        TextView tv_cart_goodname;
        TextView tv_cart_good_count;
        TextView tv_cart_price;

        public GoodViewHolder(View itemView) {
            super(itemView);
            layout_good = (LinearLayout) itemView.findViewById(R.id.layout_good);
            iv_cart_thumb = (ImageView) itemView.findViewById(R.id.iv_cart_thumb);
            tv_cart_goodname = (TextView) itemView.findViewById(R.id.tv_cart_goodname);
            tv_cart_good_count = (TextView) itemView.findViewById(R.id.tv_cart_good_count);
            tv_cart_price = (TextView) itemView.findViewById(R.id.tv_cart_price);
        }
    }
}
