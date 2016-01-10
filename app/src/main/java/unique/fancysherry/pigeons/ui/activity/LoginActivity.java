package unique.fancysherry.pigeons.ui.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import unique.fancysherry.pigeons.R;
import unique.fancysherry.pigeons.account.AccountManager;
import unique.fancysherry.pigeons.io.Constants;
import unique.fancysherry.pigeons.util.LogUtil;

public class LoginActivity extends ToolbarCastActivity {
    @InjectView(R.id.login_username)
    EditText login_username;
    @InjectView(R.id.login_password)
    EditText login_password;
    @InjectView(R.id.login_button)
    Button login_button;

    private String session_id;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        statusBarColor();
        mSocket.on(Constants.EVENT_LOGIN, onLogin);
        mSocket.on(Constants.EVENT_SESSION, onSession);
        mSocket.connect();
        activity = this;
        initView();
    }

    @OnClick({R.id.register_text})
    public void onclick(View view) {
        Intent register_intent = new Intent(this, RegisterActivity.class);
        startActivity(register_intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off(Constants.EVENT_LOGIN, onLogin);
        mSocket.off(Constants.EVENT_SESSION, onSession);
    }

    private void attemptLogin() {
        // Store values at the time of the login attempt.
        String username = login_username.getText().toString();
        String password = login_password.getText().toString();
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            JSONObject data = new JSONObject();
            try {
                data.put("sessionId", session_id);
                data.put("username", username);
                data.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.emit(Constants.EVENT_LOGIN, data);
        }
    }

    private Emitter.Listener onLogin = new Emitter.Listener() {
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
                            Intent mIntent = new Intent(activity, MainActivity.class);
                            startActivity(mIntent);
                            finish();
                            Toast.makeText(activity, "success login", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "error login", Toast.LENGTH_SHORT).show();
                        }
                        LogUtil.e("end");
                    }
                }
            });
        }
    };

    private Emitter.Listener onSession = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args[0] == null)
                        Toast.makeText(activity, "failed", Toast.LENGTH_SHORT).show();
                    else {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            session_id = data.getString("sessionId");
                            AccountManager.getInstance().sessionid = session_id;
                        } catch (JSONException e) {
                            return;
                        }
                        if (session_id != null) {
                            Toast.makeText(activity, session_id, Toast.LENGTH_SHORT).show();
                        }
                        LogUtil.e("end");
                    }
                }
            });
        }
    };


    @Override
    public void initView() {
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
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
