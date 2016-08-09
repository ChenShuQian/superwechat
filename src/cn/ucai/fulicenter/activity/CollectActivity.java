package cn.ucai.fulicenter.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.CollectAdapter;
import cn.ucai.fulicenter.adapter.GoodAdapter;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.utils.OkHttpUtils2;
import cn.ucai.fulicenter.view.DisPlayUtils;

public class CollectActivity extends Activity {
    private final static String TAG = CollectActivity.class.getSimpleName();
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    List<CollectBean> mNewGoodList;

    TextView mtvFreshHint;
    int pageId = 0;
    int pageSize = 10;
    int mBoutiqueId;
    String mTatle;
    int action = I.ACTION_DOWNLOAD;
    GridLayoutManager mGridLayoutManager;
    CollectAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        mNewGoodList = new ArrayList<>();
        initData();
        initView();
        setListener();
    }

    private void initData() {
        String userName = FuliCenterApplication.getInstance().getUserName();
        if (userName.isEmpty()) {
            finish();
        }
        /**修改精选二级分类title*/
        DisPlayUtils.initBoutique(this,mTatle);
        Log.e(TAG, "mBoutiqueId=" + mBoutiqueId);
        Log.e(TAG, "mTatle=" + mTatle);
        downLoadGoodsList();
    }

    private void setListener() {
        /**下拉刷新*/
        setDownRefreshListener();
        /**上拉加载*/
        setPullAddListener();
        setUpdateCollectListReceiver();
    }

    private void setPullAddListener() {
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastPosition;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == recyclerView.SCROLL_STATE_IDLE && lastPosition == mAdapter.getItemCount() - 1) {
                    if (mAdapter.isMore()) {
                        action = I.ACTION_PULL_UP;
                        pageId += pageSize;
                        downLoadGoodsList();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstPosition = mGridLayoutManager.findFirstVisibleItemPosition();
                lastPosition = mGridLayoutManager.findLastVisibleItemPosition();
                Log.e(TAG, "first=" + firstPosition + ",last=" + lastPosition);
                mSwipeRefreshLayout.setEnabled(mGridLayoutManager.findFirstVisibleItemPosition() == 0);
            }
        });
    }

    private void setDownRefreshListener() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageId = 0;
                mtvFreshHint.setVisibility(View.VISIBLE);
                action = I.ACTION_PULL_DOWN;
                downLoadGoodsList();
            }
        });
    }


    private void downLoadGoodsList() {
        findNewFoodsList(new OkHttpUtils2.OnCompleteListener<CollectBean[]>() {
            @Override
            public void onSuccess(CollectBean[] result) {
                Log.e(TAG, "result=" + result);
                mtvFreshHint.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                mAdapter.setFooterText("加载更多...");
                mAdapter.setMore(true);
                if (result != null) {
                    Log.e(TAG, "result=" + result.length);
                    List<CollectBean> newGoodList = Arrays.asList(result);
                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                        mAdapter.initData(newGoodList);
                    } else {
                        mAdapter.addAllList(newGoodList);
                    }
                    if (newGoodList.size() < pageSize) {
                        mAdapter.setFooterText("没有更多数据...");
                        mAdapter.setMore(false);
                    }
                } else {
                    mAdapter.setFooterText("没有更多数据...");
                    mAdapter.setMore(false);
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "error=" + error);
                mtvFreshHint.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void findNewFoodsList(OkHttpUtils2.OnCompleteListener<CollectBean[]> listener) {
        final OkHttpUtils2<CollectBean[]> utils2 = new OkHttpUtils2<>();
        utils2.setRequestUrl(I.REQUEST_FIND_COLLECTS)
                .addParam(I.Collect.USER_NAME, FuliCenterApplication.getInstance().getUserName())
                .addParam(I.PAGE_ID, String.valueOf(pageId))
                .addParam(I.PAGE_SIZE, String.valueOf(pageSize))
                .targetClass(CollectBean[].class)
                .execute(listener);
    }

    private void initView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_collect);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_yellow,
                R.color.google_red,
                R.color.google_green
        );
        mRecyclerView = (RecyclerView) findViewById(R.id.rvCollect);
        mGridLayoutManager = new GridLayoutManager(this, I.COLUM_NUM);
        mGridLayoutManager.setOrientation(LinearLayout.VERTICAL);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mAdapter = new CollectAdapter(this, mNewGoodList);
        mRecyclerView.setAdapter(mAdapter);

        mtvFreshHint = (TextView) findViewById(R.id.tvFreshHint);
        DisPlayUtils.initBack(this);
        DisPlayUtils.initBoutique(this,"收藏的宝贝");
    }

    class UpdateCollectListReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            downLoadGoodsList();
        }
    }

    UpdateCollectListReceiver mReceiver;
    private void setUpdateCollectListReceiver() {
        mReceiver = new UpdateCollectListReceiver();
        IntentFilter filter = new IntentFilter("update_collect_list");
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }
}
