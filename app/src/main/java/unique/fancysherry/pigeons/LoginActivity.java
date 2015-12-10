package unique.fancysherry.pigeons;


import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.InjectView;



public class LoginActivity extends BaseActivity {
  @InjectView(R.id.login_username)
  EditText login_username;
  @InjectView(R.id.login_password)
  EditText login_password;
  @InjectView(R.id.login_button)
  EditText login_button;

  private Activity activity;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    ButterKnife.inject(this);
    activity = this;
  }

  @Override
  public void initView() {

  }

  @Override
  public void initToolbar() {

  }

  @Override
  public void setData() {

  }
}
