package com.saveethataskdoor.app.attendance;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.HashMap;

/**
 * Created on 11/3/19.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    int count = 0;
    HashMap<Integer, Boolean> attendanceMap = new HashMap<>();

    public ViewPagerAdapter(FragmentManager fm, int count, HashMap<Integer, Boolean> attendanceMap) {
        super(fm);
        this.count = count;
        this.attendanceMap = attendanceMap;
    }

    @Override
    public Fragment getItem(int position) {
        int rollNumber = position + 1;
        if (attendanceMap.containsKey(rollNumber))
            return AttendanceFragment.newInstnce(rollNumber, attendanceMap.get(rollNumber), true);
        else
            return AttendanceFragment.newInstnce(rollNumber, false, false);
    }

    @Override
    public int getCount() {
        return count;
    }
}
