package com.qxtao.viewanalysis.utils.common

import android.content.Context
import androidx.preference.PreferenceManager

object ShareUtils {
    fun putString(mContext: Context?, key: String?, value: String?) {
        val sp = PreferenceManager.getDefaultSharedPreferences(
            mContext!!
        )
        sp.edit().putString(key, value).apply()
    }

    //键 默认值
    fun getString(mContext: Context?, key: String?, defValue: String?): String? {
        val sp = PreferenceManager.getDefaultSharedPreferences(
            mContext!!
        )
        return sp.getString(key, defValue)
    }

    //键 值
    fun putInt(mContext: Context?, key: String?, value: Int) {
        val sp = PreferenceManager.getDefaultSharedPreferences(
            mContext!!
        )
        sp.edit().putInt(key, value).apply()
    }

    //键 默认值
    fun getInt(mContext: Context?, key: String?, defValue: Int): Int {
        val sp = PreferenceManager.getDefaultSharedPreferences(
            mContext!!
        )
        return sp.getInt(key, defValue)
    }

    //键 值
    fun putBoolean(mContext: Context?, key: String?, value: Boolean) {
        val sp = PreferenceManager.getDefaultSharedPreferences(
            mContext!!
        )
        sp.edit().putBoolean(key, value).apply()
    }

    //键 默认值
    fun getBoolean(mContext: Context?, key: String?, defValue: Boolean): Boolean {
        val sp = PreferenceManager.getDefaultSharedPreferences(
            mContext!!
        )
        return sp.getBoolean(key, defValue)
    }

    //删除 单个
    fun delShare(mContext: Context?, key: String?) {
        val sp = PreferenceManager.getDefaultSharedPreferences(
            mContext!!
        )
        sp.edit().remove(key).apply()
    }

    //删除 全部
    fun delAll(mContext: Context?) {
        val sp = PreferenceManager.getDefaultSharedPreferences(
            mContext!!
        )
        sp.edit().clear().apply()
    }
}