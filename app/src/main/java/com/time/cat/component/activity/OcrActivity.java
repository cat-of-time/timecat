package com.time.cat.component.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.microsoft.projectoxford.vision.contract.Line;
import com.microsoft.projectoxford.vision.contract.OCR;
import com.microsoft.projectoxford.vision.contract.Region;
import com.microsoft.projectoxford.vision.contract.Word;
import com.shang.commonjar.contentProvider.SPHelper;
import com.shang.utils.StatusBarCompat;
import com.time.cat.R;
import com.time.cat.component.base.BaseActivity;
import com.time.cat.mvp.model.APImodel.ImageUpload;
import com.time.cat.mvp.view.DialogFragment;
import com.time.cat.mvp.view.SimpleDialog;
import com.time.cat.util.ConstantUtil;
import com.time.cat.util.OcrAnalyser;
import com.time.cat.util.SnackBarUtil;
import com.time.cat.util.ToastUtil;
import com.time.cat.util.UrlCountUtil;
import com.time.cat.util.cropper.BitmapUtil;
import com.time.cat.util.cropper.CropHandler;
import com.time.cat.util.cropper.CropHelper;
import com.time.cat.util.cropper.CropParams;
import com.time.cat.util.cropper.ImageUriUtil;
import com.time.cat.util.cropper.handler.CropImage;

import static com.time.cat.component.activity.screen.CaptureResultActivity.HTTP_IMAGE_BAIDU_COM;


/**
 * Created by wangyan-pd on 2016/11/9.
 */

public class OcrActivity extends BaseActivity implements View.OnClickListener, CropHandler {
    private static final String TAG = OcrActivity.class.getName();
    private CropParams mCropParams;
    private ImageView mImageView;
    private AppCompatEditText editText;
    private Button mPicReOcr;
    private Uri mCurrentUri;

