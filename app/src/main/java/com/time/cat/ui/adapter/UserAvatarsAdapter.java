package com.time.cat.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.time.cat.R;
import com.time.cat.data.model.DBmodel.DBUser;
import com.time.cat.util.source.AvatarManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/16
 * @discription null
 * @usage null
 */
public class UserAvatarsAdapter extends BaseAdapter {

    private Context context;
    private List<String> avatars = new ArrayList<>(AvatarManager.avatars.keySet());
    private DBUser user;

    public UserAvatarsAdapter(Context context, DBUser dbUser) {
        this.context = context;
        this.user = dbUser;
    }

    @Override
    public int getCount() {
        return avatars.size();
    }

    @Override
    public Object getItem(int position) {
        return avatars.get(position);
    }

    @Override
    public long getItemId(int position) {
        return avatars.get(position).hashCode();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ImageView v;
        String avatar = avatars.get(position);
        int resource = AvatarManager.res(avatar);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_avatar_list, viewGroup, false);
        }

        v = view.findViewById(R.id.imageView);
        v.setImageResource(resource);

        if (avatar.equals(user.avatar())) {
            v.setBackgroundResource(R.drawable.avatar_list_item_bg);
        } else {
            v.setBackgroundResource(R.color.transparent);
        }
        return view;
    }
}