package com.saveethataskdoor.app.attendance;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.saveethataskdoor.app.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class AttendanceFragment extends Fragment {

    int rollNumber = -1;

    public AttendanceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rollNumber = getArguments().getInt("rollNumber");
    }

    public static Fragment newInstnce(int rollNumber, boolean isPresent, Boolean isNotAvailable) {
        AttendanceFragment attendanceFragment = new AttendanceFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("rollNumber", rollNumber);
        bundle.putBoolean("isPresent", isPresent);
        bundle.putBoolean("isNotAvailable", isNotAvailable);
        attendanceFragment.setArguments(bundle);
        return attendanceFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_attendance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments().getBoolean("isNotAvailable")) {
            if (getArguments().getBoolean("isPresent"))
                ((RadioButton) view.findViewById(R.id.rb_present)).setChecked(true);
            else
                ((RadioButton) view.findViewById(R.id.rb_absent)).setChecked(true);
        } else {
            ((RadioButton) view.findViewById(R.id.rb_absent)).setChecked(false);
            ((RadioButton) view.findViewById(R.id.rb_present)).setChecked(false);
        }

        ((RadioGroup) view.findViewById(R.id.radioGroup)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.rb_present) {
                    onAttendanceListener.onAttendanceClicked(true);
                } else if (checkedId == R.id.rb_absent) {
                    onAttendanceListener.onAttendanceClicked(false);
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttendanceListener = (OnAttendanceListener) context;
    }

    @Override
    public void onDetach() {
        onAttendanceListener = null;
        super.onDetach();
    }

    OnAttendanceListener onAttendanceListener;

    public interface OnAttendanceListener {

        public void onAttendanceClicked(boolean isPresent);
    }
}
