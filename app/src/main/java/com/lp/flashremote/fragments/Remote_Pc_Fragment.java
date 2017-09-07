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
import com.lp.flashremote.utils.SocketUtil;
import com.lp.flashremote.utils.VoiceUtil;
import com.lp.flashremote.views.VolumwDialog;

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


    private FloatingActionButton mFab_more;
    private RelativeLayout mFab_Menu;
    private TextView mHideMenuTv;
    private TextView mConnPc;

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
               VoiceUtil voiceUtil=VoiceUtil.getInstance();
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
        mFab_more.setOnClickListener(this);
        mHideMenuTv.setOnClickListener(this);
        for (int i = 0; i < fabId.length; i++) {
            fab[i].setOnClickListener(this);
        }
    }

    private void initView(View view) {
        mFab_more = (FloatingActionButton) view.findViewById(R.id.pcmore);
        mConnPc=(TextView)view.findViewById(R.id.connpc);
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
                SocketUtil socketUtil=SocketUtil.getInstance("lzl471954654","Test");
                socketUtil.connPc();
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
                    animator=mAnimList.get(fab.length);
                    animator.setTarget(mHideMenuTv);
                    animator.start();
                }
                break;
            case R.id.fab1:
                new AlertDialog.Builder(getActivity())
                        .setIcon(R.mipmap.icon_remote)
                        .setTitle("可爱的程序员哥哥提示")
                        .setMessage("您确定要关闭您的电脑吗 ? ")
                        .setPositiveButton("关了吧", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getActivity(),"关闭成功!",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("还是等等吧", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getActivity(),"嗯，电脑还开着！",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                break;
            case R.id.fab2:
                startPCActivity("screen");
                break;
            case R.id.fab3:
                startPCActivity("mouse");
                break;
            case R.id.fab4:
                startPCActivity("disk");
                break;
            case R.id.fab5:
                startPCActivity("luminance");
                break;
            case R.id.fab6:
                startPCActivity("tools");
                break;
            case R.id.fab7:
                startPCActivity("search");
                break;
            case R.id.fab8:
                VolumwDialog dialog=new VolumwDialog(getActivity());
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                dialogWindow.setGravity(Gravity.CENTER | Gravity.TOP);
                lp.x = 0; // 新位置X坐标
                lp.y = 250;
                dialogWindow.setAttributes(lp);
                dialog.show();
                break;
            case R.id.fab9:
                Toast.makeText(getActivity(),"请长按说话！",Toast.LENGTH_SHORT).show();
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

    private void startPCActivity(String op){
        Intent Intent=new Intent(getActivity(), PcOperationActivity.class);
        Intent.putExtra("operation",op);
        startActivity(Intent);
    }

}
