package com.lp.flashremote.fragments;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ui.RecognizerDialog;
import com.lp.flashremote.R;
import com.lp.flashremote.activities.PcOperationActivity;
import com.lp.flashremote.beans.ServerProtocol;
import com.lp.flashremote.utils.SocketUtil;
import com.lp.flashremote.utils.StringUtil;
import com.lp.flashremote.utils.ToastUtil;
import com.lp.flashremote.utils.VoiceUtil;
import com.lp.flashremote.views.VolumwDialog;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by PUJW on 2017/8/14.
 *
 */

public class Remote_Pc_Fragment extends Fragment implements View.OnClickListener {

    private int[] fabId = new int[]{R.id.fab1, R.id.fab2, R.id.fab3, R.id.fab4,
            R.id.fab5, R.id.fab6, R.id.fab7, R.id.fab8, R.id.fab9};
    private int[] llId = new int[]{R.id.ll01, R.id.ll02, R.id.ll03};
    private FloatingActionButton[] fab = new FloatingActionButton[fabId.length];
    private List<AnimatorSet> mAnimList = new ArrayList<>();
    private SocketUtil mSocketOP;  //已经连接的socket

    private FloatingActionButton mFab_more;
    private RelativeLayout mFab_Menu;
    private TextView mHideMenuTv;
    private TextView mConnPc;
    private TextView mBreakConnPc;

    private RecognizerDialog iatDialog;

    private boolean isShow = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pc, container, false);
        initView(view);
        setDefaultValues();
        //语音长按监听
        view.findViewById(R.id.fab9).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                VoiceUtil voiceUtil = VoiceUtil.getInstance();
                voiceUtil.setMcontext(getActivity());
                voiceUtil.discern();
                return false;
            }
        });
        return view;
    }

    private void setDefaultValues() {
        for (int i = 0; i < fab.length; i++) {
            mAnimList.add((AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.add_anim));
        }
        mAnimList.add((AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.add_anim));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindEvents();
    }

    private void bindEvents() {
        mConnPc.setOnClickListener(this);
        mBreakConnPc.setOnClickListener(this);
        mFab_more.setOnClickListener(this);
        mHideMenuTv.setOnClickListener(this);
        for (int i = 0; i < fabId.length; i++) {
            fab[i].setOnClickListener(this);
        }
    }

    private void initView(View view) {
        mFab_more = (FloatingActionButton) view.findViewById(R.id.pcmore);
        mConnPc = view.findViewById(R.id.connpc);
        mBreakConnPc=view.findViewById(R.id.breakConnpc);
        mFab_Menu = (RelativeLayout) view.findViewById(R.id.fab_menu);
        mHideMenuTv = (TextView) view.findViewById(R.id.hide_more_menu);
        for (int i = 0; i < fabId.length; i++) {
            fab[i] = (FloatingActionButton) view.findViewById(fabId[i]);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.connpc:
                if (mSocketOP == null){
                    mSocketOP=new SocketUtil("lzl471954654", "Test");
                    Log.e("thread-id",mSocketOP.getId()+"");
                    mSocketOP.start();
                    ToastUtil.toastText(getContext(),"上线成功!");
                }else{
                    ToastUtil.toastText(getContext(),"您已经上线了！");
                }
                break;

            case R.id.breakConnpc:
                //中断线程；
                if (mSocketOP!=null){
                    mSocketOP.interrupt();
                    mSocketOP.setThreadStop();
                    mSocketOP = null;
                    ToastUtil.toastText(getContext(),"断开成功！");
                }else{
                    ToastUtil.toastText(getContext(),"您未上线，谢谢合作！");
                }
                break;
            case R.id.pcmore:
                isShow = !isShow;
                mFab_Menu.setVisibility(isShow ? View.VISIBLE : View.GONE);
                if (isShow) {
                    AnimatorSet animator;
                    for (int i = 0; i < fab.length; i++) {
                        animator = mAnimList.get(i);
                        animator.setTarget(fab[i]);
                        animator.start();
                    }
                    animator = mAnimList.get(fab.length);
                    animator.setTarget(mHideMenuTv);
                    animator.start();
                }
                break;
            case R.id.fab1:
                //发送消息试探是否仍然连接
                if (mSocketOP!=null ) {
                    mSocketOP.sendTestMessage(new SocketUtil.ConnectListener() {
                        @Override
                        public void connectSusess() {
                            new AlertDialog.Builder(getActivity())
                                    .setIcon(R.mipmap.icon_remote)
                                    .setTitle("可爱的程序员哥哥提示")
                                    .setMessage("您确定要关闭您的电脑吗 ?")
                                    .setPositiveButton("关了吧", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            mSocketOP.addMessage(StringUtil.operateCmd("0","@@op@@"));
                                            ToastUtil.toastText(getContext(), "关闭成功!");
                                        }
                                    })
                                    .setNegativeButton("还是等等吧", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            mSocketOP.addMessage(StringUtil.operateCmd("3","@@op@@"));
                                            ToastUtil.toastText(getContext(),"电脑还开着呢！");
                                        }
                                    })
                                    .show();
                        }
                        @Override
                        public void connectError() {
                            ToastUtil.toastText(getContext(),"未连接电脑,请重新连接！");
                        }
                    });
                }

                break;
            case R.id.fab2:
                //startPCActivity("screen");
                break;
            case R.id.fab3:
                //startPCActivity("mouse");
                break;
            case R.id.fab4:
                //获取磁盘分区
                mSocketOP.addMessage(StringUtil.operateCmd("4","getDisk"));
               // startPCActivity("disk");
                break;
            case R.id.fab5:
                //调节亮度
                mSocketOP.addMessage(StringUtil.operateCmd("5","@@op@@"));
                //startPCActivity("luminance");
                break;
            case R.id.fab6:
                startPCActivity("tools");
                break;
            case R.id.fab7:
               // startPCActivity("search");
                break;
            case R.id.fab8:
                VolumwDialog dialog = new VolumwDialog(getActivity());
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                dialogWindow.setGravity(Gravity.CENTER | Gravity.TOP);
                lp.x = 0; // 新位置X坐标
                lp.y = 250;
                dialogWindow.setAttributes(lp);
                dialog.show();
                break;
            case R.id.fab9:
                Toast.makeText(getActivity(), "请长按说话！", Toast.LENGTH_SHORT).show();
                // startPCActivity("yuyin");
                break;
            case R.id.hide_more_menu:
                hideFABMenu();
                break;
        }
    }

    private void hideFABMenu() {
        mFab_Menu.setVisibility(View.GONE);
        isShow = false;
    }

    private void startPCActivity(String op) {
        Intent Intent = new Intent(getActivity(), PcOperationActivity.class);
        Intent.putExtra("operation", op);
        startActivity(Intent);
    }

}
