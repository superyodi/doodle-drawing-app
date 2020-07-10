package com.example.doodlebot

import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class DoodleActivity: AppCompatActivity() {


    private lateinit var imageView : ImageView
    private lateinit var btnChoice: Button
    private lateinit var btnNext: Button

    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_GALLERY_TAKE = 2

    lateinit var currentPhotoPath: String

}