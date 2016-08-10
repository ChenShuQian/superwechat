package cn.ucai.fulicenter.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import cn.ucai.fulicenter.adapter.BoutiqueAdapter;
import cn.ucai.fulicenter.adapter.CartAdapter;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.utils.OkHttpUtils2;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {
    private final static String TAG = BoutiqueFragment.class.getSimpleName();
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    List<CartBean> mCartList;
    List<CartBean> cartList;
    Context mContext;

    TextView mtv_nothing;
    TextView mtvFreshHint;
    TextView tvShopPrice;
    TextView tvBuyPrice;
    int pageId = 0;
    int pageSize = 10;
    int action = I.ACTION_DOWNLOAD;
    LinearLayoutManager mLinearLayoutManager;
    CartAdapter mAdapter;
    public CartFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        cartList = FuliCenterApplication.getInstance().getCartList();
        mCartList = new ArrayList<>();
        initView(view);
        setListener();
        return view;
    }

    private void setListener() {
        /**下拉刷新*/
        setDownRefreshListener();
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
        mCartList.clear();
        mCartList.addAll(cartList);
        mtvFreshHint.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
        mAdapter.setMore(true);
        if (mCartList != null && mCartList.size() > 0) {
            mtv_nothing.setVisibility(View.GONE);
            if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                mAdapter.initData(cartList);
            } else {
                mAdapter.addAllList(cartList);
            }
            if (cartList.size() < pageSize) {
                mAdapter.setMore(false);
            }
        } else {
            mAdapter.setMore(false);
            mAdapter.notifyDataSetChanged();
            mtv_nothing.setVisibility(View.VISIBLE);
        }
        setPrice();
    }

    private void initView(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_new_good);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_yellow,
                R.color.google_red,
                R.color.google_green
        );
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_cart);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mLinearLayoutManager.setOrientation(LinearLayout.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new CartAdapter(mContext, mCartList);
        mRecyclerView.setAdapter(mAdapter);

        mtv_nothing = (TextView) view.findViewById(R.id.tv_nothing);
        mtvFreshHint = (TextView) view.findViewById(R.id.tvFreshHint);
        tvShopPrice = (TextView) view.findViewById(R.id.tv_shop_price);
        tvBuyPrice = (TextView) view.findViewById(R.id.tv_buy_price);
        mtv_nothing.setVisibility(View.VISIBLE);
        setUpdateCartListReceiver();
    }

    private void setPrice() {
        if (cartList != null && cartList.size() > 0) {
            int shopPrice = 0;
            int buyPrice = 0;
            for (CartBean cart : cartList) {
                GoodDetailsBean goods = cart.getGoods();
                if (goods != null && cart.isChecked()) {
                    shopPrice += convertPrice(goods.getCurrencyPrice()) * cart.getCount();
                    buyPrice += convertPrice(goods.getShopPrice()) * cart.getCount();
                }
            }
            tvShopPrice.setText("合计：¥"+shopPrice);
            tvBuyPrice.setText("节省：¥" + (shopPrice - buyPrice));
        } else {
            tvShopPrice.setText("合计：¥0");
            tvBuyPrice.setText("合计：¥0");
        }
    }

    private int convertPrice(String price) {
        int intPrice = Integer.parseInt(price.substring(price.indexOf("￥") + 1));
        return intPrice;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    class UpdateCartListReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            action = I.ACTION_DOWNLOAD;
            downLoadGoodsList();
        }
    }

    UpdateCartListReceiver mReceiver;
    private void setUpdateCartListReceiver() {
        mReceiver = new UpdateCartListReceiver();
        IntentFilter filter = new IntentFilter("update_cart_list");
        mContext.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }
    }
}
