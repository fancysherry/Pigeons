package unique.fancysherry.pigeons.ui.activity;


import android.support.v7.app.AppCompatActivity;

/**
 * Created by fancysherry on 15-12-9.
 */
public abstract class BaseActivity extends AppCompatActivity {
  public abstract void initView();

  public abstract void initToolbar();

  public abstract void setData();

}
