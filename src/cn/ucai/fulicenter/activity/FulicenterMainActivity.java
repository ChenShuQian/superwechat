package cn.ucai.fulicenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.utils.Utils;

public class FulicenterMainActivity extends BaseActivity{
    private static final String TAG = FulicenterMainActivity.class.getSimpleName();
    RadioButton rbNewGoods;
    RadioButton rbBoutique;
    RadioButton rbCategory;
    RadioButton rbCart;
    RadioButton rbPerson;
    TextView tvCartHint;
    RadioButton[] mrbTabs;
    int ACTION_LOGIN_PERSON = 100;
    int ACTION_LOGIN_CART = 200;
    int index;
    int currentIndex;
    NewGoodsFragment mNewGoodsFragment;
    BoutiqueFragment mBoutiqueFragment;
    CategoryFragment mCategoryFragment;
    PersonFragment mPersonFragment;
    CartFragment mCartFragment;
    Fragment[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fulicenter_main);
        initView();
        initFragment();
        setListener();
        // 添加显示第一个fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mNewGoodsFragment)
                .add(R.id.fragment_container, mBoutiqueFragment)
                .add(R.id.fragment_container, mCategoryFragment)
                .hide(mBoutiqueFragment).hide(mCategoryFragment)
                .show(mNewGoodsFragment)
                .commit();
    }

    private void setListener() {
        setUpdateCartCountReceiver();
    }

    private void initFragment() {
        mNewGoodsFragment = new NewGoodsFragment();
        mBoutiqueFragment = new BoutiqueFragment();
        mCategoryFragment = new CategoryFragment();
        mPersonFragment = new PersonFragment();
        mCartFragment = new CartFragment();
        fragments = new Fragment[]{mNewGoodsFragment, mBoutiqueFragment, mCategoryFragment, mCartFragment, mPersonFragment};
    }

    private void initView() {
        rbNewGoods = (RadioButton) findViewById(R.id.rbNewGoods);
        rbBoutique = (RadioButton) findViewById(R.id.rbBoutique);
        rbCategory = (RadioButton) findViewById(R.id.rbCategory);
        rbCart = (RadioButton) findViewById(R.id.rbCart);
        rbPerson = (RadioButton) findViewById(R.id.rbPerson);
        tvCartHint = (TextView) findViewById(R.id.tvCartHint);
        mrbTabs = new RadioButton[5];
        mrbTabs[0] = rbNewGoods;
        mrbTabs[1] = rbBoutique;
        mrbTabs[2] = rbCategory;
        mrbTabs[3] = rbCart;
        mrbTabs[4] = rbPerson;
    }

    public void onCheckedChange(View view) {
        switch (view.getId()) {
            case R.id.rbNewGoods:
                index = 0;
                break;
            case R.id.rbBoutique:
                index = 1;
                break;
            case R.id.rbCategory:
                index = 2;
                break;
            case R.id.rbCart:
                if (DemoHXSDKHelper.getInstance().isLogined()) {
                    index = 3;
                } else {
                    startActivityForResult(new Intent(this, LoginActivity.class),ACTION_LOGIN_CART);
                }
                break;
            case R.id.rbPerson:
                if (DemoHXSDKHelper.getInstance().isLogined()) {
                    index = 4;
                } else {
                    startActivityForResult(new Intent(this, LoginActivity.class),ACTION_LOGIN_PERSON);
                }
                break;
        }
        Log.e(TAG, "index=" + index + ",currentIndex=" + currentIndex);
        setFragment();
    }

    private void setFragment() {
        if (index != currentIndex) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
            setRadioButtonStatus(index);
            currentIndex = index;
        }
    }

    private void setRadioButtonStatus(int index) {
        for (int i=0;i<mrbTabs.length;i++) {
            if (i == index) {
                mrbTabs[i].setChecked(true);
            } else {
                mrbTabs[i].setChecked(false);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (DemoHXSDKHelper.getInstance().isLogined()) {
            if (requestCode == ACTION_LOGIN_PERSON) {
                index = 4;
            }
            if (requestCode == ACTION_LOGIN_CART) {
                index = 3;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!DemoHXSDKHelper.getInstance().isLogined() && index == 4) {
            index = 0;
        }
        setFragment();
        setRadioButtonStatus(currentIndex);
        Log.e(TAG, "index=" + index + ",currentIndex=" + currentIndex);
    }

    class UpdateCartCountReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int count = Utils.getCartCount();
            Log.e(TAG, "count="+count);
            if (!DemoHXSDKHelper.getInstance().isLogined() || count == 0) {
                tvCartHint.setText(String.valueOf(0));
                tvCartHint.setVisibility(View.GONE);
            } else {
                tvCartHint.setText(String.valueOf(count));
                tvCartHint.setVisibility(View.VISIBLE);
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
