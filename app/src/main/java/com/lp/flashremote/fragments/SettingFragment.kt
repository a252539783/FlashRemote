package com.lp.flashremote.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.lp.flashremote.R
import com.lp.flashremote.activities.FileExplorerActivity
import com.lp.flashremote.adapters.FIleExplorerAdapter
import com.lp.flashremote.adapters.getSizeText
import kotlinx.android.synthetic.main.fragment_setting.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.uiThread
import java.io.File

/**
 * Created by PUJW on 2017/8/14.
 */

class SettingFragment : Fragment(),View.OnClickListener {
    lateinit var viewArray:Array<View>
    lateinit var root:View
    var cacheSize:Long = 0L
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root =  inflater!!.inflate(R.layout.fragment_setting, null)
        return root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        viewArray  = arrayOf(root.settings_about_us,root.settings_clear_cache,root.settings_clear_folder,root.settings_feedback,root.settings_see_accept_folder)
        viewArray.forEach { it.setOnClickListener(this) }
        Log.i("dir:",context.filesDir.absolutePath)
        Log.i("size:",context.filesDir.length().toString())
        //refreshCacheSize()
    }

    override fun onResume() {
        super.onResume()
        println("resume")
        refreshCacheSize()
    }

    fun refreshCacheSize(){
        doAsync {
            fun countSize(parent:File):Long{
               var size = 0L
                parent.listFiles().forEach {
                    size += if(it.isDirectory)
                        countSize(it)
                    else
                        it.length()
                }
                return size
            }
            cacheSize = countSize(context.filesDir.parentFile)
            uiThread { root.settings_cache_size.text = getSizeText(cacheSize) }
        }
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
                startActivity<FileExplorerActivity>("mode" to FileExplorerActivity.MODE_EXPLORER,"dataType" to FIleExplorerAdapter.BASE_FILE_EXPLORER,"title" to "目录浏览","rootPath" to context.filesDir.parentFile.absolutePath)
            }
            R.id.settings_exit->{
                val dialog = AlertDialog.Builder(context)
                dialog.setMessage("你要走吗")
                dialog.setNegativeButton("再见") {
                    p0, p1 ->

                }
            }
        }
    }
}
