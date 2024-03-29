package unique.fancysherry.pigeons.ui.activity;


import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import unique.fancysherry.pigeons.R;
import unique.fancysherry.pigeons.account.AccountManager;
import unique.fancysherry.pigeons.io.Constants;
import unique.fancysherry.pigeons.io.SocketIOUtil;
import unique.fancysherry.pigeons.util.LogUtil;

public class RegisterActivity extends ToolbarCastActivity {
    @InjectView(R.id.register_username)
    EditText register_username;
    @InjectView(R.id.register_password)
    EditText register_password;
    @InjectView(R.id.register_button)
    Button register_button;
    private String session_id;
    private Activity activity;
    private Socket mSocket = SocketIOUtil.getSocket();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);
        statusBarColor();
        mSocket.on(Constants.EVENT_REGISTER, onRegister);
        session_id = AccountManager.getInstance().sessionid;
        activity = this;
        initView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.off(Constants.EVENT_REGISTER, onRegister);
    }

    private void attemptRegiter() {
        // Store values at the time of the login attempt.
        String username = register_username.getText().toString();
        String password = register_password.getText().toString();
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            JSONObject data = new JSONObject();
            try {
                data.put("sessionId", session_id);
                data.put("username", username);
                data.put("password", password);
                data.put("nickname", "梁佳林");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.emit(Constants.EVENT_REGISTER, data);
        }
    }

    private Emitter.Listener onRegister = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args[0] == null)
                        Toast.makeText(activity, "failed", Toast.LENGTH_SHORT).show();
                    else {
                        JSONObject data = (JSONObject) args[0];
                        String result;
                        try {
                            result = data.getString("err");
                        } catch (JSONException e) {
                            return;
                        }
                        if (result.equals("null")) {
                            finish();
                            Toast.makeText(activity, "success register", Toast.LENGTH_SHORT).show();
                        } else {
                            finish();
                            Toast.makeText(activity, "error register", Toast.LENGTH_SHORT).show();
                        }
                        LogUtil.e("end");
                    }
                }
            });
        }
    };

    @Override
    public void initView() {
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegiter();
            }
        });
    }

    @Override
    public void initToolbar() {

    }

    @Override
    public void setData() {

    }
}
