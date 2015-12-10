package unique.fancysherry.pigeons.util.config;


import android.app.Application;
import android.content.Context;
import com.facebook.drawee.backends.pipeline.Fresco;
import java.io.File;


/**
 * Created by suanmiao on 14-10-31.
 */
public class SApplication extends Application {
  public static Context context;


  public static final String appFolderName = "shr";

  @Override
  public void onCreate() {
    super.onCreate();
    init();
  }

  private void init() {
    context = getApplicationContext();
    Fresco.initialize(this);
  }

  public static Context getAppContext() {
    return context;
  }



}
