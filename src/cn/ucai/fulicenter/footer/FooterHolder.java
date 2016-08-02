package cn.ucai.fulicenter.footer;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import cn.ucai.fulicenter.R;

/**
 * Created by sks on 2016/8/2.
 */
public class FooterHolder extends RecyclerView.ViewHolder{
    public TextView tvFooterText;

    public FooterHolder(View itemView) {
        super(itemView);
        tvFooterText = (TextView) itemView.findViewById(R.id.tvFooterText);
    }
}
