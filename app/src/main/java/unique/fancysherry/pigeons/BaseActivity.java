package unique.fancysherry.pigeons;


import android.app.Activity;

/**
 * Created by fancysherry on 15-12-9.
 */
public abstract class BaseActivity extends Activity {
  public abstract void initView();

  public abstract void initToolbar();

  public abstract void setData();

}
