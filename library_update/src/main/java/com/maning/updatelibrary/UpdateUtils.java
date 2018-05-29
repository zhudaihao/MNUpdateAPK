package com.maning.updatelibrary;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 下载安装App工具
 */

public class UpdateUtils {
    //单利
    private UpdateUtils() {
        this.downloadCallBack = downloadCallBack;
    }

    private static UpdateUtils updateUtils = new UpdateUtils();

    public static UpdateUtils getInstance() {
        return updateUtils;
    }

    //监听
    public InstallUtils.DownloadCallBack downloadCallBack;
    //常用选择dialog
    public Dialog dialog;
    public RelativeLayout rl_layout;//最外层布局
    public LinearLayout rl_tag_layout;
    public RelativeLayout rl_progress;
    public TextView tv_progress;
    public ProgressBar pb_Progress;
    public LinearLayout ll_upInfo;
    public TextView tv_title;
    public RadioButton rb_cancel;
    public RadioButton rb_install;
    public ImageView iv_icon;

    /**
     * 布局
     */
    public void shownUpDialog(final Activity context, final String appPath) {
        dialog = new Dialog(context, R.style.CursorDialogNotFloatTheme);
        dialog.setContentView(R.layout.dialog_update_app);

        //获取控件
        rl_layout = (RelativeLayout) dialog.findViewById(R.id.rl_layout);
        rl_tag_layout = (LinearLayout) dialog.findViewById(R.id.rl_tag_layout);
        rl_progress = (RelativeLayout) dialog.findViewById(R.id.rl_progress);
        tv_progress = (TextView) dialog.findViewById(R.id.tv_progress);
        pb_Progress = (ProgressBar) dialog.findViewById(R.id.pb_Progress);
        ll_upInfo = (LinearLayout) dialog.findViewById(R.id.ll_upInfo);
        tv_title = (TextView) dialog.findViewById(R.id.tv_title);
        rb_cancel = (RadioButton) dialog.findViewById(R.id.rb_cancel);
        rb_install = (RadioButton) dialog.findViewById(R.id.rb_install);
        iv_icon = (ImageView) dialog.findViewById(R.id.iv_icon);

        //监听按钮
        rb_install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //8.0系统安装(26)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //先获取是否有安装未知来源应用的权限
                    boolean haveInstallPermission = context.getPackageManager().canRequestPackageInstalls();
                    if (!haveInstallPermission) {
                        //跳转设置开启允许安装
                        Uri packageURI = Uri.parse("package:" + context.getPackageName());
                        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                        context.startActivityForResult(intent, 1000);
                        return;
                    }
                } else {
                    publicApk(context, appPath);
                }

            }
        });

        rb_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAnimator();
            }
        });
        dialog.show();
        //动画
        startAnimator();
    }


    /**
     * 动画
     */
    private void startAnimator() {
        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation aa = new AlphaAnimation(0, 1);
        aa.setDuration(500);
        animationSet.addAnimation(aa);
        rl_layout.startAnimation(animationSet);

    }

    private void finishAnimator() {
        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation aa = new AlphaAnimation(1, 0);
        aa.setDuration(500);
        animationSet.addAnimation(aa);
        rl_layout.startAnimation(animationSet);
        //监听动画 关闭对话框
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dismissUpDialog();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    /**
     * 安装APP
     */
    private void publicApk(Activity context, String appPath) {
        //设置下载升级
        InstallUtils.with(context)
                //必须-下载地址
                .setApkUrl(appPath)
                //非必须，默认update
                .setApkName("update")
                //非必须-下载保存的路径
                // .setApkPath(Constants.APK_SAVE_PATH)
                //非必须-下载回调
                .setCallBack(downloadCallBack)
                //开始下载
                .startDownload();
    }


    /**
     * 下载监听
     */
    public void initCallBack(final Activity context) {
        downloadCallBack = new InstallUtils.DownloadCallBack() {
            @Override
            public void onStart() {
                tv_progress.setText("0%");
                pb_Progress.setProgress(0);
                rl_progress.setVisibility(View.VISIBLE);
                ll_upInfo.setVisibility(View.GONE);
            }

            @Override
            public void onComplete(String path) {
                //安装
                InstallUtils.installAPK(context, path, new InstallUtils.InstallCallBack() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(context, "正在安装程序", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(Exception e) {
                        Toast.makeText(context, "安装失败", Toast.LENGTH_SHORT).show();
                    }
                });

                tv_progress.setText("100%");
                pb_Progress.setProgress(100);
                finishAnimator();
            }

            @Override
            public void onLoading(long total, long current) {
                tv_progress.setText((int) (current * 100 / total) + "%");
                pb_Progress.setProgress((int) (current * 100 / total));
            }

            @Override
            public void onFail(Exception e) {

            }

            @Override
            public void cancel() {

            }
        };
    }


    /**
     * 关闭对话框
     */
    public void dismissUpDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;

        }
    }

}
