package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.CategoryChildActivity;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.utils.ImageUtils;

/**
 * Created by sks on 2016/8/4.
 */
public class CategoryAdapter extends BaseExpandableListAdapter{
    Context mContext;
    List<CategoryGroupBean> mGroupList;
    List<ArrayList<CategoryChildBean>> mChildList;

    public CategoryAdapter(Context mContext, List<CategoryGroupBean> mGroupList, List<ArrayList<CategoryChildBean>> mChildList) {
        this.mContext = mContext;
        this.mGroupList = new ArrayList<>();
        this.mGroupList.addAll(mGroupList);
        this.mChildList = new ArrayList<>();
        this.mChildList.addAll(mChildList);
    }

    @Override
    public int getGroupCount() {
        return mGroupList != null ? mGroupList.size() : 0;
    }

    @Override
    public int getChildrenCount(int i) {
        return mChildList.get(i).size();
    }

    @Override
    public CategoryGroupBean getGroup(int i) {
        if (mGroupList != null) {
            return mGroupList.get(i);
        }
        return null;
    }

    @Override
    public CategoryChildBean getChild(int groupPosition, int childPosition) {
        if (mChildList.get(groupPosition) != null && mChildList.get(groupPosition).get(childPosition) != null) {
            return mChildList.get(groupPosition).get(childPosition);
        }
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup viewGroup) {
        GroupViewHolder holder;
        if (view == null) {
            view = View.inflate(mContext, R.layout.item_category, null);
            holder = new GroupViewHolder();
            holder.iv_category_pic = (ImageView) view.findViewById(R.id.iv_category_pic);
            holder.tv_category_text = (TextView) view.findViewById(R.id.tv_category_text);
            holder.iv_category_img = (ImageView) view.findViewById(R.id.iv_category_img);
            view.setTag(holder);
        } else {
            holder = (GroupViewHolder) view.getTag();
        }
        CategoryGroupBean group = getGroup(groupPosition);
        holder.tv_category_text.setText(group.getName());
        ImageUtils.setGroupCategoryImage(mContext, holder.iv_category_img, group.getImageUrl());
        if (isExpanded) {
            holder.iv_category_pic.setImageResource(R.drawable.expand_off);
        } else {
            holder.iv_category_pic.setImageResource(R.drawable.expand_on);
        }
        return view;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isExpanded, View view, ViewGroup viewGroup) {
        ChildViewHolder holder;
        if (view == null) {
            view = View.inflate(mContext, R.layout.item_category_child, null);
            holder = new ChildViewHolder();
            holder.layout_category_child = (RelativeLayout) view.findViewById(R.id.layout_category_child);
            holder.iv_category_child_thumb = (ImageView) view.findViewById(R.id.iv_category_child_thumb);
            holder.tv_category_child_name = (TextView) view.findViewById(R.id.tv_category_child_name);
            view.setTag(holder);
        } else {
            holder = (ChildViewHolder) view.getTag();
        }
        final CategoryChildBean child = getChild(groupPosition, childPosition);
        if (child != null) {
            ImageUtils.setChildCategoryImage(mContext, holder.iv_category_child_thumb, child.getImageUrl());
            holder.tv_category_child_name.setText(child.getName());
            holder.layout_category_child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mContext.startActivity(new Intent(mContext, CategoryChildActivity.class)
                            .putExtra(I.CategoryChild.CAT_ID, child.getId())
                            .putExtra(I.CategoryGroup.NAME,mGroupList.get(groupPosition).getName())
                            .putExtra("childList",mChildList.get(groupPosition)));
                    Log.e("main", "child.getId()=" + child.getId());
                    Log.e("main", "mGroupList.get(groupPosition).getName()=" + mGroupList.get(groupPosition).getName());
                }
            });
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    public void addAll(List<CategoryGroupBean> mGroupList, List<ArrayList<CategoryChildBean>> mChildList) {
        this.mGroupList.clear();
        this.mGroupList.addAll(mGroupList);
        this.mChildList.clear();
        this.mChildList.addAll(mChildList);
        notifyDataSetChanged();
    }

    class GroupViewHolder {
        ImageView iv_category_img;
        TextView tv_category_text;
        ImageView iv_category_pic;
    }

    class ChildViewHolder {
        RelativeLayout layout_category_child;
        ImageView iv_category_child_thumb;
        TextView tv_category_child_name;
    }
}