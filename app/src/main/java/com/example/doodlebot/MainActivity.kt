package com.example.doodlebot

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.doodlebot.retrofit.DoodleLabel
import com.example.doodlebot.retrofit.RetrofitManager
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var imageView : ImageView
    private lateinit var btnCamera: Button
    private lateinit var btnGallery: Button
    private lateinit var btnSend: Button

    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_GALLERY_TAKE = 2
    val TAG: String = "로그"

    lateinit var currentPhotoPath: String
    lateinit var uploadedImg: File

    var label: String? = null
    var retrofitManager: RetrofitManager = RetrofitManager()


    val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        // 여기서 DoodleActivity 호출

        val intent = Intent(this, DoodleActivity::class.java).apply {
            putExtra("label",label)
        }
        startActivity(intent)
    }
    val negativeButtonClick = { dialog: DialogInterface, which: Int ->
        Toast.makeText(applicationContext,
            android.R.string.no, Toast.LENGTH_SHORT).show()
    }
    val neutralButtonClick = { dialog: DialogInterface, which: Int ->
        Toast.makeText(applicationContext,
            "Maybe", Toast.LENGTH_SHORT).show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        btnCamera = findViewById(R.id.btnCamera)
        btnGallery = findViewById(R.id.btnGallery)
        btnSend = findViewById(R.id.btnSend)

        btnCamera.setOnClickListener {
            if(checkPermission()) {
                dispatchTakePictureIntent()
                galleryAddPic()
            } else{
                requestPermission()
            }

        }

        btnGallery.setOnClickListener {
            if(checkPermission()) {
                openGalleryForImage()
            } else {
                requestPermission()
            }
        }

//                retrofitManager.getDoodleImage(label, index.toString()) {
//            val bitmap: Bitmap?
//            bitmap = if (it != null) it else {
//                // create empty bitmap
//                val w = 1
//                val h = 1
//                val conf = Bitmap.Config.ARGB_8888
//                Bitmap.createBitmap(w, h, conf)
//            }
//
//            Looper.getMainLooper().run {
//                imageView.setImageBitmap(bitmap!!)
//            }
//        }

        btnSend.setOnClickListener {
            retrofitManager.getDoodleLabel(uploadedImg) {
                label = it
                label?.let {
                    checkLabelDialog(label!!)
                } ?:  run {
                    // label이 null일때, yoloLabel이 null인 경우니까 yolo가 객체검출을 실패했을 상황
                    Toast.makeText(applicationContext,
                        "사진에서 사물검출을 실패했습니다. 다른 사진을 입력하세요", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 사용자에게 label이 true인지 확인하는 Dialog Message
    private fun checkLabelDialog(label: String){

        val builder = AlertDialog.Builder(this)

        with(builder)
        {
            setTitle("사물검출 확인")
            setMessage("이 사진은 [${label}]이 맞습니까?")
            setPositiveButton("OK", DialogInterface.OnClickListener(function = positiveButtonClick))
            setNegativeButton(android.R.string.no, negativeButtonClick)
//            setNeutralButton("Maybe", neutralButtonClick)
            show()
        }

    }




//    카메라 권한 요청 -> 사용자에게 권한요청하는 메세지가 보여지고 예 || 아니오 선택
    private fun requestPermission() {
    ActivityCompat.requestPermissions(
        this, arrayOf(READ_EXTERNAL_STORAGE, CAMERA),
        REQUEST_IMAGE_CAPTURE
    )
}

//    카메라 권한 체크 -> 사용자가 카메라 권한을 허용했는지 check
    private fun checkPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)}

//    권한요청 결과
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG", "Permission: " + permissions[0] + "was " + grantResults[0] + "카메라 허가 완료")
        }
        else {
            Log.d("TAG", "카메라 허가 실패")
        }
    }

//    카메라 열기 (Android 7.0(Nougat) 이상을 가정한다.)
    private fun dispatchTakePictureIntent() {
        Intent (MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // 카메라 엑티비티가 나오고 사진(data)를 return한다.
            takePictureIntent.resolveActivity(packageManager)?.also {
                // 그 사진이 가야하는 File을 만든다.
                val photoFile: File? =
                    try {
                        createImageFile()

                    } catch (ex: IOException) {
                        Log.d("TAG", "이미지파일 만들다가 ERROR")
                        null
                    }

                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.doodlebot.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

//    카메라로 촬영한 이미지 -> 파일로 저장
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // 이미지파일의 이름만들기
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

//    갤러리에 사진 저장하기 (작동안함....)
    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            sendBroadcast(mediaScanIntent)
        }
    }

//    갤러리에서 사진 가져오기
    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY_TAKE)
    }





    //    onActivityResult에서 사진 받기
    override fun onActivityResult( requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            1 -> {
                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
                    // 카메라에서 받은 데이터가 있을 경우
                     val file = File(currentPhotoPath)
                    print(currentPhotoPath)
                    uploadedImg = file


                    // SDK 29이상부터 getBitmap() 사용할수없다
                    if (Build.VERSION.SDK_INT < 28) {
                        val bitmap = MediaStore.Images.Media
                            .getBitmap(contentResolver, Uri.fromFile(file))
                        imageView.setImageBitmap(bitmap)
                        label = ""

                    }
                    else {
                        val decode = ImageDecoder.createSource(this.contentResolver,
                            Uri.fromFile(file))
                        val bitmap = ImageDecoder.decodeBitmap(decode)
                        imageView.setImageBitmap(bitmap)
                        label = ""

                    }
                }
            }
            2 -> {
                if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_GALLERY_TAKE) {


                    imageView.setImageURI(data?.data)
                }
            }
        }
    }


//    private String getRealPathFromURI(Uri contentURI) {
//
//
//
//        String result;
//
//        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
//
//
//
//        if (cursor == null) { // Source is Dropbox or other similar local file path
//
//            result = contentURI.getPath();
//
//
//
//        } else {
//
//            cursor.moveToFirst();
//
//            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//
//            result = cursor.getString(idx);
//
//            cursor.close();
//
//        }
//
//
//
//        return result;
//
//    }









}

