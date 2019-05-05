package com.cit17b.studays.note;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cit17b.studays.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NotificationDialogFragment extends DialogFragment implements DialogInterface.OnClickListener, View.OnClickListener {

    public interface NotificationDialogListener {
        void onFinishNotificationDialog(long input);
    }

    private Calendar selectedDateTime;

    View notificationDialogContent;
    CheckBox enableNotificationCheckBox;
    TextView dateView;
    TextView timeView;

    public NotificationDialogFragment() {
    }

    public static NotificationDialogFragment newInstance(String title, long dateTime) {
        NotificationDialogFragment fragment = new NotificationDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putLong("dateTime", dateTime);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        notificationDialogContent = getActivity().getLayoutInflater().inflate(R.layout.notification_dialog, null);
        enableNotificationCheckBox = notificationDialogContent.findViewById(R.id.enableNotificationCheckBox);
        enableNotificationCheckBox.setOnClickListener(this);
        dateView = notificationDialogContent.findViewById(R.id.notificationDialogDateView);
        dateView.setOnClickListener(this);
        timeView = notificationDialogContent.findViewById(R.id.notificationDialogTimeView);
        timeView.setOnClickListener(this);

        long dateTime = getArguments().getLong("dateTime", 0);
        selectedDateTime = Calendar.getInstance();
        if (dateTime != 0) {
            selectedDateTime.setTimeInMillis(dateTime);
            enableNotificationCheckBox.setChecked(true);
            dateView.setEnabled(true);
            timeView.setEnabled(true);
        } else {
            selectedDateTime.set(Calendar.SECOND, 0);
            selectedDateTime.add(Calendar.HOUR, 1);
            enableNotificationCheckBox.setChecked(false);
            dateView.setEnabled(false);
            timeView.setEnabled(false);
        }

        dateView.setText(new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(selectedDateTime.getTime()));
        timeView.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(selectedDateTime.getTime()));
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setView(notificationDialogContent);
        builder.setPositiveButton(R.string.ok, this);
        builder.setNegativeButton(R.string.cancel, this);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                if (enableNotificationCheckBox.isChecked()) {
                    ((NotificationDialogListener) getContext()).onFinishNotificationDialog(selectedDateTime.getTimeInMillis());
                } else {
                    ((NotificationDialogListener) getContext()).onFinishNotificationDialog(0);
                }
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.enableNotificationCheckBox:
                if (((CheckBox) v).isChecked()) {
                    dateView.setEnabled(true);
                    timeView.setEnabled(true);
                } else {
                    dateView.setEnabled(false);
                    timeView.setEnabled(false);
                }
                break;
            case R.id.notificationDialogDateView:
                new DatePickerDialog(
                        getActivity(),
                        R.style.TimePickerTheme,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                selectedDateTime.set(year, month, dayOfMonth);
                                dateView.setText(new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(selectedDateTime.getTime()));
                            }
                        },
                        selectedDateTime.get(Calendar.YEAR),
                        selectedDateTime.get(Calendar.MONTH),
                        selectedDateTime.get(Calendar.DATE)).show();
                break;
            case R.id.notificationDialogTimeView:
                new TimePickerDialog(
                        getActivity(),
                        R.style.TimePickerTheme,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selectedDateTime.set(Calendar.MINUTE, minute);
                                timeView.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(selectedDateTime.getTime()));
                            }
                        },
                        selectedDateTime.get(Calendar.HOUR_OF_DAY),
                        selectedDateTime.get(Calendar.MINUTE),
                        true).show();
                break;
        }
    }
}
