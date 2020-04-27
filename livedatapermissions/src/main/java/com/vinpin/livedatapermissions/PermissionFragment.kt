package com.vinpin.livedatapermissions

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * author : vinpin
 * e-mail : hearzwp@163.com
 * time   : 2020/4/26 15:44
 * desc   : 实际处理动态权限申请的Fragment
 */
class PermissionFragment : Fragment() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1000
    }

    private var mLiveData: MutableLiveData<PermissionResult>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val denys = arrayListOf<String>()
            val rationales = arrayListOf<String>()
            grantResults.withIndex().forEach {
                val permission = permissions[it.index]
                if (it.value == PackageManager.PERMISSION_DENIED) {
                    if (shouldShowRequestPermissionRationale(permission)) rationales.add(permission) else denys.add(permission)
                }
            }
            if (denys.isEmpty() && rationales.isEmpty()) {
                mLiveData?.value = PermissionGrant
            } else {
                mLiveData?.value = PermissionDeny(denys.toTypedArray(), rationales.toTypedArray())
            }
        }
    }

    /**
     * 请求授予此应用程序的权限。
     */
    fun request(permissions: Array<out String>): LiveData<PermissionResult> {
        mLiveData = MutableLiveData()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissions.size == 1) {
                if (ContextCompat.checkSelfPermission(requireContext(), permissions[0]) != PackageManager.PERMISSION_GRANTED)
                    requestPermissions(permissions, PERMISSION_REQUEST_CODE) else mLiveData?.postValue(PermissionGrant)
            } else {
                requestPermissions(permissions, PERMISSION_REQUEST_CODE)
            }
        } else {
            mLiveData?.postValue(PermissionGrant)
        }
        return mLiveData!!
    }
}