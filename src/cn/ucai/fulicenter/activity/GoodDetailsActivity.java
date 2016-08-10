package cn.ucai.fulicenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.AlbumsBean;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.task.DownloadCollectCountTask;
import cn.ucai.fulicenter.task.UpdateCartTask;
import cn.ucai.fulicenter.utils.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.DisPlayUtils;
import cn.ucai.fulicenter.view.FlowIndicator;
import cn.ucai.fulicenter.view.SlideAutoLoopView;


/**
 * Created by sks on 2016/8/3.
 */
public class GoodDetailsActivity extends BaseActivity {
    private final static String TAG = GoodDetailsActivity.class.getSimpleName();
    ImageView ivShare,ivCollect, ivCart;
    TextView tvNameEnglish,tvGoodName,tvPriceShop, tvPriceCurrent;
    TextView tvCartCount;

    SlideAutoLoopView mSlideAutoLoopView;
    FlowIndicator mFlowIndicator;
    WebView mGoodBrief;
    GoodDetailsBean mGoodDetail;
    int mGoodId;
    boolean isCollect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_details);
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        MyListener listener = new MyListener();
        ivCollect.setOnClickListener(listener);
        ivShare.setOnClickListener(listener);
        ivCart.setOnClickListener(listener);
        setUpdateCartCountReceiver();
    }

    private void initData() {
        mGoodId = getIntent().getIntExtra(D.GoodDetails.KEY_GOODS_ID, 0);
        if (mGoodId > 0) {
            downLoadGoodDetail();
        } else {
            finish();
        }
        Log.e(TAG, "mGoodId=" + mGoodId);
    }

    private void downLoadGoodDetail() {
        final OkHttpUtils2<String> utils2 = new OkHttpUtils2<>();
        utils2.setRequestUrl(I.REQUEST_FIND_GOOD_DETAILS)
                .addParam(D.GoodDetails.KEY_GOODS_ID,String.valueOf(mGoodId))
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG, "result=" + s);
                        if (s != null) {
                            Gson gson = new Gson();
                            mGoodDetail = gson.fromJson(s, GoodDetailsBean.class);
                            Log.e(TAG, "mGoodDetail=" + mGoodDetail.getGoodsName());
                            showGoodDetail();
                        } else {
                            Toast.makeText(GoodDetailsActivity.this, "加载商品详情失败", Toast.LENGTH_SHORT);
                            finish();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error=" + error);
                        Toast.makeText(GoodDetailsActivity.this, "加载商品详情失败", Toast.LENGTH_SHORT);
                        finish();
                    }
                });
    }

    private void showGoodDetail() {
        tvNameEnglish.setText(mGoodDetail.getGoodsEnglishName());
        tvGoodName.setText(mGoodDetail.getGoodsName());
        tvPriceShop.setText(mGoodDetail.getShopPrice());
        tvPriceCurrent.setText(mGoodDetail.getCurrencyPrice());
        mSlideAutoLoopView.startPlayLoop(mFlowIndicator, getAlbumImageUrl(), getAlbumImageSize());
        mGoodBrief.loadDataWithBaseURL(null, mGoodDetail.getGoodsBrief(), D.TEXT_HTML, D.UTF_8, null);
    }

    private String[] getAlbumImageUrl() {
        String[] albumImageUrl = new String[]{};
        if (mGoodDetail.getPromotePrice() != null && mGoodDetail.getPromotePrice().length() > 0) {
            AlbumsBean[] albums = mGoodDetail.getProperties()[0].getAlbums();
            albumImageUrl = new String[albums.length];
            for (int i=0;i<albumImageUrl.length;i++) {
                albumImageUrl[i] = albums[i].getImgUrl();
            }
        }
        return albumImageUrl;
    }

    private int getAlbumImageSize() {
        if (mGoodDetail.getPromotePrice() != null && mGoodDetail.getPromotePrice().length() > 0) {
            return mGoodDetail.getProperties()[0].getAlbums().length;
        }
        return 0;
    }

    private void initView() {
        DisPlayUtils.initBack(this);
        ivShare = (ImageView) findViewById(R.id.iv_good_share);
        ivCollect = (ImageView) findViewById(R.id.iv_good_collect);
        ivCart = (ImageView) findViewById(R.id.iv_good_cart);
        tvCartCount = (TextView) findViewById(R.id.tv_cart_count);
        tvNameEnglish = (TextView) findViewById(R.id.tv_good_name_english);
        tvGoodName = (TextView) findViewById(R.id.tv_good_name);
        tvPriceShop = (TextView) findViewById(R.id.tv_good_price_shop);
        tvPriceCurrent = (TextView) findViewById(R.id.tv_good_price_current);
        mSlideAutoLoopView = (SlideAutoLoopView) findViewById(R.id.salv);
        mFlowIndicator = (FlowIndicator) findViewById(R.id.indicator);
        mGoodBrief = (WebView) findViewById(R.id.wv_good_brief);
        WebSettings settings = mGoodBrief.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setBuiltInZoomControls(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DemoHXSDKHelper.getInstance().isLogined()) {
            OkHttpUtils2<MessageBean> utils2 = new OkHttpUtils2<>();
            utils2.setRequestUrl(I.REQUEST_IS_COLLECT)
                    .addParam(I.Collect.USER_NAME, FuliCenterApplication.getInstance().getUserName())
                    .addParam(I.Collect.GOODS_ID,String.valueOf(mGoodId))
                    .targetClass(MessageBean.class)
                    .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                        @Override
                        public void onSuccess(MessageBean result) {
                            Log.e(TAG, "result=" + result);
                            if (result != null && result.isSuccess()) {
                                isCollect = true;
                            } else {
                                isCollect = false;
                            }
                            setCollectPic();
                        }

                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "error=" + error);
                        }
                    });
        }
    }

    class MyListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_good_collect:
                    if (DemoHXSDKHelper.getInstance().isLogined()) {
                        setCollectOrCancel();
                    } else {
                        startActivity(new Intent(GoodDetailsActivity.this,LoginActivity.class));
                    }
                    break;
                case R.id.iv_good_share:
                    showShare();
                    break;
                case R.id.iv_good_cart:
                    if (DemoHXSDKHelper.getInstance().isLogined()) {
                        List<CartBean> cartList = FuliCenterApplication.getInstance().getCartList();
                        CartBean cartBean = new CartBean();
                        boolean isFind = false;
                        for (CartBean cart : cartList) {
                            if (cart.getGoodsId() == mGoodDetail.getGoodsId()) {
                                cartBean.setId(mGoodDetail.getId());
                                cartBean.setCount(cart.getCount() + 1);
                                cart.setChecked(cart.isChecked());
                                new UpdateCartTask(GoodDetailsActivity.this, cart).execute();
                                isFind = true;
                            }
                        }
                        if (!isFind) {
                            cartBean.setGoodsId(mGoodDetail.getGoodsId());
                            cartBean.setUserName(FuliCenterApplication.getInstance().getUserName());
                            cartBean.setChecked(true);
                            cartBean.setCount(1);
                            new UpdateCartTask(GoodDetailsActivity.this, cartBean).execute();
                        }
                    } else {
                        startActivity(new Intent(GoodDetailsActivity.this,LoginActivity.class));
                    }
                    break;
            }
        }
    }

    private void setCollectOrCancel() {
        if (isCollect) {
            //取消收藏
            noCollect();
        } else {
            //收藏
            yesCollect();
        }
    }

    private void yesCollect() {
        OkHttpUtils2<MessageBean> utils2 = new OkHttpUtils2<MessageBean>();
        utils2.setRequestUrl(I.REQUEST_ADD_COLLECT)
                .addParam(I.Collect.USER_NAME,FuliCenterApplication.getInstance().getUserName())
                .addParam(I.Collect.GOODS_ID, String.valueOf(mGoodDetail.getGoodsId()))
                .addParam(I.Collect.ADD_TIME, String.valueOf(mGoodDetail.getAddTime()))
                .addParam(I.Collect.GOODS_ENGLISH_NAME,mGoodDetail.getGoodsEnglishName())
                .addParam(I.Collect.GOODS_NAME,mGoodDetail.getGoodsName())
                .addParam(I.Collect.GOODS_IMG,mGoodDetail.getGoodsImg())
                .addParam(I.Collect.GOODS_THUMB,mGoodDetail.getGoodsThumb())
                .targetClass(MessageBean.class)
                .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                    @Override
                    public void onSuccess(MessageBean result) {
                        Log.e(TAG, "result=" + result.toString());
                        if (result != null && result.isSuccess()) {
                            isCollect = true;
                            new DownloadCollectCountTask(FuliCenterApplication.getInstance().getUserName(), GoodDetailsActivity.this).execute();
                        } else {
                            Log.e(TAG, "delete fail");
                        }
                        setCollectPic();
                        Toast.makeText(GoodDetailsActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error=" + error);
                    }
                });
    }

    private void noCollect() {
        OkHttpUtils2<MessageBean> utils2 = new OkHttpUtils2<MessageBean>();
        utils2.setRequestUrl(I.REQUEST_DELETE_COLLECT)
                .addParam(I.Collect.USER_NAME, FuliCenterApplication.getInstance().getUserName())
                .addParam(I.Collect.GOODS_ID,String.valueOf(mGoodId))
                .targetClass(MessageBean.class)
                .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                    @Override
                    public void onSuccess(MessageBean result) {
                        Log.e(TAG, "result=" + result);
                        if (result != null && result.isSuccess()) {
                            new DownloadCollectCountTask(FuliCenterApplication.getInstance().getUserName(), GoodDetailsActivity.this).execute();
                            sendStickyBroadcast(new Intent("update_collect_list"));
                            isCollect = false;
                        } else {
                            Log.e(TAG, "删除失败");
                        }
                        setCollectPic();
                        Toast.makeText(GoodDetailsActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error=" + error);
                    }
                });
    }

    private void setCollectPic() {
        if (isCollect) {
            ivCollect.setImageResource(R.drawable.bg_collect_out);
        } else {
            ivCollect.setImageResource(R.drawable.bg_collect_in);
        }
    }

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(this);
    }

    class UpdateCartCountReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int count = Utils.getCartCount();
            if (!DemoHXSDKHelper.getInstance().isLogined() || count == 0) {
                tvCartCount.setText(String.valueOf(0));
                tvCartCount.setVisibility(View.GONE);
            } else {
                tvCartCount.setText(String.valueOf(count));
                tvCartCount.setVisibility(View.VISIBLE);
            }
        }
    }

    UpdateCartCountReceiver mReceiver;
    private void setUpdateCartCountReceiver() {
        mReceiver = new UpdateCartCountReceiver();
        IntentFilter filter = new IntentFilter("update_cart_list");
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
