package com.example.doodlebot

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Looper
import android.os.PersistableBundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.doodlebot.retrofit.RetrofitManager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.Random


class DoodleActivity: AppCompatActivity() {

    var retrofitManager: RetrofitManager = RetrofitManager()
    // index는 0-999사이의 랜덤변수
    var index = Random().nextInt(1000)

    private lateinit var label: String
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
        val label = intent.getStringExtra("label")

        textView.setText(label)

        // onCreate하면서 doodle dataset 받아다가 imageview에 표시
        imageView?.setBitmapFrom(label, index)

        btnNext.setOnClickListener{
            // index값을 랜덤으로 다시 돌려서 dataset을 다시 받아온다.
            index = Random().nextInt(1000)
            imageView?.setBitmapFrom(label, index)
        }

        btnChoice.setOnClickListener {
            retrofitManager.sendDoodleIndex(label, index.toString())
            Toast.makeText(applicationContext,
                "낙서 선택을 완료하셨습니다. 두들 로봇을 실행해주세요", Toast.LENGTH_SHORT).show()


        }


    }

    fun ImageView.setBitmapFrom(label: String, index: Int) {
        val imageView = this
        retrofitManager.getDoodleImage(label, index.toString()) {
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


}