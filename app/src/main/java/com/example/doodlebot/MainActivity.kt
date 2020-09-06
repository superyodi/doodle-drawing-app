package com.example.doodlebot

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.doodlebot.retrofit.RetrofitManager.Companion.instance
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

    var mHandler = Handler()

    lateinit var currentPhotoPath: String
    lateinit var uploadedImgName: String
    lateinit var uploadedImg: File


    val positiveButtonClick = { dialog: DialogInterface, which: Int ->

        // 여기서 DoodleActivity 호출

        val intent = Intent(this, DoodleActivity::class.java).apply {
            putExtra("imgPath", uploadedImgName)
        }
        startActivity(intent)
    }
    val negativeButtonClick = { dialog: DialogInterface, which: Int ->
        Toast.makeText(applicationContext,
            "다시 한번 촬영해주세요", Toast.LENGTH_SHORT).show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        btnCamera = findViewById(R.id.btnCamera)
        btnGallery = findViewById(R.id.btnGallery)
        btnSend = findViewById(R.id.btnSend)

        // 카메라 버튼
        btnCamera.setOnClickListener {
            if(checkPermission()) {
                dispatchTakePictureIntent()
            } else{
                requestPermission()
            }
        }

        //갤러리 버튼
        btnGallery.setOnClickListener {
            if(checkPermission()) {
                openGalleryForImage()
            } else {
                requestPermission()
            }
        }

        btnSend.setOnClickListener {
            val dialog = WaitingDialog.create(this@MainActivity)
            dialog.show()

            instance.getObjectDetection(uploadedImg) {
                dialog.dismiss()

                val bitmap: Bitmap?
                bitmap = if (it != null) it else {
                    // create empty bitmap
                    val w = 1
                    val h = 1
                    val conf = Bitmap.Config.ARGB_8888

                    Bitmap.createBitmap(w, h, conf)
                }
                imageView.setImageBitmap(bitmap!!)

                mHandler.postDelayed({
                    // detected image 보여주고 5초 뒤에 check dialog 실행
                    checkLabelDialog()
                }, 5000) // 5초후
            }
        }

    }

    // 사용자에게 label이 true인지 확인하는 Dialog Message
    private fun checkLabelDialog(){
        val builder = AlertDialog.Builder(this)

        with(builder)
        {
            setTitle("객체 탐지 결과 확인")
            setMessage("검출된 객체가 실제와 일치합니까?")
            setPositiveButton("예", DialogInterface.OnClickListener(function = positiveButtonClick))
            setNegativeButton("아니요", negativeButtonClick)
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
            uploadedImgName = currentPhotoPath.replace(storageDir.toString()+"/", "")
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
                    uploadedImg = file


                    // SDK 29이상부터 getBitmap() 사용할수없다
                    if (Build.VERSION.SDK_INT < 28) {
                        val bitmap = MediaStore.Images.Media
                            .getBitmap(contentResolver, Uri.fromFile(file))
                        imageView.setImageBitmap(bitmap)

                    }
                    else {
                        val decode = ImageDecoder.createSource(this.contentResolver,
                            Uri.fromFile(file))
                        val bitmap = ImageDecoder.decodeBitmap(decode)
                        imageView.setImageBitmap(bitmap)
                    }
                }
            }
            2 -> {
                if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_GALLERY_TAKE)
                    imageView.setImageURI(data?.data)
            }
        }
    }


//    fun getRealPathFromURI(contentUri: Uri?): String? {
//        val proj = arrayOf(MediaStore.Images.Media._ID)
//        val cursor =
//            contentResolver.query(contentUri!!, proj, null, null, null)
//        cursor!!.moveToNext()
//        val path =
//            cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
//        val uri = Uri.fromFile(File(path))
//        cursor.close()
//        return path
//    }
}
