package com.sisucon.loopdaily.Fragment

import android.app.Activity.NOTIFICATION_SERVICE
import android.app.Activity.RESULT_OK
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.*
import android.os.StrictMode.VmPolicy
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.core.os.EnvironmentCompat
import androidx.fragment.app.Fragment
import butterknife.Unbinder
import com.donkingliang.imageselector.utils.VersionUtils
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.sisucon.loopdaily.Activity.LoginActivity
import com.sisucon.loopdaily.Model.Userkey
import com.sisucon.loopdaily.R
import com.sisucon.loopdaily.Util.NetUtil
import com.sisucon.loopdaily.Util.ServerUserModel
import com.sisucon.loopdaily.lib.CircleImageView
import com.sisucon.loopdaily.lib.UriToPath
import com.sisucon.loopdaily.lib.UriToPath.*
import es.dmoral.toasty.Toasty
import org.litepal.LitePal
import java.io.File
import java.io.IOException
import java.util.*

class MenuFragment : Fragment() {
    lateinit var unbinder : Unbinder
    private val CODE_GALLERY_REQUEST = 0xa0 //本地
    private var cropImageUri: Uri? = null
    private val CROP_PICTURE = 2 //裁剪后图片返回码
    lateinit var userAvator:CircleImageView
     var vNavigation:NavigationView? = null
     lateinit var username_menu:TextView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.menu_fragment,container,false)
        StrictMethod()
        vNavigation = rootView?.findViewById(R.id.leftView) as NavigationView?
        initView()
        return rootView
    }





    fun initView(){
        userAvator = vNavigation?.getHeaderView(0)!!.findViewById(R.id.left_view_userimg)
        userAvator.setOnClickListener {
            choseHeadImageFromGallery()
        }
        username_menu = vNavigation!!.getHeaderView(0).findViewById(R.id.main_menu_username)
        vNavigation!!.setNavigationItemSelectedListener {
            logout()
            true
        }
        updateUserInfoToView()
    }

    private fun logout(){
        LitePal.deleteAll(Userkey::class.java)
        activity!!.finish()
        startActivity(Intent(activity,LoginActivity::class.java))
    }


    private fun updateUserInfoToView() = Thread(Runnable {
        val severUserModel = Gson().fromJson<ServerUserModel>(NetUtil.GetMessage(getString(R.string.server_host)+"/user/myInfo"), ServerUserModel::class.java)
        Handler(context!!.mainLooper).post(Runnable {
            username_menu.text = severUserModel.userName
            userAvator.setImageURL(getString(R.string.server_host_file)+"/upload/avator/"+severUserModel.userName+"/"+severUserModel.avatorFileName)
        })
    }).start()

