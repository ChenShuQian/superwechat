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

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.BoutiqueAdapter;
import cn.ucai.fulicenter.adapter.GoodAdapter;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.utils.OkHttpUtils2;

/**
 * A simple {@link Fragment} subclass.
 */
public class BoutiqueFragment extends Fragment {
    private final static String TAG = BoutiqueFragment.class.getSimpleName();
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    List<BoutiqueBean> mBoutiqueList;
    Context mContext;

    TextView mtvFreshHint;
    int pageId = 0;
    int pageSize = 10;
    int action = I.ACTION_DOWNLOAD;
    LinearLayoutManager mLinearLayoutManager;
    BoutiqueAdapter mAdapter;
    public BoutiqueFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();
        View view = inflater.inflate(R.layout.fragment_boutique, container, false);
        mBoutiqueList = new ArrayList<>();
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
                int firstPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
                lastPosition = mLinearLayoutManager.findLastVisibleItemPosition();
                Log.e(TAG, "first=" + firstPosition + ",last=" + lastPosition);
                mSwipeRefreshLayout.setEnabled(mLinearLayoutManager.findFirstVisibleItemPosition() == 0);
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
        findNewFoodsList(new OkHttpUtils2.OnCompleteListener<BoutiqueBean[]>() {
            @Override
            public void onSuccess(BoutiqueBean[] result) {
                Log.e(TAG, "result=" + result);
                mtvFreshHint.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                mAdapter.setFooterText("加载更多...");
                mAdapter.setMore(true);
                if (result != null) {
                    Log.e(TAG, "result=" + result.length);
                    List<BoutiqueBean> boutiqueList = Arrays.asList(result);
                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                        mAdapter.initData(boutiqueList);
                    } else {
                        mAdapter.addAllList(boutiqueList);
                    }
                    if (boutiqueList.size() < pageSize) {
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

    private void findNewFoodsList(OkHttpUtils2.OnCompleteListener<BoutiqueBean[]> listener) {
        final OkHttpUtils2<BoutiqueBean[]> utils2 = new OkHttpUtils2<>();
        utils2.setRequestUrl(I.REQUEST_FIND_BOUTIQUES)
                .targetClass(BoutiqueBean[].class)
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
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mLinearLayoutManager.setOrientation(LinearLayout.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new BoutiqueAdapter(mContext, mBoutiqueList);
        mRecyclerView.setAdapter(mAdapter);

        mtvFreshHint = (TextView) view.findViewById(R.id.tvFreshHint);
    }

}
