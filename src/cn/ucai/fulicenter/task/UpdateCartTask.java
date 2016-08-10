package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.utils.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by sks on 2016/7/21.
 */
public class UpdateCartTask {
    private final static String TAG = UpdateCartTask.class.getSimpleName();
    CartBean mCartBean;
    Context mContext;


    public UpdateCartTask(Context context,CartBean cart) {
        mContext = context;
        this.mCartBean = cart;
    }

    public void execute() {
        final List<CartBean> cartList = FuliCenterApplication.getInstance().getCartList();
        if (cartList.contains(mCartBean)) {
            if (mCartBean.getCount() > 0) {
                //更新
                UpdateCart(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                    @Override
                    public void onSuccess(MessageBean result) {
                        if (result != null && result.isSuccess()) {
                            cartList.set(cartList.indexOf(mCartBean), mCartBean);
                            mContext.sendStickyBroadcast(new Intent("update_cart_list"));
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error=" + error);
                    }
                });
            } else {
                //删除
                DeleteCart(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                    @Override
                    public void onSuccess(MessageBean result) {
                        if (result != null && result.isSuccess()) {
                            cartList.remove(mCartBean);
                            mContext.sendStickyBroadcast(new Intent("update_cart_list"));
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error=" + error);
                    }
                });
            }
        } else {
            //添加
            AddCart(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                @Override
                public void onSuccess(MessageBean result) {
                    List<CartBean> cartList1 = FuliCenterApplication.getInstance().getCartList();
                    cartList1.add(mCartBean);
                    mContext.sendStickyBroadcast(new Intent("update_cart_list"));
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "error=" + error);
                }
            });
        }
    }

    private void UpdateCart(OkHttpUtils2.OnCompleteListener<MessageBean> listener) {
        OkHttpUtils2<MessageBean> utils2 = new OkHttpUtils2<>();
        utils2.setRequestUrl(I.REQUEST_UPDATE_CART)
                .addParam(I.Cart.ID, String.valueOf(mCartBean.getId()))
                .addParam(I.Cart.COUNT, String.valueOf(mCartBean.getCount()))
                .addParam(I.Cart.IS_CHECKED, String.valueOf(mCartBean.isChecked()))
                .targetClass(MessageBean.class)
                .execute(listener);
    }

    private void DeleteCart(OkHttpUtils2.OnCompleteListener<MessageBean> listener) {
        OkHttpUtils2<MessageBean> utils2 = new OkHttpUtils2<>();
        utils2.setRequestUrl(I.REQUEST_DELETE_CART)
                .addParam(I.Cart.ID, String.valueOf(mCartBean.getId()))
                .targetClass(MessageBean.class)
                .execute(listener);
    }

    private void AddCart(OkHttpUtils2.OnCompleteListener<MessageBean> listener) {
        OkHttpUtils2<MessageBean> utils2 = new OkHttpUtils2<>();
        utils2.setRequestUrl(I.REQUEST_ADD_CART)
                .addParam(I.Cart.USER_NAME,mCartBean.getUserName())
                .addParam(I.Cart.GOODS_ID, String.valueOf(mCartBean.getGoodsId()))
                .addParam(I.Cart.COUNT, String.valueOf(mCartBean.getCount()))
                .addParam(I.Cart.IS_CHECKED,String.valueOf(mCartBean.isChecked()))
                .targetClass(MessageBean.class)
                .execute(listener);
    }
}
