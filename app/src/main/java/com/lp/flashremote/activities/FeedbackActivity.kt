package com.lp.flashremote.activities

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import com.lp.flashremote.R
import com.lp.flashremote.utils.OkHttpUtils
import com.lp.flashremote.views.MyProgressDialog
import kotlinx.android.synthetic.main.activity_feedback.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class FeedbackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        supportActionBar?.title = "意见反馈"
        setListener()
    }

    private fun setListener(){
        feedback_ac_submit.setOnClickListener {
            if(feedback_ac_advice.text.toString()==""||feedback_ac_advice.text==null)
                return@setOnClickListener
            val dialog = MyProgressDialog(this,"正在提交")
            dialog.show()
            doAsync {
                if(OkHttpUtils.sendAdviceToServer(feedback_ac_advice.text.toString()))
                {
                    uiThread { showSnackBar(feedback_ac_submit,"提交成功") }
                }
                else
                {
                    uiThread { showSnackBar(feedback_ac_submit,"提交失败") }
                }
                uiThread { dialog.dismiss() }
            }
        }
    }
}

fun Activity.showSnackBar(view:View,msg:String){
    Snackbar.make(view,msg,Snackbar.LENGTH_SHORT).show()
}
