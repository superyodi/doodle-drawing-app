package com.example.doodlebot

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.doodlebot.retrofit.RetrofitManager
import com.example.doodlebot.retrofit.RetrofitManager.Companion.instance
import kotlinx.android.synthetic.main.activity_check.view.*


// 사용자한테 IP받아서 서버 연결. 서버연결 안되면 실행안됨
class CheckIpActivity : AppCompatActivity(){
    private lateinit var editText : EditText
    private lateinit var button: Button
    private var url: String = ""


    val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        // 여기서 MainActivity 호출

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check)


        editText = findViewById(R.id.editText)
        button = findViewById(R.id.button)

        button.setOnClickListener {
            if (editText.text != null) {

                instance.makeHttpCall(editText.text.toString())
                instance.checkConnection()
                if (instance.connectResult) {
                    checkLabelDialog()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "서버연결에 실패했습니다. IP 주소를 다시 입력해주세요", Toast.LENGTH_SHORT
                    ).show()
                }

            } else {
                    Toast.makeText(
                        applicationContext,
                        "IP를 입력해주세요", Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun checkLabelDialog(){
        val builder = AlertDialog.Builder(this)

        with(builder)
        {
            setTitle("서버 연결 확인")
            setMessage("서버와 성공적으로 연결되었습니다.")
            setPositiveButton("확인", DialogInterface.OnClickListener(function = positiveButtonClick))
            show()
        }
    }
}


