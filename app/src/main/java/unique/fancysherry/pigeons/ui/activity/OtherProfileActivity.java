package unique.fancysherry.pigeons.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import unique.fancysherry.pigeons.R;
import unique.fancysherry.pigeons.account.AccountBean;
import unique.fancysherry.pigeons.account.AccountManager;
import unique.fancysherry.pigeons.io.Constants;
import unique.fancysherry.pigeons.io.SocketIOUtil;
import unique.fancysherry.pigeons.util.LogUtil;
import unique.fancysherry.pigeons.util.config.LocalConfig;
import unique.fancysherry.pigeons.util.config.SApplication;

public class OtherProfileActivity extends ToolbarCastActivity {
    private Activity activity;
    private String sessionid;

    @InjectView(R.id.toolbar_other_profile)
    Toolbar toolbar_other_profile;
    @InjectView(R.id.add_friend_button)
    Button add_friend_button;
    @InjectView(R.id.chat_button)
    Button chat_button;
    private String username;
    private boolean is_me;
    private String session_id;
    private Socket mSocket = SocketIOUtil.getSocket();
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.off(Constants.EVENT_CONTACT_ADD, onAdd);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);
        ButterKnife.inject(this);
        initializeToolbar(toolbar_other_profile);
        session_id=AccountManager.getInstance().sessionid;
        activity = this;
        mSocket.on(Constants.EVENT_CONTACT_ADD, onAdd);
        sessionid = AccountManager.getInstance().sessionid;
        setData();
        initView();
    }

    private void attemptAdd() {
            JSONObject data = new JSONObject();
            try {
                data.put("sessionId", session_id);
                data.put("username", username);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.emit(Constants.EVENT_CONTACT_ADD, data);
    }

    private Emitter.Listener onAdd = new Emitter.Listener() {
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
                            Intent mIntent = new Intent(activity, ChatActivity.class);
                            startActivity(mIntent);
                            finish();
                            Toast.makeText(activity, "now you are friends", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "error contact add", Toast.LENGTH_SHORT).show();
                        }
                        LogUtil.e("end");
                    }
                }
            });
        }
    };


    @Override
    public void setData() {
        username = getIntent().getStringExtra("username");
        if (username.equals(AccountManager.getInstance().getCurrentUser().mAccountBean.username))
            is_me = true;
        else
            is_me = false;
    }


    @Override
    public void initView() {
        if (!is_me) {
            add_friend_button.setVisibility(View.VISIBLE);
            chat_button.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.add_friend_button, R.id.chat_button})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.add_friend_button:
                attemptAdd();
                break;
            case R.id.chat_button:
                Intent mIntent = new Intent(activity, ChatActivity.class);
                mIntent.putExtra("username",username);
                startActivity(mIntent);
                finish();
                break;
        }
    }


}
