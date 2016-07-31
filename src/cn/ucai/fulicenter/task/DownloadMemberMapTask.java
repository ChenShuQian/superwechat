package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.bean.MemberUserAvatar;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.utils.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by sks on 2016/7/21.
 */
public class DownloadMemberMapTask {
    private final static String TAG = DownloadMemberMapTask.class.getSimpleName();
    String hxid;
    Context mContext;


    public DownloadMemberMapTask(String hxid, Context context) {
        mContext = context;
        this.hxid = hxid;
    }

    public void execute() {
        OkHttpUtils2<String> utils2 = new OkHttpUtils2<>();
        utils2.setRequestUrl(I.REQUEST_DOWNLOAD_GROUP_MEMBERS_BY_HXID)
                .addParam(I.Member.GROUP_HX_ID,hxid)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG, "s" + s);
                        Result result = Utils.getListResultFromJson(s, MemberUserAvatar.class);
                        List<MemberUserAvatar> list = (List<MemberUserAvatar>) result.getRetData();
                        Log.e(TAG, "result=" +result);
                        if (list != null && list.size() > 0) {
                            Map<String, HashMap<String, MemberUserAvatar>> memberMap = FuliCenterApplication.getInstance().getMemberMap();
                            if (!memberMap.containsKey(hxid)) {
                                memberMap.put(hxid, new HashMap<String, MemberUserAvatar>());
                            }
                            HashMap<String, MemberUserAvatar> hxidMembers = memberMap.get(hxid);
                            for (MemberUserAvatar u : list) {
                                hxidMembers.put(u.getMUserName(), u);
                            }
                            mContext.sendStickyBroadcast(new Intent("update_member_list"));
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error" + error);
                    }
                });
    }
}
