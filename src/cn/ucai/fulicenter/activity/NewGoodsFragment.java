package cn.ucai.fulicenter.activity;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.GoodAdapter;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.task.DownloadCartListTask;
import cn.ucai.fulicenter.utils.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewGoodsFragment extends Fragment {
    private final static String TAG = NewGoodsFragment.class.getSimpleName();
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    List<NewGoodBean> mNewGoodList;
    Context mContext;

    TextView mtvFreshHint;
    int pageId = 0;
    int pageSize = 10;
    int action = I.ACTION_DOWNLOAD;
    GridLayoutManager mGridLayoutManager;
    GoodAdapter mAdapter;
    public NewGoodsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();
        View view = inflater.inflate(R.layout.fragment_new_goods, container, false);
        mNewGoodList = new ArrayList<>();
        initData();
        initView(view);
        setListener();
        return view;
    }

    private void initData() {
        /**下载首页数据*/
        downLoadGoodsList();
    }

    private void setListener() {
        /**下拉刷新*/
        setDownRefreshListener();/**/
        /**上拉加载*/
        setPullAddListener();
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
        findNewFoodsList(new OkHttpUtils2.OnCompleteListener<NewGoodBean[]>() {
            @Override
            public void onSuccess(NewGoodBean[] result) {
                Log.e(TAG, "result=" + result);
                mtvFreshHint.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                mAdapter.setFooterText("加载更多...");
                mAdapter.setMore(true);
                if (result != null) {
                    Log.e(TAG, "result=" + result.length);
                    List<NewGoodBean> newGoodList = Arrays.asList(result);
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

    private void findNewFoodsList(OkHttpUtils2.OnCompleteListener<NewGoodBean[]> listener) {
        final OkHttpUtils2<NewGoodBean[]> utils2 = new OkHttpUtils2<>();
        utils2.setRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS)
                .addParam(I.NewAndBoutiqueGood.CAT_ID, String.valueOf(I.CAT_ID))
                .addParam(I.PAGE_ID, String.valueOf(pageId))
                .addParam(I.PAGE_SIZE, String.valueOf(pageSize))
                .targetClass(NewGoodBean[].class)
                .execute(listener);
    }

    private void initView(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_new_good);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_yellow,
                R.color.google_red,
                R.color.google_green
        );
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvNewGood);
        mGridLayoutManager = new GridLayoutManager(mContext, I.COLUM_NUM);
        mGridLayoutManager.setOrientation(LinearLayout.VERTICAL);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mAdapter = new GoodAdapter(mContext, mNewGoodList);
        mRecyclerView.setAdapter(mAdapter);

        mtvFreshHint = (TextView) view.findViewById(R.id.tvFreshHint);
    }

}
