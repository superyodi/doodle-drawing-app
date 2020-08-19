package com.example.doodlebot

import android.app.Dialog
import android.content.Context


class WaitingDialog {
    companion object {
        fun create(ctx: Context): Dialog {
            val dialog = Dialog(ctx)
            dialog.setContentView(R.layout.dialog_waiting)
            return dialog
        }
    }
}
