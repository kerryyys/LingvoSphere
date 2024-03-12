package com.lingvosphere.Adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.lingvosphere.LearningTimelineActivity;
import com.lingvosphere.R;
import com.lingvosphere.Utils.HttpUtility;
import com.lingvosphere.Utils.Utility;
import com.lingvosphere.model.CalendarItem;
import com.lingvosphere.model.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private CalendarItem[] mCalendarData; // Replace String with your data model

    public CalendarAdapter(CalendarItem[] calendarData) {
        mCalendarData = calendarData;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_item, parent, false);
        return new ViewHolder(view);
    }

    public void setType(int type, int idx) {
        for(int i = 7;i < 49;i ++) {
            if((mCalendarData[i].getType()&CalendarItem.TYPE_IN_MONTH)!=0 && mCalendarData[i].getDate().equals(String.valueOf(idx))) {
                mCalendarData[i].setType(type);
                notifyDataSetChanged();
                return;
            }
        }

    }

    public int getType(int idx) {
        for(int i = 7;i < 49;i ++) {
            if((mCalendarData[i].getType() & CalendarItem.TYPE_IN_MONTH)!=0 && mCalendarData[i].getDate().equals(String.valueOf(idx))) {
                return mCalendarData[i].getType();
            }
        }

        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = mCalendarData[position].getDate();
        int type = mCalendarData[position].getType();
        holder.calendarItemNumber.setText(item);

        if ((type & CalendarItem.TYPE_IN_MONTH) != 0)
            holder.calendarItemNumber.setTextColor(Color.parseColor("#878787"));
        else
            holder.calendarItemNumber.setTextColor(Color.parseColor("#c3c3c3"));
        if ((type & CalendarItem.TYPE_SELECTED) == 0)
            holder.calendar_item_indicator.setVisibility(View.GONE);
        if((type & CalendarItem.TYPE_FOCUSED) != 0) {
            holder.calendar_item_indicator.setBackgroundResource(R.drawable.white_spot);
            holder.calendarItemNumber.setTextColor(Color.parseColor("#ffffff"));
        } else
            holder.calendarItemNumber.setBackgroundColor(Color.parseColor("#ffffff"));
        // 转换为秒
        holder.calendarItemNumber.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                //Intent it = new Intent(view.getContext(), AddEventActivity.class);
                //view.getContext().startActivity(it);
                LocalDate today = LocalDate.now().minusMonths(-LearningTimelineActivity.offset).withDayOfMonth(Integer.parseInt(mCalendarData[position].getDate()));
                for(int i = 7;i < 49;i ++)
                    if((mCalendarData[i].getType() | CalendarItem.TYPE_IN_MONTH) != 0) {
                        if (i != position)
                            mCalendarData[i].setType(mCalendarData[i].getType() & (CalendarItem.TYPE_IN_MONTH | CalendarItem.TYPE_SELECTED));
                        else {
                            if((mCalendarData[i].getType() & CalendarItem.TYPE_IN_MONTH) == 0)
                                return;
                            mCalendarData[i].setType(mCalendarData[i].getType() | CalendarItem.TYPE_FOCUSED);

                            LearningTimelineActivity.updateTasks(today.minusDays(today.getDayOfMonth() - Integer.parseInt(mCalendarData[i].getDate())));
                        }
                    }
                LearningTimelineActivity.selected = Integer.parseInt(mCalendarData[position].getDate());
                LearningTimelineActivity.showTasks(LearningTimelineActivity.selected);
                LearningTimelineActivity.show_bookings(LearningTimelineActivity.selected);
                notifyDataSetChanged();
            }
        });
    }

    public void setmCalendarData(CalendarItem[] data) {
        this.mCalendarData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mCalendarData.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView calendarItemNumber;
        public LinearLayout calendar_item_indicator;

        private boolean initialized = false;
        public ViewHolder(View itemView) {
            super(itemView);
            calendarItemNumber = itemView.findViewById(R.id.calendar_item_number);
            calendar_item_indicator = itemView.findViewById(R.id.calendar_item_indicator);
            // Initialize other views in your calendar item here
        }
    }
}