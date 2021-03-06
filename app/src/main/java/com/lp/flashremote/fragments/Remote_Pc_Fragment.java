package com.lp.flashremote.fragments;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
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
import com.lp.flashremote.beans.PackByteArray;
import com.lp.flashremote.beans.UserInfo;
import com.lp.flashremote.services.ConnectionCallBack;
import com.lp.flashremote.services.ConnectionManagerService;
import com.lp.flashremote.services.MainServices;
import com.lp.flashremote.utils.Command2JsonUtil;
import com.lp.flashremote.utils.JsonFactoryUtil;
import com.lp.flashremote.utils.SocketUtil;
import com.lp.flashremote.utils.StringUtil;
import com.lp.flashremote.utils.ToastUtil;
import com.lp.flashremote.utils.VoiceUtil;
import com.lp.flashremote.views.VolumwDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by PUJW on 2017/8/14.
 * 6666666666
 */

public class Remote_Pc_Fragment extends Fragment implements View.OnClickListener {

    private int[] fabId = new int[]{R.id.fab1, R.id.fab2, R.id.fab3, R.id.fab4,
            R.id.fab5, R.id.fab6, R.id.fab7, R.id.fab8, R.id.fab9};
    private int[] llId = new int[]{R.id.ll01, R.id.ll02, R.id.ll03};
    private FloatingActionButton[] fab = new FloatingActionButton[fabId.length];
    private List<AnimatorSet> mAnimList = new ArrayList<>();
    public static SocketUtil mSocketOP;  //已经连接的socket

    private FloatingActionButton mFab_more;
    private RelativeLayout mFab_Menu;
    private TextView mHideMenuTv;
    private TextView mConnPc;
    private TextView mBreakConnPc;

    private static CircleImageView mUserImage;//用户头像
    private static TextView mUserName;
    private static TextView mConnTv;//是否连接

    private RecognizerDialog iatDialog;
    private static Context mContext;
    private boolean isShow = false;

    private MainServices.SocketBinder socketBinder;
    private ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            socketBinder=(MainServices.SocketBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {}
    };


