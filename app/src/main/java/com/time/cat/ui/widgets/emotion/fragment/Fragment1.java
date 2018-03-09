package com.time.cat.ui.widgets.emotion.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.time.cat.R;
import com.time.cat.ui.base.BaseFragment;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/17
 * @discription null
 * @usage null
 */
public class Fragment1 extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView= LayoutInflater.from(getActivity()).inflate(R.layout.fragment1,null);
        TextView tv= (TextView) rootView.findViewById(R.id.tv);

        tv.setText(args.getString("Interge"));
        return rootView ;
    }
}
