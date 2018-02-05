package com.time.cat.component.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.time.cat.R;
import com.time.cat.ThemeSystem.manager.ThemeManager;


/**
 * @author dlinking-lxy
 * @date 17-7-19
 * @discription
 */
public class DialogThemeFragment extends DialogFragment implements View.OnClickListener {

    public static final String TAG = "DialogThemeFragment";
    ImageView[] mCards = new ImageView[12];
    Button mConfirm;
    Button mCancel;

    private int mCurrentTheme;
    private ClickListener mClickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentTheme = ThemeManager.getTheme(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_choose_theme, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCancel = view.findViewById(R.id.dialog_theme_cancel);
        mConfirm = view.findViewById(R.id.dialog_theme_confirm);
        mCards[0] = view.findViewById(R.id.theme_pink);
        mCards[1] = view.findViewById(R.id.theme_purple);
        mCards[2] = view.findViewById(R.id.theme_blue);
        mCards[3] = view.findViewById(R.id.theme_green);
        mCards[4] = view.findViewById(R.id.theme_green_light);
        mCards[5] = view.findViewById(R.id.theme_yellow);
        mCards[6] = view.findViewById(R.id.theme_orange);
        mCards[7] = view.findViewById(R.id.theme_red);
        mCards[8] = view.findViewById(R.id.theme_white);
        mCards[9] = view.findViewById(R.id.theme_black);
        mCards[10] = view.findViewById(R.id.theme_grey);
        mCards[11] = view.findViewById(R.id.theme_magenta);
        setImageButtons(mCurrentTheme);
        for (ImageView card : mCards) {
            card.setOnClickListener(this);
        }
        mCancel.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick");
        switch (v.getId()) {
            case R.id.dialog_theme_confirm:
                if (mClickListener != null) {
                    Log.i(TAG, "onConfirm");
                    mClickListener.onConfirm(mCurrentTheme);
                }
            case R.id.dialog_theme_cancel:
                dismiss();
                break;
            case R.id.theme_pink:
                mCurrentTheme = ThemeManager.CARD_SAKURA;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_purple:
                mCurrentTheme = ThemeManager.CARD_HOPE;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_blue:
                mCurrentTheme = ThemeManager.CARD_STORM;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_green:
                mCurrentTheme = ThemeManager.CARD_WOOD;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_green_light:
                mCurrentTheme = ThemeManager.CARD_LIGHT;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_yellow:
                mCurrentTheme = ThemeManager.CARD_THUNDER;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_orange:
                mCurrentTheme = ThemeManager.CARD_SAND;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_red:
                mCurrentTheme = ThemeManager.CARD_FIRE;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_white:
                mCurrentTheme = ThemeManager.CARD_WHITE;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_black:
                mCurrentTheme = ThemeManager.CARD_BLACK;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_grey:
                mCurrentTheme = ThemeManager.CARD_GREY;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_magenta:
                mCurrentTheme = ThemeManager.CARD_MAGENTA;
                setImageButtons(mCurrentTheme);
                break;
            default:
                break;
        }
    }

    private void setImageButtons(int currentTheme) {
        mCards[0].setSelected(currentTheme == ThemeManager.CARD_SAKURA);
        mCards[1].setSelected(currentTheme == ThemeManager.CARD_HOPE);
        mCards[2].setSelected(currentTheme == ThemeManager.CARD_STORM);
        mCards[3].setSelected(currentTheme == ThemeManager.CARD_WOOD);
        mCards[4].setSelected(currentTheme == ThemeManager.CARD_LIGHT);
        mCards[5].setSelected(currentTheme == ThemeManager.CARD_THUNDER);
        mCards[6].setSelected(currentTheme == ThemeManager.CARD_SAND);
        mCards[7].setSelected(currentTheme == ThemeManager.CARD_FIRE);
        mCards[8].setSelected(currentTheme == ThemeManager.CARD_WHITE);
        mCards[9].setSelected(currentTheme == ThemeManager.CARD_BLACK);
        mCards[10].setSelected(currentTheme == ThemeManager.CARD_GREY);
        mCards[11].setSelected(currentTheme == ThemeManager.CARD_MAGENTA);
    }

    public void setClickListener(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public interface ClickListener {
        void onConfirm(int currentTheme);
    }


}
