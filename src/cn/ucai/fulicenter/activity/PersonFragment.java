package cn.ucai.fulicenter.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.utils.UserUtils;
import cn.ucai.fulicenter.view.DisPlayUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonFragment extends Fragment {
    private final static String TAG = PersonFragment.class.getSimpleName();
    FulicenterMainActivity mContext;
    TextView mtvSettings;
    ImageView mivTackView;
    ImageView mivPersonAvatar;
    TextView mtvPersonName;
    TextView mtvCollectCount;
    RelativeLayout mlayoutUserCenter;
    LinearLayout mlayoutCollect;

    public PersonFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = (FulicenterMainActivity) getContext();
        View layout = inflater.inflate(R.layout.fragment_person, container, false);
        initView(layout);
        initData();
        setListener();
        return layout;
    }

    private void initData() {
        if (DemoHXSDKHelper.getInstance().isLogined()) {
            UserAvatar user = FuliCenterApplication.getInstance().getUser();
            Log.e(TAG, "user=" + user);
            UserUtils.setAppCurrentUserNick(mtvPersonName);
            UserUtils.setAppCurrentUserAvatar(mContext, mivPersonAvatar);
        }
    }

    private void setListener() {
        MyClickListener listener = new MyClickListener();
        mtvSettings.setOnClickListener(listener);
        mlayoutUserCenter.setOnClickListener(listener);
        mlayoutCollect.setOnClickListener(listener);
        updateCollectCountListener();
    }

    class MyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (DemoHXSDKHelper.getInstance().isLogined()) {
                switch (view.getId()) {
                    case R.id.layoutUserCenter:
                    case R.id.tvSettings:
                        startActivity(new Intent(mContext, SettingsActivity.class));
                        break;
                    case R.id.layoutCollect:
                        startActivity(new Intent(mContext, CollectActivity.class));
                        break;
                }
            } else {
                Log.e(TAG, "not logined...");
            }
        }
    }


    private void initView(View layout) {
        mtvSettings = (TextView) layout.findViewById(R.id.tvSettings);
        mivTackView = (ImageView) layout.findViewById(R.id.iv_tack_view);
        mivPersonAvatar = (ImageView) layout.findViewById(R.id.iv_person_avatar);
        mtvPersonName = (TextView) layout.findViewById(R.id.tv_person_name);
        mtvCollectCount = (TextView) layout.findViewById(R.id.tvCollectCount);
        mlayoutUserCenter = (RelativeLayout) layout.findViewById(R.id.layoutUserCenter);
        mlayoutCollect = (LinearLayout) layout.findViewById(R.id.layoutCollect);
        initOrderList(layout);
    }

    private void initOrderList(View layout) {
        GridView gridView = (GridView) layout.findViewById(R.id.gv_gridview_list);
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> order1 = new HashMap<String, Object>();
        order1.put("order", R.drawable.order_list1);
        data.add(order1);
        HashMap<String, Object> order2 = new HashMap<String, Object>();
        order2.put("order", R.drawable.order_list2);
        data.add(order2);
        HashMap<String, Object> order3 = new HashMap<String, Object>();
        order3.put("order", R.drawable.order_list3);
        data.add(order3);
        HashMap<String, Object> order4 = new HashMap<String, Object>();
        order4.put("order", R.drawable.order_list4);
        data.add(order4);
        HashMap<String, Object> order5 = new HashMap<String, Object>();
        order5.put("order", R.drawable.order_list5);
        data.add(order5);
        SimpleAdapter adapter = new SimpleAdapter(mContext, data, R.layout.simple_adapter, new String[]{"order"}, new int[]{R.id.iv_order});
        gridView.setAdapter(adapter);
    }

    class UpdateCollectCount extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int count = FuliCenterApplication.getInstance().getCollectCount();
            Log.e(TAG, "count=" + count);
            mtvCollectCount.setText(String.valueOf(count));
        }
    }

    UpdateCollectCount mReceiver;
    private void updateCollectCountListener() {
        mReceiver = new UpdateCollectCount();
        IntentFilter filter = new IntentFilter("update_collect");
        mContext.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(mReceiver);
    }
}
