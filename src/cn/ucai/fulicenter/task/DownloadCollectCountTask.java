package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.Map;

import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.utils.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by sks on 2016/7/21.
 */
public class DownloadCollectCountTask {
    private final static String TAG = DownloadCollectCountTask.class.getSimpleName();
    String username;
    Context mContext;


    public DownloadCollectCountTask(String username, Context context) {
        mContext = context;
        this.username = username;
    }

    public void execute() {
        OkHttpUtils2<MessageBean> utils2 = new OkHttpUtils2<>();
        utils2.setRequestUrl(I.REQUEST_FIND_COLLECT_COUNT)
                .addParam(I.Collect.USER_NAME,username)
                .targetClass(MessageBean.class)
                .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                    @Override
                    public void onSuccess(MessageBean msg) {
                        Log.e(TAG, "s" + msg);
                        if (msg != null) {
                            if (msg.isSuccess()) {
                                FuliCenterApplication.getInstance().setCollectCount(Integer.valueOf(msg.getMsg()));
                            } else {
                                FuliCenterApplication.getInstance().setCollectCount(0);
                            }
                        } else {
                            Log.e(TAG, "s" + msg);
                        }
                        mContext.sendStickyBroadcast(new Intent("update_collect"));
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error" + error);
                    }
                });
    }
}
