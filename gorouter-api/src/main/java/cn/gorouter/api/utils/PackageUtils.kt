package cn.gorouter.api.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import cn.gorouter.api.logger.GoLogger
import cn.gorouter.api.utils.Const.*

object PackageUtils {


    private var NEW_VERSION_NAME: String?= null
    private var NEW_VERSION_CODE: Int?= null

    @JvmStatic
    fun isNewVersion(context: Context): Boolean {
        val packageInfo = getPackageInfo(context)
        if(packageInfo != null){
            val versionName = packageInfo.versionName
            val versionCode = packageInfo.versionCode

            val sp = context.getSharedPreferences(GOROUTER_SP_CACHE_KEY, MODE_PRIVATE)
            if(versionName != sp.getString(LAST_VERSION_NAME, null) || versionCode != sp.getInt(LAST_VERSION_CODE, -1)){
                // new version
                NEW_VERSION_NAME = versionName
                NEW_VERSION_CODE = versionCode

                return true
            }else{
                return false
            }
        }else{
            return true
        }
    }

    private fun getPackageInfo(context: Context): PackageInfo? {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = context.packageManager?.getPackageInfo(context.packageName, PackageManager.GET_CONFIGURATIONS)
        }catch (ex: Exception){
            GoLogger.error("Get package info error.")
        }
        return packageInfo
    }

    @JvmStatic
    fun updateVersion(context: Context) {
        if(!android.text.TextUtils.isEmpty(NEW_VERSION_NAME) && NEW_VERSION_CODE != 0){
            val sp = context.getSharedPreferences(GOROUTER_SP_CACHE_KEY, MODE_PRIVATE)
            sp.edit().putString(LAST_VERSION_NAME, NEW_VERSION_NAME).putInt(LAST_VERSION_CODE, NEW_VERSION_CODE!!).apply()
        }
    }
}