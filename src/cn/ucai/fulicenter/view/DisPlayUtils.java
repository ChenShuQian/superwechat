package cn.ucai.fulicenter.view;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import cn.ucai.fulicenter.R;

/**
 * Created by sks on 2016/8/3.
 */
public class DisPlayUtils {
    public static void initBack(final Activity activity) {
        activity.findViewById(R.id.backClickArea).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });
    }

    public static void initBoutique(final Activity activity,String name) {
        TextView textView = (TextView) activity.findViewById(R.id.tv_goods_name);
        textView.setText(name);
    }
}
