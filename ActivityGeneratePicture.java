package com.hexway.linan.utils.picture;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hexway.linan.R;
import com.hexway.linan.function.share.YMShareActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

/**
 * Created by linanjs on 2017/2/16.
 * 生成图片并分享
 */
public class ActivityGeneratePicture extends AppCompatActivity {
    public static final int SHARE = 0;//分享
    public static final int FAILE = 1;//操作失败
    private static final String SAVE_PIC_PATH = Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED) ? Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera" : "";

    GeneratePictureView gpv;
    private Handler mHandler = new Handler_Generate(this);

    class Handler_Generate extends Handler {
        WeakReference<Activity> weakReference;//使用弱引用减少内存泄露

        public Handler_Generate(Activity activity) {
            weakReference = new WeakReference<Activity>(activity);
        }

        @Override
                public void handleMessage(Message msg) {
                    if (weakReference.get() != null) {
                        Bundle data = null;
                        switch (msg.what) {
                    case SHARE:
                        data = msg.getData();
                        //shareMsg("分享卡片", "分享卡片title", "分享卡片内容", data.getString("path"));
                        shareMsg(data.getString("path"));
                        Log.i("path", "the path is:" + data.getString("path"));
                        break;
                    case FAILE:
                        String strData = (String) msg.obj;
                        Toast.makeText(getApplicationContext(), strData, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_cap);
        gpv = (GeneratePictureView) findViewById(R.id.gpv);
        PictureContent pictureContent = (PictureContent) getIntent().getSerializableExtra("data");

        String headUrl = pictureContent.headUrl;
        String startAddress = pictureContent.startAddress;
        String endAddress = pictureContent.endAddress;
        String goodsInfo = pictureContent.goodsInfo;
        String carInfo = pictureContent.carInfo;
        String contact = pictureContent.contact;
        String phoneNum = pictureContent.phoneNum;

        gpv.init(headUrl, startAddress, endAddress, goodsInfo, carInfo, contact, phoneNum);

    }

    /**
     * 开启新线程进行
     * 分享
     * @param view
     */
    public void Share(View view) {
        if (TextUtils.isEmpty(SAVE_PIC_PATH))
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final File realFile = saveBitmap("share.jpg");
                    if (realFile == null) {
                        Message message = mHandler.obtainMessage(FAILE);
                        message.obj = "分享失败,文件过大!";
                        message.sendToTarget();
                    } else {
                        Message message = mHandler.obtainMessage(SHARE);
                        Bundle data = new Bundle();
                        data.putString("path", realFile.getAbsolutePath());
                        message.setData(data);
                        message.sendToTarget();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Message message = mHandler.obtainMessage(FAILE);
                    message.obj = "分享失败," + e.getMessage();
                    message.sendToTarget();
                } finally {
                }
            }
        }).start();
    }

    public void Cancel(View view) {
        finish();
    }

    /**
     * 保存图片到文件
     *
     * @param fileName 文件名称
     * @return
     * @throws Exception
     */
    private File saveBitmap(String fileName) throws Exception {
        Bitmap bitmap = gpv.getScreen();//截屏
        if (bitmap == null)
            return null;
        File file = new File(SAVE_PIC_PATH);
        if (!file.exists()) {
            file.mkdirs();//建立文件夹
        }
        final File realFile = new File(file, fileName);
        if (!realFile.exists()) {
            realFile.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(realFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);//图片进行压缩
        fos.flush();
        fos.close();
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
            System.gc(); // 通知系统回收
        }
        return realFile;
    }

   /* public void shareMsg(String activityTitle, String msgTitle, String msgText,
                         String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); //如果图片路径是空就分享纯文本
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/jpg");//图片类型
                Uri u = Uri.fromFile(f);//将图片路径转换成uri
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, activityTitle));
    }*/

    /**
     * 用友盟进行分享
     * @param imgPath
     */
    public void shareMsg(String imgPath) {
        /*File imgFile = new File(imgPath);
        Uri u = Uri.fromFile(imgFile);*/
        Intent intent = new Intent();
        intent.putExtra("imagePath", imgPath);
        intent.setClass(ActivityGeneratePicture.this, YMShareActivity.class);
        startActivity(intent);
        finish();
    }
}