    private static Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
           if (msg.what==1){
               mConnTv.setText("已上线");
               mUserName.setText(UserInfo.getUsername());
               mUserImage.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.uesr_bg));
           }else if (msg.what==2){
               if(mSocketOP!=null){
                   mSocketOP.interrupt();
                   mSocketOP.setThreadStop();
                   mSocketOP.clearSocketCon();
                   mSocketOP=null;
               }
               ToastUtil.toastText(mContext, "上线失败!");
           }
        }
    };



    private EventBus mEventBus=null;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getContext();
        mEventBus=EventBus.getDefault();
        mEventBus.register(this);
    }

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
                if (mSocketOP!=null){
                    VoiceUtil voiceUtil = VoiceUtil.getInstance();
                    voiceUtil.setMcontext(getActivity(),mSocketOP);
                    voiceUtil.discern();
                }

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
        mUserImage=view.findViewById(R.id.user_image);
        mConnTv=view.findViewById(R.id.login_text);
        mUserName=view.findViewById(R.id.user_name);
        mFab_more = (FloatingActionButton) view.findViewById(R.id.pcmore);
        mConnPc = view.findViewById(R.id.connpc);
        mBreakConnPc = view.findViewById(R.id.breakConnpc);
        mFab_Menu = (RelativeLayout) view.findViewById(R.id.fab_menu);
        mHideMenuTv = (TextView) view.findViewById(R.id.hide_more_menu);
        for (int i = 0; i < fabId.length; i++) {
            fab[i] = (FloatingActionButton) view.findViewById(fabId[i]);
        }
    }

    private ConnectionManagerService.ConnectionBinder binder = null;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (ConnectionManagerService.ConnectionBinder) service;
            System.out.println("ServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private static ConnectionCallBack connectionCallBack = new ConnectionCallBack() {
        @Override
        public void connectionSuccess() {
            System.out.println("ConnectionSuccess , PC");
        }

        @Override
        public void connectionFailed() {
            System.out.println("connectionFailed , PC");
        }

        @Override
        public void connectionDisconnected() {
            System.out.println("connectionDisconnected , PC");
        }

        @Override
        public void serviceShutdodwn() {
            System.out.println("serviceShutdodwn , PC");
        }

        @Override
        public void getMessage(@NotNull PackByteArray data) {

        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.connpc:
                if(UserInfo.getPassword().equals("") || UserInfo.getUsername().equals("") ){
                    ToastUtil.toastText(getContext(),"请您先设置账户");
                    return;
                }
                if (mSocketOP == null) {
                    mSocketOP = SocketUtil.getInstance(UserInfo.getUsername(), UserInfo.getPassword());
                    mSocketOP.start();
                } else {
                    ToastUtil.toastText(getContext(), "您已经上线了！");
                }
                break;

            case R.id.breakConnpc:
                /**
                 * 中断服务
                         getActivity().unbindService(serviceConnection);
                 */

                //中断线程；
                if (mSocketOP != null) {
                    mSocketOP.interrupt();
                    mSocketOP.setThreadStop();
                    mSocketOP.clearSocketCon();
                    mSocketOP = null;
                    mConnTv.setText("未上线");
                    mUserName.setText("");
                    mUserImage.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.defualt_image));
                } else {
                    ToastUtil.toastText(getContext(), "您未上线，谢谢合作！");
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
                if (mSocketOP != null) {
                    mSocketOP.sendTestMsg(new SocketUtil.ConnectListener() {
                        @Override
                        public void connectSusess() {
                            new AlertDialog.Builder(getActivity())
                                    .setIcon(R.mipmap.icon_remote)
                                    .setTitle("可爱的程序员哥哥提示")
                                    .setMessage("您确定要关闭您的电脑吗 ?")
                                    .setPositiveButton("关了吧", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            mSocketOP.addMessageHighLevel(StringUtil.
                                                    cmdFactory(JsonFactoryUtil.getCmd("0",""),false));
                                            ToastUtil.toastText(getContext(), "关闭成功!");
                                        }
                                    })
                                    .setNegativeButton("取消关机", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            mSocketOP.addMessageHighLevel(StringUtil.
                                                    cmdFactory(JsonFactoryUtil.getCmd("1",""),false));
                                            ToastUtil.toastText(getContext(), "电脑还开着呢！");
                                        }
                                    })
                                    .show();
                        }

                        @Override
                        public void connectError() {
                            ToastUtil.toastText(getContext(), "未连接电脑,请重新连接！");
                        }
                    });
                }

                break;
            case R.id.fab2:
                if(mSocketOP!=null){
                final String screenShotTime=System.currentTimeMillis()+"";
                mSocketOP.addMessageHighLevel(StringUtil.cmdFactory(JsonFactoryUtil
                        .getCmd("2",screenShotTime),false));
                new AlertDialog.Builder(getActivity())
                        .setTitle("提示")
                        .setMessage("屏幕已经截取，是否回传?")
                        .setPositiveButton("确定" ,new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startPCActivity(screenShotTime);
                            }
                        })
                        .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .show();
            }else{
                ToastUtil.toastText(getContext(), "您未上线，谢谢合作！");
            }
                break;
            case R.id.fab3:
                startPCActivity("mouse");
                break;
            case R.id.fab4:
                //获取磁盘分区
                startPCActivity("disk");
                break;
            case R.id.fab5:
                //调节亮度
                if (mSocketOP!=null){
                    mSocketOP.sendTestMsg(new SocketUtil.ConnectListener() {
                        @Override
                        public void connectSusess() {
                           makeDialog(false);
                        }
                        @Override
                        public void connectError() {
                            ToastUtil.toastText(getActivity(),"连接失败，请重新连接！");
                        }
                    });
                }
                break;
            case R.id.fab6:
                startPCActivity("tools");
                break;
            case R.id.fab7:
                startPCActivity("search");
                break;
            case R.id.fab8://音量调节
                if (mSocketOP!=null){
                    mSocketOP.sendTestMsg(new SocketUtil.ConnectListener() {
                        @Override
                        public void connectSusess() {
                            makeDialog(true);
                        }
                        @Override
                        public void connectError() {
                            ToastUtil.toastText(getActivity(),"连接失败，请重新连接！");
                        }
                    });
                }

                break;
            case R.id.fab9:
                Toast.makeText(getActivity(), "请长按说话！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.hide_more_menu:
                hideFABMenu();
                break;
        }
    }

    public static void connisok( boolean b) {
        Message m=new Message();
        m.what=b?1:2;
        handler.sendMessage(m);
    }




    private void hideFABMenu() {
        mFab_Menu.setVisibility(View.GONE);
        isShow = false;
    }

    private void startPCActivity(String op) {
        if (mSocketOP!=null){
            Intent Intent = new Intent(getActivity(), PcOperationActivity.class);
            Intent.putExtra("operation", op);
            startActivity(Intent);
        }else{
            ToastUtil.toastText(getActivity(),"请先连接!!!");
        }

    }

    private void makeDialog(boolean b){
        VolumwDialog dialog;
        if (b)
             dialog = new VolumwDialog(getActivity(),mSocketOP);
        else
            dialog = new VolumwDialog(getActivity(),mSocketOP,
                    getActivity().getDrawable(R.mipmap.light),1);

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER | Gravity.TOP);
        lp.x = 0; // 新位置X坐标
        lp.y = 250;
        dialogWindow.setAttributes(lp);
        dialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mEventBus!=null)
            mEventBus.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(String str) {
        ToastUtil.toastText(getContext(),str);
    }

}
