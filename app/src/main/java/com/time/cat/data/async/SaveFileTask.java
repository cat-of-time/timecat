package com.time.cat.data.async;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;

import com.time.cat.R;
import com.time.cat.util.FileUtils;
import com.time.cat.util.override.ToastUtil;

public class SaveFileTask extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private String filePath;
    private String fileName;
    private String content;
    private Response response;

    public SaveFileTask(Context context, String filePath, String fileName, String content,
                        Response response) {
        this.context = context;
        this.filePath = filePath;
        this.fileName = fileName;
        this.content = content;
        this.response = response;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean result;
        Handler handler = new Handler(context.getMainLooper());
        if (TextUtils.isEmpty(fileName)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.w(R.string.toast_file_name_can_not_empty);
                }
            });
            result = false;
        } else {
            result = FileUtils.saveFile(filePath, content);
            if (result) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.ok(R.string.toast_saved);
                    }
                });
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.w(R.string.toast_file_name_exists);
                    }
                });
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        response.taskFinish(aBoolean);
    }

    public interface Response {
        void taskFinish(Boolean result);
    }
}
