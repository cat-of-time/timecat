package com.time.cat.ui.modules.plans;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.time.cat.R;
import com.time.cat.data.Constants;
import com.time.cat.data.model.entity.FileEntity;
import com.time.cat.helper.StorageHelper;
import com.time.cat.ui.activity.main.listener.OnPlanViewClickListener;
import com.time.cat.ui.adapter.FilesAdapter;
import com.time.cat.ui.base.BaseFragment;
import com.time.cat.ui.modules.editor.EditorActivity;
import com.time.cat.ui.modules.editor.fragment.EditorFragment;
import com.time.cat.ui.modules.editor.QueryTask;
import com.time.cat.util.FileUtils;
import com.time.cat.util.override.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;

public class FileListFragment extends BaseFragment implements OnPlanViewClickListener{
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.file_list)
    RecyclerView fileListRecyclerView;
    @BindView(R.id.create_markdown_btn)
    FloatingActionButton createMarkdownBtn;
    @BindView(R.id.empty_list)
    RelativeLayout emptyList;

    @BindString(R.string.app_name) String appName;
    private String root = Environment.getExternalStorageDirectory().toString();
    private String rootPath;

    private FilesAdapter adapter;
    private List<FileEntity> entityList;
    private List<FileEntity> beforeSearch;

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentConfig(true, false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_filelist;
    }

    @Override
    public void initView() {
        toolbarTitle = appName;
        setDisplayHomeAsUpEnabled = false;
        super.initView();

        initVar(); // init variable
        setFab(); // set floating action button
//        setHasOptionsMenu(true); // set has options menu
        setRecyclerView(); // set recyclerview
        setSwipeRefreshLayout(); // set swipe refresh layout
    }

    public void initVar() {
        rootPath = root + "/" + appName + "/";
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appCompatActivity);
    }

    /**
     *  Set visibility to VISIBLE for floating action button menu, and set listener for the menu.
     *  If the button menu expanded, change background color for menu container.
     *  Otherwise, change background to transparent.
     */
    public void setFab() {
        final Fragment fragment = new EditorFragment();
        Bundle args = new Bundle();
        args.putBoolean("FROM_FILE", false);
        fragment.setArguments(args);
        createMarkdownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2MainActivity = new Intent();
                Bundle args = new Bundle();
                args.putBoolean(Constants.BUNDLE_KEY_FROM_FILE, false);
                intent2MainActivity.putExtras(args);
                intent2MainActivity.setClass(appCompatActivity, EditorActivity.class);
                startActivity(intent2MainActivity);
            }
        });
    }

//    /**
//     * Set item listener for navigation view.Open new fragment for each click action.
//     */
//    public void setNavigationViewItemListener() {
//        navigationView.setNavigationItemSelectedListener(
//                new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(MenuItem item) {
//                Fragment selectedFragment = null;
//                switch (item.getItemId()) {
//                    case R.id.sync:
////                        selectedFragment = new SyncFragment();
//                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                        builder.setMessage(R.string.dialog_message_alert_user);
//                        builder.setPositiveButton(R.string.ok,
//                                new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        });
//                        builder.show();
//                        break;
//                    case R.id.theme:
//                        break;
//                    case R.id.help:
//                        selectedFragment = new EditorHelpFragment();
//                        break;
//                    case R.id.settings:
//                        selectedFragment = new SettingsFragment();
//                        break;
//                }
//                navigationView.setCheckedItem(item.getItemId());
//                if (selectedFragment != null) {
//                    context.getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.fragment_container, selectedFragment)
//                            .addToBackStack(null).commit();
//                }
//                return true;
//            }
//        });
//    }

    public void setRecyclerView() {
        if (StorageHelper.isExternalStorageReadable()) {
            entityList = FileUtils.listFiles(rootPath);
            if (entityList != null && entityList.isEmpty()) {
                emptyList.setVisibility(View.VISIBLE);
            } else {
                emptyList.setVisibility(View.GONE);
                adapter = new FilesAdapter(entityList);
                fileListRecyclerView.setLayoutManager(new LinearLayoutManager(appCompatActivity));
                fileListRecyclerView.setItemAnimator(new DefaultItemAnimator());
                fileListRecyclerView.addItemDecoration(new DividerItemDecoration(appCompatActivity,
                        DividerItemDecoration.VERTICAL));
                fileListRecyclerView.setAdapter(adapter);
            }
        } else {
            Toast.makeText(appCompatActivity, R.string.toast_message_sdcard_unavailable,
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void setSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                if (entityList != null && adapter != null) {
                    Toast.makeText(appCompatActivity, entityList.size() + "", Toast.LENGTH_SHORT).show();
                    entityList.clear();
                    entityList.addAll(FileUtils.listFiles(rootPath));
                    adapter.notifyDataSetChanged();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onViewSortClick() {
        AlertDialog.Builder sortDialog = new AlertDialog.Builder(appCompatActivity);
        sortDialog.setTitle(R.string.menu_item_sort);
        int sortTypeIndex = sharedPreferences.getInt("SORT_TYPE_INDEX", 0);
        sortDialog.setSingleChoiceItems(R.array.sort_options, sortTypeIndex,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        sortDialog.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        sortDialog.show();
    }

    @Override
    public void initSearchView(Menu menu, AppCompatActivity activity) {
        MenuItem searchItem = menu.findItem(R.id.main_menu_plan_search);
        SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    // search files
                    if (StorageHelper.isExternalStorageReadable()) {
                        beforeSearch = new ArrayList<>(entityList);
                        new QueryTask(rootPath, query, new QueryTask.Response() {
                            @Override
                            public void onTaskFinish(List<FileEntity> entityList) {
                                FileListFragment.this.entityList.clear();
                                FileListFragment.this.entityList.addAll(entityList);
                                adapter.notifyDataSetChanged();
                            }
                        }).execute();
                    } else {
                        ToastUtil.show(R.string.toast_message_sdcard_unavailable);
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (entityList != null && adapter != null && beforeSearch != null) {
                    entityList.clear();
                    entityList.addAll(beforeSearch);
                    adapter.notifyDataSetChanged();
                }
                return true;
            }
        });
    }
}
