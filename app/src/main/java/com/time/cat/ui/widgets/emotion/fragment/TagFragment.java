package com.time.cat.ui.widgets.emotion.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.time.cat.R;
import com.time.cat.data.database.DB;
import com.time.cat.data.model.APImodel.Tag;
import com.time.cat.data.model.Converter;
import com.time.cat.ui.base.BaseFragment;
import com.time.cat.ui.widgets.label_tag_view.TagCloudView;
import com.time.cat.util.override.LogUtil;
import com.time.cat.util.string.MeechaoDataUtils;
import com.time.cat.util.string.TimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/17
 * @discription null
 * @usage null
 */
public class TagFragment extends BaseFragment {
    TagCloudView tagCloudView;


    /**
     * 创建与Fragment对象关联的View视图时调用
     * @param inflater inflater
     * @param container container
     * @param savedInstanceState savedInstanceState
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tag, container, false);
        tagCloudView = rootView.findViewById(R.id.tag_cloud_view);
        List<Tag> tagList = new ArrayList<>();
        for (int i = 1 ; i <= 40; i++) {
            Tag tag = new Tag();
            tag.setOwner(Converter.getOwnerUrl(DB.users().getActive()));
            tag.setCreated_datetime(TimeUtil.formatGMTDate(new Date()));
            tag.setName(" #标签"+i+"["+i+"|话题]# ");
            tag.setUrl("http://192.168.88.105:8000/tags/"+i+"/");
            tagList.add(tag);
        }
        List<String> tags = new ArrayList<>();
        for (Tag t: tagList) {
            tags.add(MeechaoDataUtils.regexTag(t.getName()));
        }
        tagCloudView.setTags(tags);
        tagCloudView.setOnTagClickListener(position -> {
            if (position == -1) {
                LogUtil.e("onClick tagCloudView at --> 点击末尾文字");
            } else {
                LogUtil.e("onClick tagCloudView at --> 点击 position" + position);
                if (onTagAddListener != null) {
                    onTagAddListener.insertTag(tagList.get(position));
                }
            }
        });
        return rootView;
    }

    OnTagAddListener onTagAddListener;

    public void setOnTagAddListener(OnTagAddListener onTagAddListener) {
        this.onTagAddListener = onTagAddListener;
    }

    public interface OnTagAddListener {
        void insertTag(Tag tag);
    }

}
