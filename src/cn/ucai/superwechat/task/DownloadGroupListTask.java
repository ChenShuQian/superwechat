package cn.ucai.superwechat.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.Map;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.bean.GroupAvatar;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.bean.UserAvatar;
import cn.ucai.superwechat.utils.OkHttpUtils2;
import cn.ucai.superwechat.utils.Utils;

/**
 * Created by sks on 2016/7/21.
 */
public class DownloadGroupListTask {
    private final static String TAG = DownloadGroupListTask.class.getSimpleName();
    String username;
    Context mContext;


    public DownloadGroupListTask(String username, Context context) {
        mContext = context;
        this.username = username;
    }

    public void execute() {
        OkHttpUtils2<String> utils2 = new OkHttpUtils2<>();
        utils2.setRequestUrl(I.REQUEST_FIND_GROUP_BY_USER_NAME)
                .addParam(I.User.USER_NAME,username)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG, "s" + s);
                        Result result = Utils.getListResultFromJson(s, GroupAvatar.class);
                        List<GroupAvatar> list = (List<GroupAvatar>) result.getRetData();
                        if (list != null && list.size() > 0) {
                            SuperWeChatApplication.getInstance().setGroupList(list);
                            mContext.sendStickyBroadcast(new Intent("update_group_list"));
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error" + error);
                    }
                });
    }
}
