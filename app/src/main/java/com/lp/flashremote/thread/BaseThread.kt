package com.lp.flashremote.thread

/**
 * Created by xiyou3g on 2017/11/24.
 *
 */
interface BaseThread :Runnable{
    fun sendMessage(b:ByteArray)
    fun getMessage(b:ByteArray)
}