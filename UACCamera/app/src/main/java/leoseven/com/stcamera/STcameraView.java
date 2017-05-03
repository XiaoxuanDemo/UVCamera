package leoseven.com.stcamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by 10789 on 2017-05-02.
 */

public class STcameraView extends SurfaceView implements Camera.ShutterCallback, Camera.PictureCallback, ViewTreeObserver.OnGlobalLayoutListener {
    private Camera mCamera;//相机
    private SurfaceView suffer;//预览界面

    /**
     * 设置车牌号
     * @param carID
     */
    public void setCarID(String carID) {
        this.carID = carID;
    }

    /**
     * 设置速度
     * @param speed
     */
    public void setSpeed(String speed) {
        this.speed = speed;
    }

    /**
     * 设置学员编号
     * @param studentID
     */
    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    /**
     * 设置教练员编号
     * @param teacherID
     */
    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    /**
     * 设置驾校编号
     * @param schoolID
     */
    public void setSchoolID(String schoolID) {
        this.schoolID = schoolID;
    }

    /**
     * 设置经纬度
     * @param GPS
     */
    public void setGPS(String GPS) {
        this.GPS = GPS;
    }

    public STcameraView(Context context) {
        this(context,null);
    }

    public STcameraView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    /**
     * 加载布局
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private Context mContext;
    public STcameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        suffer=this;
        mContext=context;
        initCamera();
    }
    public void changeChanel(final int chanal){
        mCamera.stopPreview();
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCamera.setAnalogInputState(true, 0,
                        Camera.SYSTEM_PAL,chanal, Camera.FORMAT_PL_420);
                mCamera.startPreview();
            }
        },1000);
    }
    /**
     * 初始化相机
     */
    private void initCamera() {
//        int cNum = Camera.getNumberOfCameras();
//        for (int i = 0; i < cNum; i++) {
//            try {
//                mCamera=Camera.open(i);
//            }catch (Exception e){
//                mCamera=null;
//            }
//        }
//        if (mCamera==null){
//            throw new RuntimeException("相机开启失败,请检查相机是否连接正常");
//        }
        mCamera=CamerHolder.getInstance().getmCamera();
        carID=CamerHolder.getInstance().getCarID();
        GPS=CamerHolder.getInstance().getGPS();
        schoolID=CamerHolder.getInstance().getSchoolID();
        speed=CamerHolder.getInstance().getSpeed();
        studentID=CamerHolder.getInstance().getStudentID();
        teacherID=CamerHolder.getInstance().getTeacherID();
        mCamera.setAnalogInputState(true, 0,
                Camera.SYSTEM_PAL, Camera.CHANNEL_ONLY_1, Camera.FORMAT_PL_420);
        this.getViewTreeObserver().addOnGlobalLayoutListener(this);

    }

    private OnTakePhoto call;

    /**
     * 拍照调用
     * @param call 拍照成功或者是失败的回调
     */
    public void takePhoto(OnTakePhoto call){
        mCamera.takePicture(this,this,this);
        this.call=call;
    }
    @Override
    public void onShutter() {
        if (call!=null) {
            call.onShutter();
        }
    }
    private File root=new File(Environment.getExternalStorageDirectory(),"ShenTongPic");
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        if(data!=null){
            BitmapFactory.Options opt = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length,opt).copy(Bitmap.Config.ARGB_8888, true);
//            Bitmap bm1 = bitmap.createBitmap(bitmap, 0, 0, opt.outWidth/2, opt.outHeight / 2);
//
//
//            Bitmap bm2=Bitmap.createBitmap(bitmap,opt.outWidth/2,0,opt.outWidth/2,opt.outHeight/2);
//
//            addWaterMask(bm1);

            addWaterMask(bitmap);
            UUID uuid = UUID.randomUUID();
            CharSequence picname = uuid.toString().subSequence(0, 6);
            File right = new File(root, picname.toString()+"_one.jpg");
//            File left = new File(root, picname.toString() + "_two.jpg");
            if (!root.exists()) {
                root.mkdirs();
            }
            try {
//                FileOutputStream fos1 = new FileOutputStream(left);
                FileOutputStream fos2 = new FileOutputStream(right);
//                boolean compress = bm1.compress(Bitmap.CompressFormat.JPEG, 75, fos1);
                boolean compress1 = bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos2);
                if (compress1) {
                    call.onSuccess(right.getAbsolutePath());
                    this.onGlobalLayout();
                }
//                if (bm1!=null) {
//                    bm1.recycle();
//                }
//                if (bm2!=null) {
//                    bm2.recycle();
//                }
                if (bitmap!=null) {
                    bitmap.recycle();
                }
//                if (fos1!=null) {
//                    fos1.close();
//                }
                if (fos2!=null){
                    fos2.close();
                }
            } catch (FileNotFoundException e) {
                call.onError(e);
                this.onGlobalLayout();
            } catch (IOException e) {
                call.onError(e);
                this.onGlobalLayout();
            }
        }else {
            this.onGlobalLayout();
        }


    }
    private String schoolID="123456";//驾校编号
    private String teacherID="654321";//教练编号
    private String studentID="987654";//学员编号
    private String speed="50";//车速
    private String carID="川A 66666";//车牌号
    private String time="2013-01-01 10:30:30";//时间
    private String GPS="106.72,26.57";//经纬度

    /**
     * 给Bitmap添加水印
     * @param bm1
     */
    private void addWaterMask(Bitmap bm1) {
        int height = bm1.getHeight();
        int width = bm1.getWidth();
        Paint p = new Paint();
        p.setTextSize(16);
        p.setColor(Color.RED);
        Canvas canvas = new Canvas(bm1);
        //计算文字位置 居中显示
        String text="神通测试图片";
        long l = System.currentTimeMillis();
        time = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(l));
        Paint.FontMetrics fontMetrics = p.getFontMetrics();
        float top = fontMetrics.bottom-fontMetrics.top;
        canvas.drawText("驾校编号:"+schoolID,0,top,p);
        canvas.drawText("教练员编号:"+teacherID,0,2*top,p);
        canvas.drawText("学员编号:"+studentID,0,3*top,p);
        canvas.drawText(carID,0,height,p);
        canvas.drawText("车速:"+speed+"km/h",0,height-top,p);
        float v = p.measureText(time);
        canvas.drawText(time,width-v,height,p);
        float v1 = p.measureText(GPS);
        canvas.drawText(GPS,width-v1,height-top,p);
        canvas.save(Canvas.ALL_SAVE_FLAG);
    }

    /**
     * 开启预览界面
     */
    @Override
    public void onGlobalLayout() {
        try {
            mCamera.setPreviewDisplay(suffer.getHolder());
            mCamera.startPreview();
        } catch (IOException e) {
        }

    }

    /**
     * 释放资源，在销毁界面时请先调用此方法
     */
    public void release(){
        mCamera.release();
    }

    /**
     * 拍照的接口回调
     */
    public interface OnTakePhoto{
        /**
         * 拍照成功
         * @param path1 第一张图片的地址
         * @param path2 第二张图片的地址
         */
        void onSuccess(String path2);

        /**
         * 异常
         * @param e
         */
        void onError(Exception e);

        /**
         * 拍照时
         */
        void onShutter();
    }
}
