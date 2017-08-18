package com.lp.flashremote.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lp.flashremote.Model.FileManagerModel

import com.lp.flashremote.R
import com.lp.flashremote.beans.BaseFile
import com.lp.flashremote.beans.MusicFile
import com.lp.flashremote.beans.VideoFile
import kotlinx.android.synthetic.main.fragment_file_manager.*

/**
 * Created by PUJW on 2017/8/14.

 */

class FileFragment : Fragment(),View.OnClickListener {
    lateinit var rootView:View
    lateinit var mContext:Context
    lateinit var fileManager:FileManagerModel

    lateinit var musicList:java.util.ArrayList<MusicFile>
    lateinit var videoList:java.util.ArrayList<VideoFile>
    lateinit var imageList:java.util.ArrayList<BaseFile>
    lateinit var docList:java.util.ArrayList<BaseFile>
    lateinit var apkLIst:java.util.ArrayList<BaseFile>
    lateinit var zipList:java.util.ArrayList<BaseFile>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileManager = FileManagerModel(mContext,mHandler)
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_file_manager, container, false)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListener()
        fileManager.startCount()
    }

    private fun setListener(){
        val buttonView = arrayOf(m_video,m_pic,m_apk,m_music,m_document,m_download,m_zip,m_bluetooth,m_remote)
        buttonView.forEach {
            it.setOnClickListener(this)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.m_music->{

            }
            R.id.m_video->{

            }
            R.id.m_document->{

            }
            R.id.m_pic->{

            }
            R.id.m_apk->{

            }
            R.id.m_download->{

            }
            R.id.m_bluetooth->{

            }
            R.id.m_remote->{

            }
            R.id.m_zip->{

            }

        }
    }

    private fun refreshCount(bundle: Bundle){
        musicList = bundle.getSerializable("musicList") as ArrayList<MusicFile>
        videoList = bundle.getSerializable("videoList") as ArrayList<VideoFile>
        imageList = bundle.getSerializable("imageList") as ArrayList<BaseFile>
        docList   = bundle.getSerializable("docList") as ArrayList<BaseFile>
        zipList   = bundle.getSerializable("zipList") as ArrayList<BaseFile>
        apkLIst   = bundle.getSerializable("apkList") as ArrayList<BaseFile>

        m_music_count.text = "${musicList.size}项"
        m_music_count.visibility = View.VISIBLE
        m_video_count.text = "${videoList.size}项"
        m_video_count.visibility = View.VISIBLE
        m_pic_count.text = "${imageList.size}项"
        m_pic_count.visibility = View.VISIBLE
        m_zip_count.text = "${zipList.size}项"
        m_zip_count.visibility = View.VISIBLE
        m_document_count.text = "${docList.size}项"
        m_document_count.visibility = View.VISIBLE
        m_apk_count.text = "${apkLIst.size}项"
        m_apk_count.visibility = View.VISIBLE
    }
    private val mHandler = object : Handler(){
        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                REFRESH_COUNT -> {
                    if(msg!=null)
                        refreshCount(msg.data)
                }
                else -> {

                }
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context!=null)
            mContext = context
    }
    companion object {
        val REFRESH_COUNT = 1
    }
}
