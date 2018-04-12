package com.time.cat.ui.modules.plans.card_view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.time.cat.R;
import com.time.cat.TimeCatApp;
import com.time.cat.data.database.DB;
import com.time.cat.data.model.Converter;
import com.time.cat.data.model.DBmodel.DBPlan;
import com.time.cat.data.model.events.PersistenceEvents;
import com.time.cat.util.override.ToastUtil;
import com.time.cat.util.string.TimeUtil;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/29
 * @discription null
 * @usage null
 */
public class CardLastFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.add)
    RelativeLayout add;
    @BindView(R.id.image)
    ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card_last, null);
        ButterKnife.bind(this, rootView);
        add.setOnClickListener(this);
        imageView.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
            case R.id.image:
                Activity activity = (Activity) getContext();
                if (activity != null) {
                    new MaterialDialog.Builder(activity)
                            .title("制定一个计划")
                            .inputType(InputType.TYPE_CLASS_TEXT)
                            .input("计划名称", "", (dialog, input) -> {
                                // Do something
                                DBPlan dbPlan = new DBPlan();
                                dbPlan.setTitle("" + input);
                                dbPlan.setCoverImageUrl("http://img.hb.aicdn.com/3f04db36f22e2bf56d252a3bc1eacdd2a0416d75221a7c-rpihP1_fw658");
                                dbPlan.setCreated_datetime(TimeUtil.formatGMTDate(new Date()));
                                dbPlan.setUpdate_datetime(dbPlan.getCreated_datetime());
                                dbPlan.setOwner(Converter.getOwnerUrl(DB.users().getActive()));
                                DB.plans().safeSaveDBPlan(dbPlan);
                                Object event = new PersistenceEvents.PlanCreateEvent(dbPlan);
                                TimeCatApp.eventBus().post(event);
                                ToastUtil.ok("制定[计划]：" + dbPlan.getTitle());
                            }).show();
                }
                break;
        }
    }
}
