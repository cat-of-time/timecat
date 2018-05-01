package com.time.cat.ui.modules.editor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.chinalwb.are.AREditor;
import com.rey.material.widget.Button;
import com.time.cat.R;
import com.time.cat.ui.base.mvp.BaseActivity;
import com.time.cat.ui.widgets.FlipView.FlipMenuItem;
import com.time.cat.ui.widgets.FlipView.FlipMenuView;
import com.time.cat.util.override.ToastUtil;
import com.time.cat.util.view.ImageUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.chinalwb.are.styles.toolbar.ARE_Toolbar.REQ_IMAGE;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/4/10
 * @discription null
 * @usage null
 */
public class RichTextEditorActivity extends BaseActivity<RichTextEditorMVP.View, RichTextEditorPresenter> implements View.OnClickListener {

    public static final String TO_SAVE_IMG = "to_save_img";

    private AREditor arEditor;
    TextView tv_type;
    Button bt_submit;

    @Override
    protected int layout() {
        return R.layout.activity_richtext_editor;
    }

    @Override
    protected boolean canBack() {
        return true;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @NonNull
    @Override
    public RichTextEditorPresenter providePresenter() {
        return new RichTextEditorPresenter();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv_type = findViewById(R.id.tv_type);
        bt_submit = findViewById(R.id.bt_submit);
        arEditor = findViewById(R.id.areditor);
        arEditor.getARE().setTextColor(Color.BLACK);
//        new AREditor.Builder(this).setLayoutRes(R.id.areditor).build();

        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.type).setOnClickListener(this);
        findViewById(R.id.bt_submit).setOnClickListener(this);

        String filename = getIntent().getStringExtra(TO_SAVE_IMG);
        Uri uri = null;
        Intent data = null;
        if (filename != null) {
            uri = getMediaUriFromPath(this, filename);
        }
        if (uri != null) {
            data = new Intent("com.time.cat.uri", uri);
        }
        if (data != null) {
            this.arEditor.onActivityResult(REQ_IMAGE, Activity.RESULT_OK, data);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.arEditor.onActivityResult(requestCode, resultCode, data);
    }


    @SuppressLint("SimpleDateFormat")
    private void saveHtml(String html) {
        try {
            String filePath = Environment.getExternalStorageDirectory() + File.separator + "ARE" + File.separator;
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdir();
            }

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh_mm_ss");
            String time = dateFormat.format(new Date());
            String fileName = time.concat(".html");

            File file = new File(filePath + fileName);
            if (!file.exists()) {
                boolean isCreated = file.createNewFile();
                if (!isCreated) {
                    ToastUtil.e("Cannot create file at: " + filePath);
                    return;
                }
            }

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(html);
            fileWriter.close();

            ToastUtil.ok(fileName + " has been saved at " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            ToastUtil.e("Run into error: " + e.getMessage());
        }
    }

    public static Uri getImageStreamFromExternal(String imageName) {
        File externalPubPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File picPath = new File(externalPubPath, imageName);
        Uri uri = null;
        if (picPath.exists()) {
            uri = Uri.fromFile(picPath);
        }

        return uri;
    }

    public static Uri getMediaUriFromPath(Context context, String path) {
        Uri mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(mediaUri, null, MediaStore.Images.Media.DISPLAY_NAME + "= ?", new String[]{path.substring(path.lastIndexOf("/") + 1)}, null);

        Uri uri = null;
        if (cursor.moveToFirst()) {
            uri = ContentUris.withAppendedId(mediaUri, cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
        }
        cursor.close();
        return uri;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:finish();break;
            case R.id.bt_submit: finish();break;
            case R.id.type:
                FlipMenuView share = new FlipMenuView.Builder(getActivity(), findViewById(R.id.more))
                        .addItem(new FlipMenuItem("笔记", Color.parseColor("#03A9F4"), 0xffeeeeee, ImageUtil.getBitmapFromVectorDrawable(getActivity(), R.drawable.ic_notes_blue_24dp)))
                        .addItem(new FlipMenuItem("任务", Color.parseColor("#03A9F4"), 0xffeeeeee, ImageUtil.getBitmapFromVectorDrawable(getActivity(), R.drawable.ic_schedules_blue_24dp)))
                        .addItem(new FlipMenuItem("闹钟", Color.parseColor("#03A9F4"), 0xffeeeeee, ImageUtil.getBitmapFromVectorDrawable(getActivity(), R.drawable.ic_routines_blue_24dp)))
                        .addItem(new FlipMenuItem("计划", Color.parseColor("#03A9F4"), 0xffeeeeee, ImageUtil.getBitmapFromVectorDrawable(getActivity(), R.drawable.ic_plans_active_blue_24dp)))
                        .create();

                share.setOnFlipClickListener(new FlipMenuView.OnFlipClickListener() {
                    @Override
                    public void onItemClick(int position1) {
                        switch (position1) {
                            case 0:
                                tv_type.setText("笔记");
                                ToastUtil.w("TODO！我们会继续完善，请支持我们喵~~");
                                break;
                            case 1:
                                tv_type.setText("任务");
                                ToastUtil.w("TODO！我们会继续完善，请支持我们喵~~");
                                break;
                            case 2:
                                tv_type.setText("闹钟");
                                ToastUtil.w("TODO！我们会继续完善，请支持我们喵~~");
                                break;
                            case 3:
                                tv_type.setText("计划");
                                ToastUtil.w("TODO！我们会继续完善，请支持我们喵~~");
                                break;
                        }
                    }

                    @Override
                    public void dismiss() {

                    }
                });
                break;
        }
    }
}
