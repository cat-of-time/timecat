package com.time.cat.ui.activity.about;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.mikepenz.aboutlibraries.LibsBuilder;
import com.time.cat.R;
import com.time.cat.ui.base.BaseActivity;

/**
 * @author dlink
 * @date 2018/2/3
 * @discription
 */
public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setupToolbar("关于", getResources().getColor(R.color.dark_grey_home));
        setupStatusBar(getResources().getColor(R.color.dark_grey_home));

        if (savedInstanceState == null) {

            Fragment fragment = new LibsBuilder().withAboutAppName("TimeCat").withAboutIconShown(true).withAboutVersionShown(true).withLicenseShown(true).withLicenseDialog(true).withAboutDescription(getString(R.string.about_description)).fragment();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.fragment_holder, fragment).commit();
        }
    }
}
