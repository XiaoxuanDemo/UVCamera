package leoseven.com.stcamera;

import android.app.Application;
import android.hardware.Camera;

/**
 * Created by 10789 on 2017-05-03.
 */

public class MyApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        CamerHolder.init();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        CamerHolder.release();
    }
}
