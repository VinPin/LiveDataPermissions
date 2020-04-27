package com.vinpin.livedatapermissions

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * author : vinpin
 * e-mail : hearzwp@163.com
 * time   : 2020/4/26 15:41
 * desc   : 利用LiveData实现动态权限申请
 */
@Suppress("unused")
class LiveDataPermissions private constructor() {

    @Volatile
    private var mPermissionFragment: PermissionFragment? = null

    companion object {
        const val TAG = "PermissionFragment"
    }

    constructor(activity: AppCompatActivity) : this() {
        mPermissionFragment = createFragmentIfAbsent(activity.supportFragmentManager)
    }

    constructor(fragment: Fragment) : this() {
        mPermissionFragment = createFragmentIfAbsent(fragment.childFragmentManager)
    }

    private fun createFragmentIfAbsent(fragmentManager: FragmentManager): PermissionFragment {
        return mPermissionFragment ?: synchronized(this) {
            mPermissionFragment ?: if (fragmentManager.findFragmentByTag(TAG) == null) PermissionFragment().apply {
                fragmentManager.beginTransaction().add(this, TAG).commitNow()
            } else fragmentManager.findFragmentByTag(TAG) as PermissionFragment
        }
    }

    /**
     * 请求授予此应用程序的权限。
     */
    fun request(vararg permissions: String) = mPermissionFragment!!.request(permissions)
}