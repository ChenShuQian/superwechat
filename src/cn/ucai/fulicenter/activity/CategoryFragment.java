package cn.ucai.fulicenter.activity;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.CategoryAdapter;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.utils.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {
    private final static String TAG = CategoryFragment.class.getSimpleName();
    ExpandableListView mExpandableListView;
    Context mContext;
    List<CategoryGroupBean> mGroupList;
    List<ArrayList<CategoryChildBean>> mChildList;
    CategoryAdapter mAdapter;
    int groupCount = 0;

    public CategoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext =  getContext();
        View layout = inflater.inflate(R.layout.fragment_category, container, false);
        mGroupList = new ArrayList<>();
        mChildList = new ArrayList<>();
        mAdapter = new CategoryAdapter(mContext, mGroupList, mChildList);
        intiView(layout);
        initData();
        return layout;
    }

    private void initData() {
        findCategoryGroupList(new OkHttpUtils2.OnCompleteListener<CategoryGroupBean[]>() {
            @Override
            public void onSuccess(CategoryGroupBean[] result) {
                Log.e(TAG, "Group:result=" + result);
                if (result != null) {
                    ArrayList<CategoryGroupBean> groupList = Utils.array2List(result);
                    if (groupList != null) {
                        Log.e(TAG, "Group:groupList=" + groupList.size());
                        mGroupList.addAll(groupList);
                        int i = 0;
                        for (CategoryGroupBean g : groupList) {
                            mChildList.add(new ArrayList<CategoryChildBean>());
                            findCategoryChildList(g.getId(), i);
                            i++;
                        }
                    }
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Group:error=" + error);
            }
        });
    }

    private void findCategoryChildList(int parentId, final int index) {
        OkHttpUtils2<CategoryChildBean[]> utils2 = new OkHttpUtils2<>();
        utils2.setRequestUrl(I.REQUEST_FIND_CATEGORY_CHILDREN)
                .addParam(I.CategoryChild.PARENT_ID, String.valueOf(parentId))
                .addParam(I.PAGE_ID, String.valueOf(I.PAGE_ID_DEFAULT))
                .addParam(I.PAGE_SIZE, String.valueOf(I.PAGE_SIZE_DEFAULT))
                .targetClass(CategoryChildBean[].class)
                .execute(new OkHttpUtils2.OnCompleteListener<CategoryChildBean[]>() {
                    @Override
                    public void onSuccess(CategoryChildBean[] result) {
                        groupCount++;
                        Log.e(TAG, "Chilc:result=" + result);
                        if (result != null) {
                            ArrayList<CategoryChildBean> childList = Utils.array2List(result);
                            Log.e(TAG, "Group:childList=" + childList.size());
                            if (childList != null) {
                                mChildList.set(index, childList);
                            }
                        }
                        if (groupCount == mGroupList.size()) {
                            mAdapter.addAll(mGroupList, mChildList);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Chilc:error=" + error);
                    }
                });
    }

    private void findCategoryGroupList(OkHttpUtils2.OnCompleteListener<CategoryGroupBean[]> listener) {
        OkHttpUtils2<CategoryGroupBean[]> utils2 = new OkHttpUtils2<>();
        utils2.setRequestUrl(I.REQUEST_FIND_CATEGORY_GROUP)
                .targetClass(CategoryGroupBean[].class)
                .execute(listener);
    }

    private void intiView(View layout) {
        mExpandableListView = (ExpandableListView) layout.findViewById(R.id.elvCategory);
        mExpandableListView.setGroupIndicator(null);
        mExpandableListView.setAdapter(mAdapter);
    }

}
