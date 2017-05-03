package leoseven.com.stcamera;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by 10789 on 2017-05-03.
 */

public class CamerHolder implements Camera.PictureCallback, Camera.ShutterCallback {
    private Camera mCamera;
    public static void release(){
        instance.des();
        instance=null;
    }
    public void des(){
        mCamera.release();
    }
    private CamerHolder() {
        int cNum = Camera.getNumberOfCameras();
        for (int i = 0; i < cNum; i++) {
            try {
                mCamera=Camera.open(i);
            }catch (Exception e){
                mCamera=null;
            }
        }
        if (mCamera==null){
            throw new RuntimeException("相机开启失败,请检查相机是否连接正常");
        }
    }
    private static CamerHolder instance;
    public static void init(){
        if (instance==null) {
            instance=new CamerHolder();
        }
   }

    public Camera getmCamera() {
        return mCamera;
    }

    public String getSchoolID() {
        return schoolID;
    }

    public void setSchoolID(String schoolID) {
        this.schoolID = schoolID;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getCarID() {
        return carID;
    }

    public void setCarID(String carID) {
        this.carID = carID;
    }

    public String getGPS() {
        return GPS;
    }

    public void setGPS(String GPS) {
        this.GPS = GPS;
    }

    private String schoolID="123456";//驾校编号
    private String teacherID="654321";//教练编号
    private String studentID="987654";//学员编号
    private String speed="50";//车速
    private String carID="川A 66666";//车牌号
    private String time="2013-01-01 10:30:30";//时间
    private String GPS="106.72,26.57";//经纬度
    public static CamerHolder getInstance() {
        return instance;
    }
    private Context contextl;
    public void takePic(Context context){
        this.contextl=context;
        mCamera.stopPreview();
        mCamera.setAnalogInputState(true, 0,
                Camera.SYSTEM_PAL, Camera.CHANNEL_ALL_2X2, Camera.FORMAT_PL_420);
        mCamera.takePicture(this,this,this);
    }
    private File root=new File(Environment.getExternalStorageDirectory(),"ShenTongPic");
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
    private String action="com.scstjy.piccomlite";
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        if(data!=null){
            BitmapFactory.Options opt = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length,opt);
            Bitmap bm1 = bitmap.createBitmap(bitmap, 0, 0, opt.outWidth/2, opt.outHeight / 2);


            Bitmap bm2=Bitmap.createBitmap(bitmap,opt.outWidth/2,0,opt.outWidth/2,opt.outHeight/2);

            addWaterMask(bm1);
            addWaterMask(bm2);
            UUID uuid = UUID.randomUUID();
            CharSequence picname = uuid.toString().subSequence(0, 6);
            File right = new File(root, picname.toString()+"_one.jpg");
            File left = new File(root, picname.toString() + "_two.jpg");
            if (!root.exists()) {
                root.mkdirs();
            }
            try {
                FileOutputStream fos1 = new FileOutputStream(left);
                FileOutputStream fos2 = new FileOutputStream(right);
                boolean compress = bm1.compress(Bitmap.CompressFormat.JPEG, 75, fos1);
                boolean compress1 = bm2.compress(Bitmap.CompressFormat.JPEG, 75, fos2);
                if (compress&&compress1) {
                    //拍照成功
                    Intent intent = new Intent(action);
                    intent.putExtra("state","success");
                    intent.putExtra("num",2);
                    intent.putExtra("path",left.getAbsolutePath());
                    intent.putExtra("path2",right.getAbsolutePath());
                    Log.e("MainActivity","拍照成功"+left.getAbsolutePath()+"  "+right.getAbsolutePath());
                    contextl.sendBroadcast(intent);
                }
                if (bm1!=null) {
                    bm1.recycle();
                }
                if (bm2!=null) {
                    bm2.recycle();
                }
                if (bitmap!=null) {
                    bitmap.recycle();
                }
                if (fos1!=null) {
                    fos1.close();
                }
                if (fos2!=null){
                    fos2.close();
                }
            } catch (FileNotFoundException e) {

            } catch (IOException e) {

            }
        }else {
            mCamera.setAnalogInputState(true, 0,
                    Camera.SYSTEM_PAL, Camera.CHANNEL_ONLY_1, Camera.FORMAT_PL_420);
            mCamera.startPreview();
        }

    }

    @Override
    public void onShutter() {

    }
}
