package leoseven.com.stcamera;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by 10789 on 2017-05-03.
 */

public class PicService extends Service {
    private PicBroadCast cast;
    private static int type=-1;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private CamerHolder holder;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("PicService","开启服务");
        holder=CamerHolder.getInstance();
        IntentFilter fl = new IntentFilter(action);
        cast = new PicBroadCast();
        registerReceiver(cast,fl);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(cast);
    }

    public static int getType() {
        return type;
    }

    private String action="com.scstjy.pic";
    private String action2="com.scstjy.piccomlite";
    private String action3="com.scstjy.picfinish";
    class PicBroadCast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("PicService",intent.getAction());

            if (intent.getAction().equals(action)) {
                String cmd = intent.getStringExtra("cmd");
                Log.e("PicService","cmd:  "+cmd);
                if (cmd.equals("login")||cmd.equals("logout")) {
                    Intent it = new Intent(PicService.this, MainActivity.class);
                    String schoolID = intent.getStringExtra("schoolID");
                    String teacherID = intent.getStringExtra("teacherID");
                    String speed = intent.getStringExtra("speed");
                    String carID = intent.getStringExtra("carID");
                    String gps = intent.getStringExtra("GPS");
                    Log.e("PicService",schoolID+"   "+teacherID+"    "+speed);
                    holder.setCarID(carID);
                    holder.setGP(gps);
                    holder.setSchoolID(schoolID);
                    holder.setTeacherID(teacherID);
                    holder.setSpeed(speed);
                    type=3;
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(it);
                }else if (cmd.equals("random")){
                    type=0;
                    String schoolID = intent.getStringExtra("schoolID");
                    String teacherID = intent.getStringExtra("teacherID");
                    String speed = intent.getStringExtra("speed");
                    String carID = intent.getStringExtra("carID");
                    String gps = intent.getStringExtra("GPS");
                    holder.setCarID(carID);
                    holder.setGPS(gps);
                    holder.setSchoolID(schoolID);
                    holder.setTeacherID(teacherID);
                    holder.setSpeed(speed);
                    holder.takePic(PicService.this);
                }
            }
        }
    }
}
