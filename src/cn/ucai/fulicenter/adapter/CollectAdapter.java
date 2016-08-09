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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.GoodDetailsActivity;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.footer.FooterHolder;
import cn.ucai.fulicenter.task.DownloadCollectCountTask;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.utils.OkHttpUtils2;

/**
 * Created by sks on 2016/8/1.
 */
public class CollectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = CollectAdapter.class.getSimpleName();
    Context mContext;
    List<CollectBean> mCollectList;
    boolean isMore;
    final static int FooterType = 0;
    final static int ItemType = 1;
    String FooterText;
    int sortBy;

    public void setFooterText(String footerText) {
        FooterText = footerText;
        notifyDataSetChanged();
    }

    public void setSortBy(int sortBy) {
        this.sortBy = sortBy;
        notifyDataSetChanged();
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public CollectAdapter(Context context, List<CollectBean> list) {
        mContext = context;
        mCollectList = new ArrayList<>();
        mCollectList.addAll(list);
        sortBy = I.SORT_BY_ADDTIME_DESC;
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
                view = LayoutInflater.from(mContext).inflate(R.layout.item_collect, parent, false);
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
        final CollectBean collectBean = mCollectList.get(position);
        GoodViewHolder viewHolder = (GoodViewHolder) holder;
        final CollectBean good = mCollectList.get(position);
        Log.e(TAG, "good="+good.toString());
        Log.e(TAG, "GoodsThumb="+good.getGoodsThumb());
        ImageUtils.setGoodThumb(mContext,viewHolder.ivGoodThumb,good.getGoodsThumb());
        viewHolder.tvGoodName.setText(collectBean.getGoodsName());
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, GoodDetailsActivity.class)
                .putExtra(D.GoodDetails.KEY_GOODS_ID,good.getGoodsId()));
            }
        });
        viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkHttpUtils2<MessageBean> utils2 = new OkHttpUtils2<MessageBean>();
                utils2.setRequestUrl(I.REQUEST_DELETE_COLLECT)
                        .addParam(I.Collect.USER_NAME, FuliCenterApplication.getInstance().getUserName())
                        .addParam(I.Collect.GOODS_ID,String.valueOf(collectBean.getGoodsId()))
                        .targetClass(MessageBean.class)
                        .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                            @Override
                            public void onSuccess(MessageBean result) {
                                Log.e(TAG, "result=" + result);
                                if (result != null && result.isSuccess()) {
                                    mCollectList.remove(collectBean);
                                    new DownloadCollectCountTask(FuliCenterApplication.getInstance().getUserName(), mContext).execute();
                                    notifyDataSetChanged();
                                } else {
                                    Log.e(TAG, "删除失败");
                                }
                                Toast.makeText(mContext, result.getMsg(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String error) {
                                Log.e(TAG, "error=" + error);
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
//        return mNewGoodsList.size();
        return mCollectList==null?1:mCollectList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return FooterType;
        } else {
            return ItemType;
        }
    }

    public void initData(List<CollectBean> list) {
        Log.e(TAG, "list=" + list.toString());
        mCollectList.clear();
        mCollectList.addAll(list);
        notifyDataSetChanged();
    }

    public void addAllList(List<CollectBean> newGoodList) {
        mCollectList.addAll(newGoodList);
    }

    class GoodViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        ImageView ivGoodThumb;
        TextView tvGoodName;
        ImageView ivDelete;

        public GoodViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.layout_good);
            ivGoodThumb = (ImageView) itemView.findViewById(R.id.niv_good_thumb);
            tvGoodName = (TextView) itemView.findViewById(R.id.tv_good_name);
            ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
        }
    }
}
