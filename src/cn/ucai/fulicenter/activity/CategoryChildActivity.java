package cn.ucai.fulicenter.activity;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.GoodAdapter;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.utils.OkHttpUtils2;
import cn.ucai.fulicenter.view.CatChildFilterButton;
import cn.ucai.fulicenter.view.DisPlayUtils;

public class CategoryChildActivity extends Activity {
    private final static String TAG = CategoryChildActivity.class.getSimpleName();
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    List<NewGoodBean> mNewGoodList;

    Button mButton1;
    Button mButton2;

    TextView mtvFreshHint;
    int pageId = 0;
    int pageSize = 10;
    int mCategoryChildId;
    int action = I.ACTION_DOWNLOAD;
    GridLayoutManager mGridLayoutManager;
    GoodAdapter mAdapter;
    int sortBy;
    boolean mSortPriceAsc;
    boolean mSortAddTimeAsc;
    CatChildFilterButton mCatChildFilterButton;

    String name;
    ArrayList<CategoryChildBean> childList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_child);
        mNewGoodList = new ArrayList<>();
        sortBy = I.SORT_BY_PRICE_ASC;
        initData();
        initView();
        setListener();
    }

    private void initData() {
        mCategoryChildId = getIntent().getIntExtra(I.CategoryChild.CAT_ID, 0);
        name = getIntent().getStringExtra(I.CategoryGroup.NAME);
        Log.e(TAG, "name=" + name);
        childList = (ArrayList<CategoryChildBean>) getIntent().getSerializableExtra("childList");
        Log.e(TAG, "childList=" + childList);
        if (mCategoryChildId < 0) {
            return;
        }
        Log.e("main", "mCategoryChildId=" + mCategoryChildId);
        downLoadGoodsList();
    }

    private void setListener() {
        /**下拉刷新*/
        setDownRefreshListener();
        /**上拉加载*/
        setPullAddListener();
        SortStatusChangedListener listener = new SortStatusChangedListener();
        mButton1.setOnClickListener(listener);
        mButton2.setOnClickListener(listener);
        mCatChildFilterButton.setOnCatFilterClickListener(name,childList);
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
                    Log.e(TAG, "newGoodList="+newGoodList.toString());
                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                        mAdapter.initData(newGoodList);
                    } else {
                        mAdapter.addAllList(newGoodList);
                    }
                    if (newGoodList.size() < pageSize) {
                        mAdapter.setMore(false);
                        mAdapter.setFooterText("没有更多数据...");
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
        utils2.setRequestUrl(I.REQUEST_FIND_GOODS_DETAILS)
                .addParam(I.NewAndBoutiqueGood.CAT_ID, String.valueOf(mCategoryChildId))
                .addParam(I.PAGE_ID, String.valueOf(pageId))
                .addParam(I.PAGE_SIZE, String.valueOf(pageSize))
                .targetClass(NewGoodBean[].class)
                .execute(listener);
    }

    private void initView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_category_child);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_yellow,
                R.color.google_red,
                R.color.google_green
        );
        mButton1 = (Button) findViewById(R.id.btn_sort_price);
        mButton2 = (Button) findViewById(R.id.btn_sort_addtime);
        mRecyclerView = (RecyclerView) findViewById(R.id.rvCategoryChild);
        mGridLayoutManager = new GridLayoutManager(this, I.COLUM_NUM);
        mGridLayoutManager.setOrientation(LinearLayout.VERTICAL);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mAdapter = new GoodAdapter(this, mNewGoodList);
        mRecyclerView.setAdapter(mAdapter);
        mCatChildFilterButton = (CatChildFilterButton) findViewById(R.id.btnCatChildFilter);
        mtvFreshHint = (TextView) findViewById(R.id.tvFreshHint);
        DisPlayUtils.initBack(this);


    }

    class SortStatusChangedListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Drawable right;
            switch (view.getId()) {
                case R.id.btn_sort_price:
                    if (mSortPriceAsc) {
                        sortBy = I.SORT_BY_PRICE_ASC;
                        right = getResources().getDrawable(R.drawable.arrow_order_up);
                    } else {
                        sortBy = I.SORT_BY_PRICE_DESC;
                        right = getResources().getDrawable(R.drawable.arrow_order_down);
                    }
                    mSortPriceAsc = !mSortPriceAsc;
                    right.setBounds(0, 0, right.getMinimumWidth(), right.getMinimumHeight());
                    mButton1.setCompoundDrawables(null, null, right, null);
                    break;
                case R.id.btn_sort_addtime:
                    if (mSortAddTimeAsc) {
                        sortBy = I.SORT_BY_PRICE_ASC;
                        right = getResources().getDrawable(R.drawable.arrow_order_up);
                    } else {
                        sortBy = I.SORT_BY_PRICE_DESC;
                        right = getResources().getDrawable(R.drawable.arrow_order_down);
                    }
                    mSortAddTimeAsc = !mSortAddTimeAsc;
                    right.setBounds(0, 0, right.getMinimumWidth(), right.getMinimumHeight());
                    mButton2.setCompoundDrawables(null, null, right, null);
                    break;
            }
            mAdapter.setSortBy(sortBy);
        }
    }


}
