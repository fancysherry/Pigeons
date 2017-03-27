package unique.fancysherry.pigeons.util.config;


import android.app.Application;
import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.facebook.drawee.backends.pipeline.Fresco;

import unique.fancysherry.pigeons.io.SocketIOUtil;


public class PigeonsApplication extends Application {
    public static Context context;

    public static final String appFolderName = "shr";

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
//        SocketIOUtil.disconnect();
        ActiveAndroid.dispose();
    }

    private void init() {
        context = getApplicationContext();
        Fresco.initialize(this);
        ActiveAndroid.initialize(this);
    }

    public static Context getAppContext() {
        return context;
    }


}
