package leoseven.com.stcamera;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements STcameraView.OnTakePhoto {
    private STcameraView st;
    private int type=0;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        st= (STcameraView) findViewById(R.id.mCamera);

    }

    @Override
    protected void onResume() {
        super.onResume();
        type =PicService.getType();
        if (type==3) {
            takePhoto();
        }
    }

    private void takePhoto() {
        MediaPlayer player = MediaPlayer.create(this, R.raw.three);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                st.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        st.takePhoto(MainActivity.this);
                    }
                },3000);
            }
        });
        player.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog!=null) {
            dialog.cancel();
        }
    }
    private String action="com.scstjy.piccomlite";
    @Override
    public void onSuccess( String path2) {
        Intent intent = new Intent(action);
        intent.putExtra("state","success");
        intent.putExtra("num",1);
        intent.putExtra("path",path2);
        Log.e("MainActivity","拍照成功"+path2);
        show(path2);
        sendBroadcast(intent);
    }
    private boolean sure=true;
    private void show(String path) {
        sure=true;
        AlertDialog.Builder bd=new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.item_dialog, null, false);
        ImageView right= (ImageView) v.findViewById(R.id.dialog_right);
        ImageView cancle= (ImageView) v.findViewById(R.id.dialog_image_cancle);
        ImageView bg= (ImageView) v.findViewById(R.id.dialog_image);
        bg.setImageBitmap(BitmapFactory.decodeFile(path));
        bd.setView(v);
        dialog = bd.create();
        dialog.show();
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sure=false;
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (sure) {
                    finish();
                }else {
                    takePhoto();
                }
            }
        });
        st.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        },5000);
    }

    @Override
    public void onError(Exception e) {
        Intent intent = new Intent(action);
        Log.e("MainActivity","拍照失败");
        intent.putExtra("state","error");
        sendBroadcast(intent);
        finish();
    }

    @Override
    public void onShutter() {

    }
}