    // private boolean shouldShowDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orc);
        StatusBarCompat.setupStatusBarView(this, (ViewGroup) getWindow().getDecorView(), true, R.color.colorPrimary);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.ocr_picture);


        mCropParams = new CropParams(this);
        mImageView = findViewById(R.id.image);
        editText = findViewById(R.id.result);
        mPicReOcr = findViewById(R.id.re_ocr);
        findViewById(R.id.take_pic).setOnClickListener(this);
        findViewById(R.id.select_pic).setOnClickListener(this);
        findViewById(R.id.re_ocr).setOnClickListener(this);
        parseIntent(getIntent());

        findViewById(R.id.hint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_OCR_TO_TIMECAT_ACTIVITY);
                Intent intent = new Intent(OcrActivity.this, TimeCatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(TimeCatActivity.TO_SPLIT_STR, editText.getText().toString());
                startActivity(intent);
            }
        });
    }

    private void parseIntent(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (intent.getClipData() != null && intent.getClipData().getItemAt(0) != null && intent.getClipData().getItemAt(0).getUri() != null) {
                Uri uri = intent.getClipData().getItemAt(0).getUri();
                showBitmapandOcr(uri);
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_OCR_FROM_SHARE);
            }
        } else if (intent.getData() != null) {
            Uri uri = intent.getData();
            showBitmapandOcr(uri);
            UrlCountUtil.onEvent(UrlCountUtil.CLICK_OCR_FROM_SHARE);
        }

    }

    private void showBitmapandOcr(Uri uri) {
        mImageView.setVisibility(View.VISIBLE);
        mImageView.setImageBitmap(BitmapUtil.decodeUriAsBitmap(this, uri));
        uploadImage4Ocr(uri);
        mCurrentUri = uri;
        showSearchOcr(mCurrentUri);
    }

    private void showSearchOcr(Uri uri) {
        String img_path = ImageUriUtil.getImageAbsolutePath(this, uri);
        findViewById(R.id.search).setVisibility(View.VISIBLE);
        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(R.string.upload_img);
                OcrAnalyser.getInstance().uploadImage(OcrActivity.this, img_path, new OcrAnalyser.ImageUploadCallBack() {
                    @Override
                    public void onSuccess(ImageUpload imageUpload) {
                        if (imageUpload != null && imageUpload.getData() != null && !TextUtils.isEmpty(imageUpload.getData().getUrl())) {

                            String url = HTTP_IMAGE_BAIDU_COM + "queryImageUrl=" + imageUpload.getData().getUrl() + "&querySign=4074500770,3618317556&fromProduct= ";
                            Intent intent = new Intent();
                            intent.putExtra("url", url);
                            intent.setClass(OcrActivity.this, WebActivity.class);
                            startActivity(intent);
                        } else {
                            ToastUtil.show(R.string.upload_img_fail);
                        }

                    }

                    @Override
                    public void onFail(Throwable throwable) {
                        ToastUtil.show(throwable.getMessage());
                    }
                });
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        parseIntent(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        mCropParams.refreshUri();

        switch (v.getId()) {
            case R.id.take_pic:
                try {
                    UrlCountUtil.onEvent(UrlCountUtil.CLICK_OCR_TAKEPICTURE);
                    mCropParams.enable = true;
                    mCropParams.compress = false;
                    Intent intent = CropHelper.buildCameraIntent(mCropParams);
                    startActivityForResult(intent, CropHelper.REQUEST_CAMERA);
                    mPicReOcr.setVisibility(View.GONE);
                } catch (Throwable e) {
                }

                break;
            case R.id.select_pic:

                try {
                    UrlCountUtil.onEvent(UrlCountUtil.CLICK_OCR_PICK_FROM_GALLERY);
                    mCropParams.enable = false;
                    mCropParams.compress = false;
                    Intent intent1 = CropHelper.buildGalleryIntent(mCropParams);
                    startActivityForResult(intent1, CropHelper.REQUEST_CROP);
                    mPicReOcr.setVisibility(View.GONE);
                } catch (Throwable e) {
                }
                break;
            case R.id.re_ocr:
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_OCR_REOCR);
                if (mCurrentUri != null) uploadImage4Ocr(mCurrentUri);
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == -1) {
            CropImage.ActivityResult result = data.getExtras().getParcelable(CropImage.CROP_IMAGE_EXTRA_RESULT);
            mCurrentUri = result.getUri();
            showBitmapandOcr(mCurrentUri);

        } else {

            CropHelper.handleResult(this, requestCode, resultCode, data);
        }
        if (requestCode == 1) {
            Log.e(TAG, "");
        }
    }

    @Override
    protected void onDestroy() {
        CropHelper.clearCacheDir();
        super.onDestroy();
    }

    @Override
    public CropParams getCropParams() {
        return mCropParams;
    }

    @Override
    public void onPhotoCropped(Uri uri) {
        // Original or Cropped uri
        Log.d(TAG, "Crop Uri in path: " + uri.getPath());
        CropImage.activity(uri).start(OcrActivity.this);

    }

    private void showBeyondQuoteDialog() {
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {

            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                // 这里是保持开启
                super.onPositiveActionClicked(fragment);
                Intent intent = new Intent();
                intent.setClass(OcrActivity.this, DiyOcrKeyActivity.class);
                startActivity(intent);
            }

            @Override
            public void onDismiss(DialogInterface dialog) {
                super.onCancel(dialog);
                //  shouldShowDialog = false;
            }
        };
        builder.message(this.getString(R.string.ocr_quote_beyond_time)).positiveAction(this.getString(R.string.free_use));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getSupportFragmentManager(), null);
    }

    private void uploadImage4Ocr(Uri uri) {
        String img_path = ImageUriUtil.getImageAbsolutePath(this, uri);
        findViewById(R.id.hint).setVisibility(View.VISIBLE);
        if (SPHelper.getInt(ConstantUtil.OCR_TIME, 0) == ConstantUtil.OCR_TIME_TO_ALERT) {
            int time = SPHelper.getInt(ConstantUtil.OCR_TIME, 0) + 1;
            SPHelper.save(ConstantUtil.OCR_TIME, time);
            return;
        }
        editText.setText(R.string.recognize);
        OcrAnalyser.getInstance().analyse(this, img_path, true, new OcrAnalyser.CallBack() {
            @Override
            public void onSuccess(OCR ocr) {
                editText.setText(OcrAnalyser.getInstance().getPassedMiscSoftText(ocr));
            }

            @Override
            public void onFail(Throwable throwable) {

                if (SPHelper.getString(ConstantUtil.DIY_OCR_KEY, "").equals("")) {
                    ToastUtil.show(getResources().getString(R.string.ocr_useup_toast));
                }
                editText.setText(R.string.sorry_for_parse_fail);
                mPicReOcr.setVisibility(View.VISIBLE);
            }
        });

    }

    private String getPasedMiscSoftText(OCR r) {
        String result = "";
        for (Region reg : r.regions) {
            for (Line line : reg.lines) {
                for (Word word : line.words) {
                    result += word.text + " ";
                }
                result += "\n";
            }
            result += "\n\n";
        }
        return result;
    }

    private void showBigBang(String result) {
        Intent intent = new Intent(this, TimeCatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(TimeCatActivity.TO_SPLIT_STR, result);
        startActivity(intent);
    }

    @Override
    public void onCompressed(Uri uri) {
        // Compressed uri
        mImageView.setImageBitmap(BitmapUtil.decodeUriAsBitmap(this, uri));

    }

    @Override
    public void onCancel() {
        SnackBarUtil.show(editText, "Crop canceled!");
    }

    @Override
    public void onFailed(String message) {
        SnackBarUtil.show(editText, "Crop failed: " + message);
    }

    @Override
    public void handleIntent(Intent intent, int requestCode) {
        try {
            startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
