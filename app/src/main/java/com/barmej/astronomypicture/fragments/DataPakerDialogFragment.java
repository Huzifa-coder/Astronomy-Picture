package com.barmej.astronomypicture.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.barmej.astronomypicture.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DataPakerDialogFragment extends DialogFragment {

    private ButtonSheetListener okButtonSheetListener;

    private CalendarView mCalendarView;
    private Button okBtn, cancelBtn;

    private String selectedDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.data_paker_dialog_fragment, container, false);

        mCalendarView = mView.findViewById(R.id.calendar_date_paker);
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, day);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.YEAR, year);
                SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                selectedDate = df2.format(calendar.getTimeInMillis());

            }
        });

        okBtn = mView.findViewById(R.id.ok_btn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                okButtonSheetListener.onButtonCilcked(selectedDate);
                dismiss();
            }
        });

        cancelBtn = mView.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return mView;
    }//end of onCreateView

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            okButtonSheetListener = (ButtonSheetListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement ButtonSheetListener");
        }

    }//end of onAttach

    public interface ButtonSheetListener{
        void onButtonCilcked(String date);
    }//end of ButtonSheetListener

}//end of DataPakerDialogFragment