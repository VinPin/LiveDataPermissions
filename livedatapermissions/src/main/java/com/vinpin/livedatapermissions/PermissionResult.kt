package com.vinpin.livedatapermissions

/**
 * author : vinpin
 * e-mail : hearzwp@163.com
 * time   : 2020/4/26 15:46
 * desc   : 动态权限检查结果
 */
sealed class PermissionResult

/**
 * 如果已将权限授予给指定的包，则返回该结果。
 */
object PermissionGrant : PermissionResult()

/**
 * 如果未授予给定包许可，则返回该结果。[deny] 勾选了不再询问，[rationale] 沒有勾选不再询问。
 */
class PermissionDeny(val deny: Array<String>, val rationale: Array<String>) : PermissionResult()