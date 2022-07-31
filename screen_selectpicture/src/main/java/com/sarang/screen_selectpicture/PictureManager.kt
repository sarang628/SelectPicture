package com.sarang.screen_selectpicture

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.torang_core.data.model.MyImage
import java.util.*
import kotlin.collections.ArrayList

class PictureManager {
    @RequiresApi(api = Build.VERSION_CODES.M)
    fun checkPermission(activity: AppCompatActivity): Boolean {
        //권한 체크 하기
        val isPermission =
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
        return if (isPermission == PackageManager.PERMISSION_DENIED) {
            val b = AlertDialog.Builder(activity)
            b.setMessage("이미지를 등록하기위해선 저장소 읽기 권한이 필요합니다. 허용하시겠습니까?")
            b.setPositiveButton("yes") { dialogInterface: DialogInterface?, i: Int ->
                activity.requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ), 0x01
                )
            }
            b.setNegativeButton("no") { dialogInterface: DialogInterface?, i: Int -> activity.finish() }
            b.show()
            false
        } else {
            true
        }
    }

    fun requestPicFolderList(act: Activity): ArrayList<String> {
        val appCompatActivity = act as AppCompatActivity
        val context: Context = act
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkPermission(appCompatActivity)) {
            return ArrayList()
        }
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DESCRIPTION,
            MediaStore.Images.Media.PICASA_ID,
            MediaStore.Images.Media.IS_PRIVATE,
            MediaStore.Images.Media.LATITUDE,
            MediaStore.Images.Media.LONGITUDE,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.ORIENTATION,
            MediaStore.Images.Media.MINI_THUMB_MAGIC,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATE_ADDED
        )
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        val folderMap: MutableMap<String, MyImage?> = TreeMap()
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                val filePath = cursor.getString(10)
                val splited = cursor.getString(10).split("/".toRegex()).toTypedArray()
                val folder = filePath.replace(splited[splited.size - 1], "")
                val myImage = MyImage()
                myImage.data = folder
                if (folderMap[folder] == null) folderMap[folder] = myImage
            }
            cursor.close()
        }
        val myImages = ArrayList(folderMap.values)
        val folderList = ArrayList<String>()
        folderList.add(0, "전체")
        for (myImage in myImages) {
            myImage!!.data?.let { folderList.add(it) }
        }
        return folderList
    }

    fun getPicList(context: Context, forderName: String?): ArrayList<MyImage> {
        var forderName = forderName
        if (forderName != null && forderName == "전체") forderName = null
        var cursor: Cursor? = null
        var uri: Uri? = null
        val projection = arrayOfNulls<String>(1)
        var selection: String? = null
        val selectionArgs = arrayOfNulls<String>(1)
        val sortOrder = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"
        val myImageArrayList = ArrayList<MyImage>()
        try {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            if (forderName != null) {
                projection[0] = MediaStore.MediaColumns.DATA
                selection = MediaStore.Images.ImageColumns.DATA + " LIKE ?"
                selectionArgs[0] = "%$forderName%"
                cursor = context.contentResolver.query(
                    uri,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
                )
            } else {
                cursor = context.contentResolver.query(uri, null, null, null, sortOrder)
            }
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
                    val myImage = MyImage()
                    myImage.data = cursor.getString(columnIndex)
                    myImageArrayList.add(myImage)
                }
                cursor.close()
            }
        } catch (e: Exception) {
            Log.e("sr", e.toString())
        }
        cursor?.close()
        return myImageArrayList
    }

    companion object {
        lateinit var pictureManager: PictureManager
        val instance: PictureManager
            get() {
                if (pictureManager == null) pictureManager = PictureManager()
                return pictureManager
            }
    }
}