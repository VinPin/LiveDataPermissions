# LiveDataPermissions
基于LiveData实现的动态权限申请库

## 如何使用
```java
LiveDataPermissions(this).request(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
        ).observe(this, Observer {
            when (it) {
                is PermissionGrant -> {
                    Toast.makeText(this, "同意", Toast.LENGTH_LONG).show()
                }
                is PermissionDeny -> {
                    if (it.deny.isNotEmpty()) {
                        Toast.makeText(this, "拒绝下次不再提醒：${it.deny.size}", Toast.LENGTH_LONG).show()
                    } else if (it.rationale.isNotEmpty()) {
                        Toast.makeText(this, "拒绝下次提醒：${it.rationale.size}", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        })
```
LiveDataPermissions构造函数支持Activity和Fragment。

## 实现原理
#### PermissionFragment 
实际处理动态权限申请，会被注入到Activity或Fragment中，观察并获取到onRequestPermissionsResult()回调函数。

##### 请求授予此应用程序的权限

```java
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

```
返回一个LiveData，可以利用观察这个LiveData获取请求授予权限的返回结果。

##### 响应请求授予权限回调结果

```java
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
```
根据返回的结果给LiveData设置对应的值。

#### LiveDataPermissions
主要是一个封装类，也是对外使用的一个入口。主要的作用是将PermissionFragment注入到Activity或Fragment中，并提供与PermissionFragment一致的请求授予此应用程序的权限接口。

```java
fun request(vararg permissions: String) = mPermissionFragment!!.request(permissions)
```

#### PermissionResult
请求授权结果类，是个密封类。

```
/**
 * 如果已将权限授予给指定的包，则返回该结果。
 */
object PermissionGrant : PermissionResult()

/**
 * 如果未授予给定包许可，则返回该结果。[deny] 勾选了不再询问，[rationale] 沒有勾选不再询问。
 */
class PermissionDeny(val deny: Array<String>, val rationale: Array<String>) : PermissionResult()
```
