package com.time.cat.ui.modules.setting.card;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.time.cat.ui.modules.setting.SettingFloatViewActivity;
import com.timecat.commonjar.contentProvider.SPHelper;
import com.time.cat.R;
import com.time.cat.ui.modules.whitelist.SelectionDbHelper;
import com.time.cat.ui.base.baseCard.AbsCard;
import com.time.cat.ui.widgets.dialog.Dialog;
import com.time.cat.ui.widgets.dialog.DialogFragment;
import com.time.cat.ui.widgets.dialog.SimpleDialog;
import com.time.cat.util.string.AESUtils;
import com.time.cat.data.Constants;
import com.time.cat.util.IOUtil;
import com.time.cat.util.override.LogUtil;
import com.time.cat.util.phone.NativeHelper;
import com.time.cat.util.override.ToastUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static com.timecat.commonjar.contentProvider.SPHelperImpl.MAINSPNAME;

public class SLSettingCard extends AbsCard {

    public SLSettingCard(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;

        LayoutInflater.from(context).inflate(R.layout.card_sl_setting, this);

        findViewById(R.id.default_setting_rl).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDefaultDialog();
            }
        });

        findViewById(R.id.save_setting_rl).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaveDialog();
            }
        });

        findViewById(R.id.load_setting_rl).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadDialog();
            }
        });

    }

    private void showLoadDialog() {
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {

            @Override
            protected void onBuildDone(Dialog dialog) {
                dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                super.onBuildDone(dialog);
            }

            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                // 这里是保持开启
                super.onPositiveActionClicked(fragment);
                loadSettings();
            }

            @Override
            public void onDismiss(DialogInterface dialog) {
                super.onCancel(dialog);
            }
        };
        builder.message(mContext.getString(R.string.load_setting_tips)).positiveAction(mContext.getString(R.string.confirm)).negativeAction(mContext.getString(R.string.cancel));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), null);
    }


    private void showSaveDialog() {
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {

            @Override
            protected void onBuildDone(Dialog dialog) {
                dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                super.onBuildDone(dialog);
            }

            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                // 这里是保持开启
                super.onPositiveActionClicked(fragment);
                saveSettings();
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
                saveOCR();
            }

            @Override
            public void onDismiss(DialogInterface dialog) {
                super.onCancel(dialog);
            }
        };
        builder.message(mContext.getString(R.string.save_setting_tips)).positiveAction(mContext.getString(R.string.save_other)).negativeAction(mContext.getString(R.string.only_save_ocr)).neutralAction(mContext.getString(R.string.cancel));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), null);
    }


    private void showDefaultDialog() {
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {

            @Override
            protected void onBuildDone(Dialog dialog) {
                dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                super.onBuildDone(dialog);
            }

            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                // 这里是保持开启
                defaultSettings();
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onDismiss(DialogInterface dialog) {
                super.onCancel(dialog);
            }
        };
        builder.message(mContext.getString(R.string.default_setting_tips)).positiveAction(mContext.getString(R.string.confirm)).negativeAction(mContext.getString(R.string.cancel));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), null);
    }

    private void defaultSettings() {
        String ocr = SPHelper.getString(Constants.DIY_OCR_KEY, "");
        SPHelper.clear();
        SPHelper.save(Constants.DIY_OCR_KEY, ocr);
        IOUtil.delete(SettingFloatViewActivity.FLOATVIEW_IMAGE_PATH);
        SelectionDbHelper helper = new SelectionDbHelper(mContext);
        helper.deleteAll();
        File spDir = new File(mContext.getFilesDir().getParentFile() + File.separator + "shared_prefs", "sp_name.xml");
        if (spDir.exists()) {
            spDir.delete();
        }

        mContext.sendBroadcast(new Intent(Constants.EFFECT_AFTER_REBOOT_BROADCAST));

        ToastUtil.ok(R.string.effect_after_reboot);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Process.killProcess(Process.myPid());
            }
        }, 1500);
    }

    private void saveSettings() {
        String ocr = SPHelper.getString(Constants.DIY_OCR_KEY, "");
        SPHelper.save(Constants.DIY_OCR_KEY, "");

        File file = mContext.getFilesDir();
        File dbDir = new File(file.getParentFile(), "databases");
        File spDir = new File(file.getParentFile(), "shared_prefs");

        File desDir = new File(Environment.getExternalStorageDirectory() + File.separator + "timecat/backup");

        try {
            IOUtil.copyFile(dbDir.getAbsolutePath(), new File(desDir, "databases").getAbsolutePath());
            IOUtil.copyFile(SettingFloatViewActivity.FLOATVIEW_IMAGE_PATH, new File(desDir, "floatview.png").getAbsolutePath());
            IOUtil.copyFile(spDir.getAbsolutePath(), new File(desDir, "shared_prefs").getAbsolutePath());
            ToastUtil.ok(R.string.save_success);
        } catch (IOException e) {
            ToastUtil.e(R.string.save_fail);
        }
        SPHelper.save(Constants.DIY_OCR_KEY, ocr);
    }

    private void saveOCR() {
        String ocr = SPHelper.getString(Constants.DIY_OCR_KEY, "");
        String imei = NativeHelper.getImei(mContext);
        String cpu = NativeHelper.getCpuAbi();
        LogUtil.d("ocr=" + ocr);
        LogUtil.d("imei=" + imei);
        LogUtil.d("cpu=" + cpu);

        File desOCRFile = new File(Environment.getExternalStorageDirectory() + File.separator + "timecat/backup/OCR/ocr.txt");
        desOCRFile.getParentFile().mkdirs();
        String ocrEncrypt = AESUtils.encrypt(imei + cpu, ocr);
        InputStream inputStream = new ByteArrayInputStream(ocrEncrypt.getBytes());
        try {
            IOUtil.saveToFile(inputStream, desOCRFile);
            ToastUtil.ok(R.string.save_success);
        } catch (IOException e) {
            ToastUtil.e(R.string.save_fail);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void loadSettings() {
        boolean dbRestore = true;
        boolean spRestore = true;
        String toast = "";
        String toastEnd = mContext.getString(R.string.effect_after_reboot);
        File file = mContext.getFilesDir();
        File dbDir = new File(file.getParentFile(), "databases");
        File spDir = new File(file.getParentFile().getAbsolutePath() + "/shared_prefs");


        File desDbDir = new File(Environment.getExternalStorageDirectory() + File.separator + "timecat/backup/databases");
        File desSpFile = new File(Environment.getExternalStorageDirectory() + File.separator + "timecat/backup/shared_prefs");
        File floatViewFile = new File(Environment.getExternalStorageDirectory() + File.separator + "timecat/backup/", "floatview.png");

        if (floatViewFile.exists()) {
            try {
                IOUtil.copyFile(floatViewFile.getAbsolutePath(), SettingFloatViewActivity.FLOATVIEW_IMAGE_PATH);
            } catch (IOException e) {
            }
        }

        if (desDbDir.exists()) {
            IOUtil.deleteDirs(dbDir.getAbsolutePath());
            try {
                IOUtil.copyFile(desDbDir.getAbsolutePath(), dbDir.getAbsolutePath());
                dbRestore = true;
            } catch (IOException e) {
                dbRestore = false;

            }
        }

        String ocrOrigin = SPHelper.getString(Constants.DIY_OCR_KEY, "");
        if (desSpFile.exists()) {
            SPHelper.clear();
            IOUtil.deleteDirs(spDir.getAbsolutePath());
            try {
                IOUtil.copyFile(desSpFile.getAbsolutePath(), spDir.getAbsolutePath());
                spRestore = true;
            } catch (IOException e) {
                spRestore = false;
            }
        }


        String imei = NativeHelper.getImei(mContext);
        String cpu = NativeHelper.getCpuAbi();
        LogUtil.d("imei=" + imei);
        LogUtil.d("cpu=" + cpu);

        File desOCRFile = new File(Environment.getExternalStorageDirectory() + File.separator + "timecat/backup/OCR/ocr.txt");
        if (!desOCRFile.exists()) {
            return;
        }
        String ocrBackup = null;
        try {
            String ocrEncrypt = IOUtil.readString(desOCRFile);
            ocrBackup = AESUtils.decrypt(imei + cpu, ocrEncrypt);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String ocr = "";
        if (!dbRestore || !spRestore) {
            toast = mContext.getString(R.string.restore_failed);
        } else {
            toast = mContext.getString(R.string.restore_success);
        }
        if (!TextUtils.isEmpty(ocrOrigin)) {
            ocr = ocrOrigin;
            toast += mContext.getString(R.string.restore_ocr_origin);
        } else if (!TextUtils.isEmpty(ocrBackup)) {
            ocr = ocrBackup;
            toast += mContext.getString(R.string.restore_ocr_back);
        }
        saveOcrKeyWithSP(ocr);

        mContext.sendBroadcast(new Intent(Constants.EFFECT_AFTER_REBOOT_BROADCAST));
        ToastUtil.i(toast + toastEnd);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Process.killProcess(Process.myPid());
            }
        }, 1500);
    }

    private void saveOcrKeyWithSP(String ocrKey) {
        SharedPreferences sp = mContext.getSharedPreferences(MAINSPNAME, Context.MODE_PRIVATE);
        sp.edit().putString(Constants.DIY_OCR_KEY, ocrKey).apply();
    }

}
