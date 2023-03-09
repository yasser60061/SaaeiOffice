package com.saaei12092021.office.util

import android.Manifest
import androidx.core.content.ContextCompat
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.saaei12092021.office.util.Constant.CAMERA_REQUEST_CODE
import com.saaei12092021.office.util.Constant.CONTACTS_REQUEST_CODE
import com.saaei12092021.office.util.Constant.RECORDING_REQUEST_CODE
import com.saaei12092021.office.util.Constant.STORAGE_REQUEST_CODE

class Permissions {
    fun isStoragePermissionAllow(context: Context?): Boolean {
        return ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isCameraPermissionsAllow(context: Context?): Boolean {
        return ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestStoragePermissions(activity: Activity?) {
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            STORAGE_REQUEST_CODE
        )
    }

    fun requestCameraPermissions(activity: Activity?) {
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            CAMERA_REQUEST_CODE
        )
    }

    fun isContactOk(context: Context?): Boolean {
        return ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestContact(activity: Activity?) {
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(Manifest.permission.READ_CONTACTS),
            CONTACTS_REQUEST_CODE
        )
    }

    fun isRecordingOk(context: Context?): Boolean {
        return ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestRecording(activity: Activity?) {
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            RECORDING_REQUEST_CODE
        )
    }
}