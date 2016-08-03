package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.AlbumsBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_details);
        initView();
        initData();
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
}
