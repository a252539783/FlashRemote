package com.lp.flashremote.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.lp.flashremote.R
import kotlinx.android.synthetic.main.fragment_setting.view.*

/**
 * Created by PUJW on 2017/8/14.
 */

class SettingFragment : Fragment(),View.OnClickListener {
    lateinit var viewArray:Array<View>
    lateinit var root:View
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root =  inflater!!.inflate(R.layout.fragment_setting, null)
        return root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        viewArray  = arrayOf(root.settings_about_us,root.settings_clear_cache,root.settings_clear_folder,root.settings_feedback,root.settings_see_accept_folder)
        viewArray.forEach { it.setOnClickListener(this) }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.settings_about_us->{

            }
            R.id.settings_feedback->{

            }
            R.id.settings_clear_cache->{

            }
            R.id.settings_clear_folder->{

            }
            R.id.settings_see_accept_folder->{

            }
            R.id.settings_exit->{
                val dialog = AlertDialog.Builder(context)
                dialog.setMessage("你要走吗")
                dialog.setNegativeButton("再见",object : DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                    }
                })
            }
        }
    }
}
