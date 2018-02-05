package com.time.cat.component.activity.main.routines;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.doomonafireball.betterpickers.timepicker.TimePickerBuilder;
import com.doomonafireball.betterpickers.timepicker.TimePickerDialogFragment;
import com.time.cat.R;
import com.time.cat.TimeCatApp;
import com.time.cat.database.DB;
import com.time.cat.database.Routine;
import com.time.cat.util.Snack;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

public class RoutineCreateOrEditFragment extends DialogFragment implements
                                                                RadialTimePickerDialog.OnTimeSetListener,
                                                                TimePickerDialogFragment.TimePickerDialogHandler {

    OnRoutineEditListener mRoutineEditCallback;
    Routine mRoutine;

    Button timeButton;
    TextView mNameTextView;
    Button mConfirmButton;

    int hour;
    int minute;

    int pColor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_or_edit_routine, container, false);

        pColor = DB.users().getActive(getActivity()).color();

        mNameTextView = (TextView) rootView.findViewById(R.id.routine_edit_name);
        timeButton = (Button) rootView.findViewById(R.id.button2);

        timeButton.setTextColor(pColor);

        long routineId = -1;

        if (getArguments() != null) {
            routineId = getArguments().getLong(TimeCatApp.INTENT_EXTRA_ROUTINE_ID, -1);
        }

        if (routineId == -1 && savedInstanceState != null) {
            routineId = savedInstanceState.getLong(TimeCatApp.INTENT_EXTRA_ROUTINE_ID, -1);
        }

        if (routineId != -1) {
            mRoutine = Routine.findById(routineId);
            setRoutine(mRoutine);
            hour = mRoutine.time().getHourOfDay();
            minute = mRoutine.time().getMinuteOfHour();
        } else {
            DateTime now = DateTime.now();
            hour = now.getHourOfDay();
            minute = now.getMinuteOfHour();
        }

        if (getDialog() != null) {
            getDialog().setTitle("Create routine");
            mConfirmButton = (Button) rootView.findViewById(R.id.done_button);
            mConfirmButton.setVisibility(View.VISIBLE);
            mConfirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onEdit();
                }
            });
        }


        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                float density = getResources().getDisplayMetrics().densityDpi;
                Log.d("RoutineCEFragment", "Density: " + density);
                if (density >= DisplayMetrics.DENSITY_XHIGH) {
                    RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
                            .newInstance(RoutineCreateOrEditFragment.this, hour, minute, true);
                    timePickerDialog.show(getChildFragmentManager(), "111");
                } else {
                    TimePickerBuilder tpb = new TimePickerBuilder()
                            .setFragmentManager(getChildFragmentManager())
                            .setStyleResId(R.style.BetterPickersDialogFragment_Light);
                    tpb.addTimePickerDialogHandler(RoutineCreateOrEditFragment.this);
                    tpb.show();
                }

            }
        });

        updateTime();


        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRoutine != null)
            outState.putLong(TimeCatApp.INTENT_EXTRA_ROUTINE_ID, mRoutine.getId());
    }

    private void setRoutine(Routine r) {
        Log.d(getTag(), "Routine set: " + r.name());
        mRoutine = r;
        mNameTextView.setText(mRoutine.name());
        updateTime();
    }


    void updateTime() {
        timeButton.setText(new LocalTime(hour, minute).toString("kk:mm"));
    }

    public void onEdit() {

        String name = mNameTextView.getText().toString();

        if (name != null && name.length() > 0) {


            // if editing
            if (mRoutine != null) {
                mRoutine.setName(name);
                mRoutine.setTime(new LocalTime(hour, minute));
                DB.routines().saveAndFireEvent(mRoutine);
                //mRoutine.save();
                if (mRoutineEditCallback != null) {
                    mRoutineEditCallback.onRoutineEdited(mRoutine);
                }
            }
            // if creating
            else {
                mRoutine = new Routine(new LocalTime(hour, minute), name);
                mRoutine.setUser(DB.users().getActive(getActivity()));
                Log.d(getTag(), "Routine created");
                DB.routines().saveAndFireEvent(mRoutine);
                if (mRoutineEditCallback != null) {
                    mRoutineEditCallback.onRoutineCreated(mRoutine);
                }
            }
        } else {
            Snack.show("please type a name", getActivity());
        }
    }


    public void showDeleteConfirmationDialog(final Routine r) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String message;

        if (r.scheduleItems().size() > 0) {
            message = String.format(getString(R.string.remove_routine_message_long), r.name());
        } else {
            message = String.format(getString(R.string.remove_routine_message_short), r.name());
        }

        builder.setMessage(message)
                .setCancelable(true)
                .setTitle(getString(R.string.remove_routine_dialog_title))
                .setPositiveButton(getString(R.string.dialog_yes_option), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (mRoutineEditCallback != null) {
                            mRoutineEditCallback.onRoutineDeleted(mRoutine);
                        }
                    }
                })
                .setNegativeButton(getString(R.string.dialog_no_option), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Log.d(getTag(), "Activity " + activity.getClass().getName() + ", " + (activity instanceof OnRoutineEditListener));
        // If the container activity has implemented
        // the callback interface, set it as listener
        if (activity instanceof OnRoutineEditListener) {
            Log.d(getTag(), "Set onRoutineEditListener onAttach");
            mRoutineEditCallback = (OnRoutineEditListener) activity;
        }
    }

    // optionally set the listener manually
    public void setOnRoutineEditListener(OnRoutineEditListener l) {
        mRoutineEditCallback = l;
    }

    @Override
    public void onTimeSet(RadialTimePickerDialog dialog, int hourOfDay, int minute) {
        this.hour = hourOfDay;
        this.minute = minute;
        updateTime();
    }

    @Override
    public void onDialogTimeSet(int ref, int hour, int minute) {
        onTimeSet(null, hour, minute);
    }

    // Container Activity must implement this interface
    public interface OnRoutineEditListener {
        void onRoutineEdited(Routine r);

        void onRoutineCreated(Routine r);

        void onRoutineDeleted(Routine r);
    }

}