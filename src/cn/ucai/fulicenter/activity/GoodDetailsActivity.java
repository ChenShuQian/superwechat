package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.AlbumsBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.task.DownloadCollectCountTask;
import cn.ucai.fulicenter.utils.OkHttpUtils2;
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
                        Log.e(TAG, "result=" + result);
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


}
