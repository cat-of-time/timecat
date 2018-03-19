package com.time.cat.ui.adapter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.time.cat.R;
import com.time.cat.TimeCatApp;
import com.time.cat.data.Constants;
import com.time.cat.data.database.DB;
import com.time.cat.data.model.APImodel.Note;
import com.time.cat.data.model.DBmodel.DBNote;
import com.time.cat.data.model.events.PersistenceEvents;
import com.time.cat.data.network.RetrofitHelper;
import com.time.cat.ui.activity.TimeCatActivity;
import com.time.cat.ui.activity.WebActivity;
import com.time.cat.ui.adapter.viewholder.TimeLineNotesViewHolder;
import com.time.cat.ui.modules.operate.InfoOperationActivity;
import com.time.cat.util.SearchEngineUtil;
import com.time.cat.util.UrlCountUtil;
import com.time.cat.util.clipboard.ClipboardUtils;
import com.time.cat.util.override.ToastUtil;
import com.time.cat.util.source.AvatarManager;
import com.time.cat.util.string.TimeUtil;
import com.timecat.commonjar.contentProvider.SPHelper;

import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/19
 * @discription null
 * @usage null
 */
public class TimeLineNotesAdapter extends BaseQuickAdapter<DBNote, TimeLineNotesViewHolder> {
    public TimeLineNotesAdapter() {
        super(R.layout.item_notes_list);
    }