//    private val mPics: MutableList<PhotoBean> = mutableListOf<PhotoBean>()
//    private fun initData() {
//        mPics.clear()
//        val contentResolver: ContentResolver = getContentResolver()
//        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//        val query: Cursor? = contentResolver.query(
//            uri, arrayOf(
//                MediaStore.Images.Media.DATA,
//                MediaStore.Images.Media.DISPLAY_NAME,
//                MediaStore.Images.Media.DATE_ADDED,
//                MediaStore.Images.Media._ID
//            ), null, null, null, null
//        )
//        while (query!!.moveToNext()) {
//            val photoItem = PhotoBean()
//            photoItem.setPath(query.getString(0))
//            //这里的下标跟上面的query第一个参数对应，时间是第2个，所以下标为1
//            photoItem.setCreateDate(query.getLong(1))
//            photoItem.setName(query.getString(2))
//            photoItem.setID(query.getInt(query.getColumnIndex(MediaStore.MediaColumns._ID)))
//            mPics.add(photoItem)
//        }
//        query.close()
//    }

    private val isAndroidQ =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    /***
     * 调用相册获取头像
     */
    private fun choseHeadImageFromGallery() {
        val intentFromGallery = Intent()
        // 设置文件类型
        // 设置文件类型
        intentFromGallery.type = "image/*" //选择图片

        intentFromGallery.action = Intent.ACTION_OPEN_DOCUMENT
        //如果你想在Activity中得到新打开Activity关闭后返回的数据，
        //你需要使用系统提供的startActivityForResult(Intent intent,int requestCode)方法打开新的Activity
        //如果你想在Activity中得到新打开Activity关闭后返回的数据，
        //你需要使用系统提供的startActivityForResult(Intent intent,int requestCode)方法打开新的Activity
        startActivityForResult(
            intentFromGallery, CODE_GALLERY_REQUEST
        )
//        ImageSelector.builder()
//            .useCamera(false)
//            .setSingle(true)
//            .setMaxSelectCount(1)
//            .setCrop(true)
//            .start(this,CODE_GALLERY_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            CODE_GALLERY_REQUEST -> {
                if (resultCode==RESULT_OK&&data!=null){
                    VersionUtils.isAndroidQ()
                    val originaUri = data.data
                   startPhotoZoom(originaUri)
                }
            }
            CROP_PICTURE -> {
                if (cropImageUri!=null)
                {
                    println(cropImageUri!!.path)
                    var file = File(cropImageUri!!.path)
                    println()
                    println(getPath(activity!!,cropImageUri))
                    println(file.name)
                    println(file.absolutePath)
                    println("URI "+UriToPath.getFilePathFromURI(activity,cropImageUri))
                    Thread(Runnable {
                        val result =  NetUtil.PostFile(getString(R.string.server_host)+"/user/uploadAvator",File(getRealPathFromUriAboveApi19(activity!!,cropImageUri)))
                        activity!!.runOnUiThread {
                            Toasty.info(activity!!,""+result).show()
                            updateUserInfoToView()
                        }
                    }).start()
                }
            }
        }

    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun  createImageFile(): File? {
        var imageName =  SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        var storageDir = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir!!.exists()) {
            storageDir.mkdir();
        }
        var tempFile = File(storageDir, imageName);
        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
            return null;
        }
        return tempFile;
    }



    /**
     * 安卓7相册相关设置
     */
    private fun StrictMethod() {
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }
      var mCropImageFile :File? = null
    var mCropImagePath :String?=null
    /***
     * 调用系统裁剪
     * @param uri 裁剪目标
     */
    fun startPhotoZoom(uri: Uri?) {
        try {
            if (isAndroidQ) {
                cropImageUri = createImageUri()
            } else {
                try {
                    mCropImageFile = createImageFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                if (mCropImageFile != null) {
                    mCropImagePath = mCropImageFile!!.absolutePath
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //适配Android 7.0文件权限，通过FileProvider创建一个content类型的Uri
                        cropImageUri = FileProvider.getUriForFile(
                            activity!!,
                             activity!!.packageName+ ".fileprovider", mCropImageFile!!)
                    } else {
                        cropImageUri = Uri.fromFile(mCropImageFile);
                    }
                }
            }
            val intent = Intent("com.android.camera.action.CROP")
            //设置源地址uri
            intent.setDataAndType(uri, "image/*")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //添加这一句表示对目标应用临时授权该Uri所代表的文件
            }
            intent.setDataAndType(uri, "image/*")
            intent.putExtra("crop", "true")
            intent.putExtra("aspectX", 1)
            intent.putExtra("aspectY", 1)
            intent.putExtra("outputX", 200)
            intent.putExtra("outputY", 200)
            intent.putExtra("scale", true)
            //设置目的地址uri
            intent.putExtra(
                MediaStore.EXTRA_OUTPUT, cropImageUri)
            //设置图片格式
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
            intent.putExtra("return-data", false)
            intent.putExtra("noFaceDetection", true) // no face detection
            startActivityForResult(intent, CROP_PICTURE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun createImageUri(): Uri? {
        var status = Environment.getExternalStorageState();
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return activity!!.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,  ContentValues());
        } else {
            return activity!!.contentResolver.insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI,  ContentValues());
        }

    }
}


