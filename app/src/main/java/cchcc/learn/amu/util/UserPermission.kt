package cchcc.learn.amu.util

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.util.SparseArray
import java.util.*

class UserPermission(private val activity: Activity, vararg permissions: String) {
    val perms: Array<out String> by lazy { permissions }

    lateinit var isGrantedCallback: () -> Unit
    lateinit var notGrantedCallback: () -> Unit

    /**
     *  1. check if permission is granted
     *  2. if permission is granted, callback `isGranted`, or request permission
     *  3. callback `isGranted` or `notGranted` as a result of requesting
     */
    fun checkOrRequest(isGranted: () -> Unit, notGranted: () -> Unit) {
        val notGrantedPermissions = perms.filter { ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED }
                .toTypedArray()

        val permissionIsAllGranted = notGrantedPermissions.isEmpty()
        if (permissionIsAllGranted) {
            isGranted()
        } else {
            if(shouldShowRequestRational()) {
                activity.showAlert()
            } else {
                isGrantedCallback = isGranted
                notGrantedCallback = notGranted
                val requestCode = Math.abs((activity.hashCode() + isGranted.hashCode()).toShort().toInt())
                activity.permissions.put(requestCode, this)
                ActivityCompat.requestPermissions(activity, notGrantedPermissions, requestCode)
            }
        }
    }

    fun shouldShowRequestRational(): Boolean = perms.all { ActivityCompat.shouldShowRequestPermissionRationale(activity, it) }

    override fun toString(): String = "${javaClass.simpleName} ${Arrays.toString(perms)}"
}

private val Activity.permissions by lazy { SparseArray<UserPermission>() }

fun Activity.permissionOf(vararg permissions: String): UserPermission = UserPermission(this, *permissions)


/**
 *  this has to be called in Activity.onRequestPermissionsResult
 */
fun Activity.requestPermissionResult(requestCode: Int, grantResults: IntArray) = permissions[requestCode]?.let {
    permissions.remove(requestCode)
    val isGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
    if (isGranted) it.isGrantedCallback() else it.notGrantedCallback()
}