    @Override
    protected void convert(TimeLineNotesViewHolder viewHolder, DBNote item) {
        Date date = TimeUtil.formatGMTDateStr(item.getCreated_datetime());
        viewHolder.setText(R.id.notes_tv_time, date.getMonth() + "月" + date.getDate() + "日")
                .setText(R.id.notes_tv_title, item.getTitle())
                .setText(R.id.notes_tv_content, item.getContent());

        viewHolder.setOnLongClickListener(R.id.notes_tv_title, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new MaterialDialog.Builder(mContext)
                        .content("确定删除这个任务吗？")
                        .positiveText("删除")
                        .onPositive((dialog, which) -> {
//                        LogUtil.e("dbNote == " + dbNote.toString());
                            try {
                                DB.notes().delete(item);
                                ToastUtil.ok("已删除");
                            } catch (SQLException e) {
                                e.printStackTrace();
                                ToastUtil.e("删除失败");
                            }
                            RetrofitHelper.getNoteService().deleteNoteByUrl(item.getUrl())
                                    .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                                    .observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
                                    .subscribe(new Subscriber<Note>() {
                                        @Override
                                        public void onCompleted() {

                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            //请求失败
//                                        ToastUtil.show("删除操作同步失败");
//                                        LogUtil.e("删除操作同步失败 --> " + e.toString());
                                        }

                                        @Override
                                        public void onNext(Note note) {
                                            //请求成功
//                                        ToastUtil.show("删除成功");
//                                        LogUtil.e("删除成功 --> " + note.toString());
                                        }
                                    });
                            Object event = new PersistenceEvents.NoteDeleteEvent();
                            TimeCatApp.eventBus().post(event);
                        })
                        .negativeText("取消")
                        .onNegative((dialog, which) -> dialog.dismiss()).show();
                return false;
            }
        });
        viewHolder.setOnClickListener(R.id.notes_tv_title, v -> {
            Intent intent2DialogActivity = new Intent(mContext, InfoOperationActivity.class);
            intent2DialogActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent2DialogActivity.putExtra(InfoOperationActivity.TO_SAVE_STR, item.getContent());
            Bundle bundle = new Bundle();
            bundle.putSerializable(InfoOperationActivity.TO_UPDATE_NOTE, item);
            intent2DialogActivity.putExtras(bundle);
            mContext.startActivity(intent2DialogActivity);
            ToastUtil.i("修改笔记");
        });
        viewHolder.setOnClickListener(R.id.notes_tv_content, v -> {
            UrlCountUtil.onEvent(UrlCountUtil.CLICK_TIMECAT_COPY);

            Intent intent = new Intent(Constants.BROADCAST_SET_TO_CLIPBOARD);
            intent.putExtra(Constants.BROADCAST_SET_TO_CLIPBOARD_MSG, item.getContent());
            mContext.sendBroadcast(intent);
            String finalText = item.getContent();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    ClipboardUtils.setText(mContext.getApplicationContext(), finalText);
                    ToastUtil.ok("已复制");
                }
            }, 100);
        });
        viewHolder.setOnClickListener(R.id.notes_iv_copy, v -> {
            UrlCountUtil.onEvent(UrlCountUtil.CLICK_TIMECAT_COPY);

            Intent intent = new Intent(Constants.BROADCAST_SET_TO_CLIPBOARD);
            intent.putExtra(Constants.BROADCAST_SET_TO_CLIPBOARD_MSG, item.getContent());
            mContext.sendBroadcast(intent);
            String finalText = item.getContent();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    ClipboardUtils.setText(mContext.getApplicationContext(), finalText);
                    ToastUtil.ok("已复制");
                }
            }, 100);
        });
        viewHolder.setOnClickListener(R.id.notes_iv_timecat, v -> {
            Intent intent2TimeCat = new Intent(mContext, TimeCatActivity.class);
            intent2TimeCat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent2TimeCat.putExtra(TimeCatActivity.TO_SPLIT_STR, item.getContent());
            mContext.startActivity(intent2TimeCat);
        });
        viewHolder.setOnClickListener(R.id.notes_iv_translate, v -> {
            Intent intent2Translate = new Intent(mContext, TimeCatActivity.class);
            intent2Translate.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent2Translate.putExtra(TimeCatActivity.TO_SPLIT_STR, item.getContent());
            intent2Translate.putExtra(TimeCatActivity.IS_TRANSLATE, true);
            mContext.startActivity(intent2Translate);
        });
        viewHolder.setOnClickListener(R.id.notes_iv_search, v -> {
            String content = item.getContent();
            UrlCountUtil.onEvent(UrlCountUtil.CLICK_TIMECAT_SEARCH);
            boolean isUrl = false;
            Uri uri = null;
            try {
                Pattern p = Pattern.compile("^((https?|ftp|news):\\/\\/)?([a-z]([a-z0-9\\-]*[\\.。])+([a-z]{2}|aero|arpa|biz|com|coop|edu|gov|info|int|jobs|mil|museum|name|nato|net|org|pro|travel)|(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))(\\/[a-z0-9_\\-\\.~]+)*(\\/([a-z0-9_\\-\\.]*)(\\?[a-z0-9+_\\-\\.%=&]*)?)?(#[a-z][a-z0-9_]*)?$", Pattern.CASE_INSENSITIVE);
                Matcher matcher = p.matcher(content);
                if (!matcher.matches()) {
                    uri = Uri.parse(SearchEngineUtil.getInstance().getSearchEngines().get(SPHelper.getInt(Constants.BROWSER_SELECTION, 0)).url + URLEncoder.encode(content, "utf-8"));
                    isUrl = false;
                } else {
                    uri = Uri.parse(content);
                    if (!content.startsWith("http")) {
                        content = "http://" + content;
                    }
                    isUrl = true;
                }

                boolean t = SPHelper.getBoolean(Constants.USE_LOCAL_WEBVIEW, true);
                Intent intent2Web;
                if (t) {
                    intent2Web = new Intent();
                    if (isUrl) {
                        intent2Web.putExtra("url", content);
                    } else {
                        intent2Web.putExtra("query", content);
                    }
                    intent2Web.setClass(mContext, WebActivity.class);
                    mContext.startActivity(intent2Web);
                } else {
                    intent2Web = new Intent(Intent.ACTION_VIEW, uri);
                    intent2Web.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent2Web);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Intent intent2web = new Intent();
                if (isUrl) {
                    intent2web.putExtra("url", content);
                } else {
                    intent2web.putExtra("query", content);
                }
                intent2web.setClass(mContext, WebActivity.class);
                mContext.startActivity(intent2web);
            }
        });
    }
}
