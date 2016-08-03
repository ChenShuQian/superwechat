package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import cn.ucai.fulicenter.R;

public class FulicenterMainActivity extends BaseActivity{
    private static final String TAG = FulicenterMainActivity.class.getSimpleName();
    RadioButton rbNewGoods;
    RadioButton rbBoutique;
    RadioButton rbCategory;
    RadioButton rbCart;
    RadioButton rbPerson;
    TextView tvCartHint;
    RadioButton[] mrbTabs;
    int index;
    int currentIndex;
    NewGoodsFragment mNewGoodsFragment;
    BoutiqueFragment mBoutiqueFragment;
    Fragment[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fulicenter_main);
        initView();
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
        mNewGoodsFragment = new NewGoodsFragment();
        mBoutiqueFragment = new BoutiqueFragment();
        fragments = new Fragment[]{mNewGoodsFragment,mBoutiqueFragment};
        // 添加显示第一个fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mNewGoodsFragment)
//                .add(R.id.fragment_container, mBoutiqueFragment)
//                .hide(contactListFragment)
                .show(mNewGoodsFragment)
                .commit();
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
                index = 3;
                break;
            case R.id.rbPerson:
                index = 4;
                break;
        }
        if (currentIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        mrbTabs[currentIndex].setSelected(false);
        // 把当前tab设为选中状态
        mrbTabs[index].setSelected(true);
        currentIndex = index;

        Log.e(TAG, "index=" + index + ",currentIndex=" + currentIndex);
        if (index != currentIndex) {
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
}
