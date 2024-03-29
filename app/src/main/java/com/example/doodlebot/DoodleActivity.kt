package com.example.doodlebot

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.doodlebot.retrofit.RetrofitManager.Companion.instance
import java.util.*


class DoodleActivity: AppCompatActivity() {


    val TAG: String = "DoodleActivity"

    private lateinit var imageView : ImageView
    private lateinit var btnChoice: Button
    private lateinit var btnNext: Button
    private lateinit var textView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doodle)

        imageView = findViewById(R.id.imageView)
        btnChoice = findViewById(R.id.btnChoice)
        btnNext = findViewById(R.id.btnNext)
        textView = findViewById(R.id.textView)

        val intent = intent
        val imgPath = intent.getStringExtra("imgPath")


        // onCreate하면서 doodle dataset 받아다가 imageview에 표시
        imageView?.setBitmapFrom(imgPath)

        btnNext.setOnClickListener{
            // index값을 랜덤으로 다시 돌려서 dataset을 다시 받아온다.
            imageView?.setBitmapFrom(imgPath)
        }

        btnChoice.setOnClickListener {
            val dialog = WaitingDialog.create(this@DoodleActivity)
            dialog.show()

            instance.confirmDrawDoodle() {
                dialog.dismiss()
                if (it)
                    Toast.makeText(applicationContext,
                        "낙서 선택을 완료하셨습니다. 두들 로봇을 실행해주세요", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(applicationContext,
                        "낙서를 선택하는 도중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun ImageView.setBitmapFrom(imgPath: String) {
        val dialog = WaitingDialog.create(this@DoodleActivity)
        dialog.show()

        val imageView = this
        instance.getDoodlesImage(imgPath) {
            dialog.dismiss()

            val bitmap: Bitmap?
            bitmap = if (it != null) it else {
                // create empty bitmap
                val w = 1
                val h = 1
                val conf = Bitmap.Config.ARGB_8888

                Bitmap.createBitmap(w, h, conf)
            }
            Looper.getMainLooper().run {
                imageView.setImageBitmap(bitmap!!)
            }
        }
    }

    fun imgResize(bitmap: Bitmap, x: Int, y: Int): Bitmap? {
        val output = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        val w = bitmap.width
        val h = bitmap.height
        val src = Rect(0, 0, w, h)
        val dst = Rect(0, 0, x, y) //이 크기로 변경됨
        canvas.drawBitmap(bitmap, src, dst, null)
        return output
    }


}
