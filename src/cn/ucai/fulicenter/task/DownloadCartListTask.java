package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.utils.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by sks on 2016/7/21.
 */
public class DownloadCartListTask {
    private final static String TAG = DownloadCartListTask.class.getSimpleName();
    String username;
    Context mContext;


    public DownloadCartListTask(String username, Context context) {
        mContext = context;
        this.username = username;
    }

    public void execute() {
        OkHttpUtils2<CartBean[]> utils2 = new OkHttpUtils2<>();
        utils2.setRequestUrl(I.REQUEST_FIND_CARTS)
                .addParam(I.Cart.USER_NAME,username)
                .addParam(I.PAGE_ID, String.valueOf(I.PAGE_ID_DEFAULT))
                .addParam(I.PAGE_SIZE, String.valueOf(I.PAGE_SIZE_DEFAULT))
                .targetClass(CartBean[].class)
                .execute(new OkHttpUtils2.OnCompleteListener<CartBean[]>() {
                    @Override
                    public void onSuccess(CartBean[] s) {
                        Log.e(TAG, "s" + s);
                        if (s != null) {
                            ArrayList<CartBean> list = Utils.array2List(s);
                            final List<CartBean> cartList = FuliCenterApplication.getInstance().getCartList();
                            for (final CartBean cart : list) {
                                if (!cartList.contains(cart)) {
                                    OkHttpUtils2<GoodDetailsBean> utils = new OkHttpUtils2<GoodDetailsBean>();
                                    utils.setRequestUrl(I.REQUEST_FIND_GOOD_DETAILS)
                                            .addParam(D.GoodDetails.KEY_GOODS_ID, String.valueOf(cart.getGoodsId()))
                                            .targetClass(GoodDetailsBean.class)
                                            .execute(new OkHttpUtils2.OnCompleteListener<GoodDetailsBean>() {
                                                @Override
                                                public void onSuccess(GoodDetailsBean result) {
                                                    cart.setGoods(result);
                                                    cartList.add(cart);
                                                    Log.e(TAG, "cart=" + cart.toString());
                                                }

                                                @Override
                                                public void onError(String error) {
                                                    Log.e(TAG, "error" + error);
                                                }
                                            });
                                } else {
                                    cartList.get(cartList.indexOf(cart)).setChecked(cart.isChecked());
                                    cartList.get(cartList.indexOf(cart)).setCount(cart.getCount());
                                }
                                mContext.sendStickyBroadcast(new Intent("update_cart_list"));
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error" + error);
                    }
                });
    }
}
