package unique.fancysherry.pigeons.util.system;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import unique.fancysherry.pigeons.util.config.LocalConfig;
import unique.fancysherry.pigeons.util.config.SApplication;


/**
 * Created by fancysherry on 15-4-22.
 */
public class ScreenUtils {

  private static int screenHeight = -1;
  private static int screenWidth = -1;

  private ScreenUtils()
  {
    /** cannot be instantiated **/
    throw new UnsupportedOperationException("cannot be instantiated");
  }

  /**
   * 获得屏幕高度
   *
   * @return
   */
  public static int getScreenWidth() {
    if (screenHeight == -1) {
      WindowManager windowManager =
          (WindowManager) SApplication.getAppContext().getSystemService(Context.WINDOW_SERVICE);
      Display display = windowManager.getDefaultDisplay();
      DisplayMetrics metrics = new DisplayMetrics();
      display.getMetrics(metrics);
      screenWidth = metrics.widthPixels;
      screenHeight = metrics.heightPixels;
    }
    return screenWidth;
  }

  /**
   * 获得屏幕宽度
   *
   * @return
   */
  public static int getScreenHeight() {
    if (screenHeight == -1) {
      WindowManager windowManager =
          (WindowManager) SApplication.getAppContext().getSystemService(Context.WINDOW_SERVICE);
      Display display = windowManager.getDefaultDisplay();
      DisplayMetrics metrics = new DisplayMetrics();
      display.getMetrics(metrics);
      screenWidth = metrics.widthPixels;
      screenHeight = metrics.heightPixels;
    }
    return screenHeight;
  }

  /**
   * 获得状态栏的高度
   *
   * @return
   */
  public static int getStatusBarHeight() {
    return Resources.getSystem().getDimensionPixelSize(
        Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));
  }


  /**
   * 获得actionbar的高度
   *
   * @return
   */
  public static int getNavigationBarHeight() {
    return Resources.getSystem().getDimensionPixelSize(
        Resources.getSystem().getIdentifier("navigation_bar_height", "dimen", "android"));
  }



  /**
   * 获取当前屏幕截图，包含状态栏
   *
   * @param activity
   * @return
   */
  public static Bitmap snapShotWithStatusBar(Activity activity)
  {
    View view = activity.getWindow().getDecorView();
    view.setDrawingCacheEnabled(true);
    view.buildDrawingCache();
    Bitmap bmp = view.getDrawingCache();
    int width = getScreenWidth();
    int height = getScreenHeight();
    Bitmap bp = null;
    bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
    view.destroyDrawingCache();
    return bp;

  }

  /**
   * 获取当前屏幕截图，不包含状态栏
   *
   * @param activity
   * @return
   */
  public static Bitmap snapShotWithoutStatusBar(Activity activity)
  {
    View view = activity.getWindow().getDecorView();
    view.setDrawingCacheEnabled(true);
    view.buildDrawingCache();
    Bitmap bmp = view.getDrawingCache();
    Rect frame = new Rect();
    activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
    int statusBarHeight = frame.top;

    int width = getScreenWidth();
    int height = getScreenHeight();
    Bitmap bp = null;
    bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
        - statusBarHeight);
    view.destroyDrawingCache();
    return bp;

  }

  /**可见屏幕高度**/
  public static int getAppHeight(Activity paramActivity) {
    Rect localRect = new Rect();
    paramActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
    return localRect.height();
  }

  public static int getKeyboardHeight(Activity paramActivity) {
    int height = getScreenHeight() - getStatusBarHeight()
            - getAppHeight(paramActivity);
    if (height == 0) {
      height = LocalConfig.getIntShareData("KeyboardHeight", 700);//787为默认软键盘高度 基本差不离
    }else{
      LocalConfig.putIntShareData("KeyboardHeight", height);
    }
    return height;
  }

  // below actionbar, above softkeyboard
  public static int getAppContentHeight(Activity paramActivity) {
    return getScreenHeight() - getStatusBarHeight()
            - getNavigationBarHeight() - getKeyboardHeight(paramActivity);
  }


  /**键盘是否在显示**/
  public static boolean isKeyBoardShow(Activity paramActivity) {
    int height = getScreenHeight() - getStatusBarHeight()
            - getAppHeight(paramActivity);
    return height != 0;
  }
}
