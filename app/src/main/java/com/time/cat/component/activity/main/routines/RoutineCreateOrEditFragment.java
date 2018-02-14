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
import com.time.cat.mvp.model.DBmodel.DBRoutine;
import com.time.cat.util.Snack;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

public class RoutineCreateOrEditFragment extends DialogFragment implements RadialTimePickerDialog.OnTimeSetListener, TimePickerDialogFragment.TimePickerDialogHandler {

    OnRoutineEditListener mRoutineEditCallback;
    DBRoutine mDBRoutine;

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

        mNameTextView = rootView.findViewById(R.id.routine_edit_name);
        timeButton = rootView.findViewById(R.id.button2);

        timeButton.setTextColor(pColor);

        long routineId = -1;

        if (getArguments() != null) {
            routineId = getArguments().getLong(TimeCatApp.INTENT_EXTRA_ROUTINE_ID, -1);
        }

        if (routineId == -1 && savedInstanceState != null) {
            routineId = savedInstanceState.getLong(TimeCatApp.INTENT_EXTRA_ROUTINE_ID, -1);
        }

        if (routineId != -1) {
            mDBRoutine = DBRoutine.findById(routineId);
            setRoutine(mDBRoutine);
            hour = mDBRoutine.time().getHourOfDay();
            minute = mDBRoutine.time().getMinuteOfHour();
        } else {
            DateTime now = DateTime.now();
            hour = now.getHourOfDay();
            minute = now.getMinuteOfHour();
        }

        if (getDialog() != null) {
            getDialog().setTitle("Create routine");
            mConfirmButton = rootView.findViewById(R.id.done_button);
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
                    RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog.newInstance(RoutineCreateOrEditFragment.this, hour, minute, true);
                    timePickerDialog.show(getChildFragmentManager(), "111");
                } else {
                    TimePickerBuilder tpb = new TimePickerBuilder().setFragmentManager(getChildFragmentManager()).setStyleResId(R.style.BetterPickersDialogFragment_Light);
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
        if (mDBRoutine != null)
            outState.putLong(TimeCatApp.INTENT_EXTRA_ROUTINE_ID, mDBRoutine.getId());
    }

    private void setRoutine(DBRoutine r) {
        Log.d(getTag(), "DBRoutine set: " + r.name());
        mDBRoutine = r;
        mNameTextView.setText(mDBRoutine.name());
        updateTime();
    }


    void updateTime() {
        timeButton.setText(new LocalTime(hour, minute).toString("kk:mm"));
    }

    public void onEdit() {

        String name = mNameTextView.getText().toString();

        if (name != null && name.length() > 0) {


            // if editing
            if (mDBRoutine != null) {
                mDBRoutine.setName(name);
                mDBRoutine.setTime(new LocalTime(hour, minute));
                DB.routines().saveAndFireEvent(mDBRoutine);
                //mDBRoutine.save();
                if (mRoutineEditCallback != null) {
                    mRoutineEditCallback.onRoutineEdited(mDBRoutine);
                }
            }
            // if creating
            else {
                mDBRoutine = new DBRoutine(new LocalTime(hour, minute), name);
                mDBRoutine.setUser(DB.users().getActive(getActivity()));
                Log.d(getTag(), "DBRoutine created");
                DB.routines().saveAndFireEvent(mDBRoutine);
                if (mRoutineEditCallback != null) {
                    mRoutineEditCallback.onRoutineCreated(mDBRoutine);
                }
            }
        } else {
            Snack.show("please type a name", getActivity());
        }
    }


    public void showDeleteConfirmationDialog(final DBRoutine r) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String message;

        if (r.scheduleItems().size() > 0) {
            message = String.format(getString(R.string.remove_routine_message_long), r.name());
        } else {
            message = String.format(getString(R.string.remove_routine_message_short), r.name());
        }

        builder.setMessage(message).setCancelable(true).setTitle(getString(R.string.remove_routine_dialog_title)).setPositiveButton(getString(R.string.dialog_yes_option), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (mRoutineEditCallback != null) {
                    mRoutineEditCallback.onRoutineDeleted(mDBRoutine);
                }
            }
        }).setNegativeButton(getString(R.string.dialog_no_option), new DialogInterface.OnClickListener() {
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
        void onRoutineEdited(DBRoutine r);

        void onRoutineCreated(DBRoutine r);

        void onRoutineDeleted(DBRoutine r);
    }

}