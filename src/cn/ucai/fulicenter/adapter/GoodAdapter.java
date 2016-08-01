package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.NewGoodBean;

/**
 * Created by sks on 2016/8/1.
 */
public class GoodAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    List<NewGoodBean> mNewGoodsList;
    GoodViewHolder mGoodViewHolder;

    public GoodAdapter(Context context, List<NewGoodBean> list) {
        mContext = context;
        mNewGoodsList = new ArrayList<NewGoodBean>();
        mNewGoodsList.addAll(list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_new_goods, null, false);
        return new GoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        NewGoodBean newGoodBean = mNewGoodsList.get(position);
        mGoodViewHolder.tvGoodName.setText(newGoodBean.getGoodsName());
        mGoodViewHolder.tvGoodPrice.setText(newGoodBean.getCurrencyPrice());
    }

    @Override
    public int getItemCount() {
        return mNewGoodsList.size();
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

}
