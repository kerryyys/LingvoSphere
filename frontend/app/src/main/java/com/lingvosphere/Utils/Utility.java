package com.lingvosphere.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.lingvosphere.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Utility {
    private static final String PREF_NAME = "LingvoSpherePreferences";

    public static void setPreference(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getPreference(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    public static int getFlag(String name) {
        if(name.contains("Chinese"))
            return R.drawable.flag_cn;
        if(name.contains("Japanese"))
            return R.drawable.flag_jp;
        if(name.contains("English"))
            return R.drawable.flag_uk;
        if(name.contains("Korean"))
            return R.drawable.flag_korea;
        return R.drawable.flag_my;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static long LocalDate2Long(LocalDate localDate) {
        LocalDateTime localDateTime = localDate.atStartOfDay(); // 转换为当天的开始时间
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault()); // 将 LocalDateTime 转换为 ZonedDateTime
        Instant instant = zonedDateTime.toInstant(); // 转换为 Instant
        return  instant.toEpochMilli(); // 获取自 Unix 纪元以来的毫秒数
    }
}
