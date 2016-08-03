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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.BoutiqueSortActivity;
import cn.ucai.fulicenter.activity.GoodDetailsActivity;
import cn.ucai.fulicenter.activity.NewGoodsFragment;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.footer.FooterHolder;
import cn.ucai.fulicenter.utils.ImageUtils;

/**
 * Created by sks on 2016/8/1.
 */
public class BoutiqueAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = BoutiqueAdapter.class.getSimpleName();
    Context mContext;
    List<BoutiqueBean> mNewGoodsList;
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

    public BoutiqueAdapter(Context context, List<BoutiqueBean> list) {
        mContext = context;
        mNewGoodsList = new ArrayList<>();
        mNewGoodsList.addAll(list);
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
                view = LayoutInflater.from(mContext).inflate(R.layout.item_boutique, parent, false);
                holder = new BoutiqueViewHolder(view);
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
        BoutiqueBean BoutiqueBean = mNewGoodsList.get(position);
        BoutiqueViewHolder viewHolder = (BoutiqueViewHolder) holder;
        final BoutiqueBean good = mNewGoodsList.get(position);
        Log.e(TAG, "good="+good.toString());
        ImageUtils.setGoodThumb(mContext,viewHolder.ivBoutiqueImg,good.getImageurl());
        viewHolder.tvBoutiqueTitle.setText(BoutiqueBean.getTitle());
        viewHolder.tv_boutique_name.setText(BoutiqueBean.getName());
        viewHolder.tv_boutique_description.setText(BoutiqueBean.getDescription());
        viewHolder.layout_boutique_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, BoutiqueSortActivity.class)
                .putExtra(D.Boutique.KEY_ID,good.getId()));
            }
        });
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

    public void initData(List<BoutiqueBean> list) {
        mNewGoodsList.clear();
        mNewGoodsList.addAll(list);
        notifyDataSetChanged();
    }

    public void addAllList(List<BoutiqueBean> newGoodList) {
        mNewGoodsList.addAll(newGoodList);
    }

    class BoutiqueViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout layout_boutique_item;
        ImageView ivBoutiqueImg;
        TextView tvBoutiqueTitle;
        TextView tv_boutique_name;
        TextView tv_boutique_description;

        public BoutiqueViewHolder(View itemView) {
            super(itemView);
            layout_boutique_item = (RelativeLayout) itemView.findViewById(R.id.layout_boutique_item);
            ivBoutiqueImg = (ImageView) itemView.findViewById(R.id.ivBoutiqueImg);
            tvBoutiqueTitle = (TextView) itemView.findViewById(R.id.tvBoutiqueTitle);
            tv_boutique_name = (TextView) itemView.findViewById(R.id.tv_boutique_name);
            tv_boutique_description = (TextView) itemView.findViewById(R.id.tv_boutique_description);
        }
    }
}
