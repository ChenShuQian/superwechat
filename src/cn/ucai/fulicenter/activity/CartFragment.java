package cn.ucai.fulicenter.activity;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.CartAdapter;
import cn.ucai.fulicenter.bean.CartBean;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {
    RecyclerView mrv_cart;
    CartAdapter mAdapter;
    List<CartBean> mCartBeanList;
    FulicenterMainActivity mContext;

    public CartFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_cart, container, false);
        mContext = (FulicenterMainActivity) getContext();
        mCartBeanList = new ArrayList<>();
        initView(layout);
        initData();
        return layout;
    }

    private void initData() {
        mCartBeanList = FuliCenterApplication.getInstance().getCartList();
        mAdapter.initData(mCartBeanList);
    }

    private void initView(View layout) {
        mrv_cart = (RecyclerView) layout.findViewById(R.id.rv_cart);
        mAdapter = new CartAdapter(mContext,mCartBeanList);
        mrv_cart.setAdapter(mAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        mrv_cart.setLayoutManager(manager);
    }

}
