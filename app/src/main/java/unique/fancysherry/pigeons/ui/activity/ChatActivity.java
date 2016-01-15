package unique.fancysherry.pigeons.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.socket.emitter.Emitter;
import unique.fancysherry.pigeons.R;
import unique.fancysherry.pigeons.account.AccountManager;
import unique.fancysherry.pigeons.io.Constants;
import unique.fancysherry.pigeons.io.model.Message;
import unique.fancysherry.pigeons.io.model.User;
import unique.fancysherry.pigeons.ui.adapter.ChatAdapter;
import unique.fancysherry.pigeons.ui.adapter.SearchMemberAdapter;
import unique.fancysherry.pigeons.util.LogUtil;


public class ChatActivity extends ToolbarCastActivity {
    @InjectView(R.id.chat_toolbar)
    Toolbar chat_toolbar;
    @InjectView(R.id.chat_list)
    RecyclerView chat_list;

    private String current_chat_username;
    private Message current_send_message;
    private ChatAdapter chatAdapter;
    private Activity activity;
    private String sessionid;
    private List<Message> messageList = new ArrayList<>();
    private String current_username = AccountManager.getInstance().getCurrentUser().getAccountBean().username;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off(Constants.EVENT_CHAT, onChat);
        mSocket.off(Constants.EVENT_MESSAGE, onMessage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.inject(this);
        initializeToolbar(chat_toolbar);
        current_chat_username = getIntent().getStringExtra("username");
        activity = this;
        sessionid = AccountManager.getInstance().sessionid;
        mSocket.connect();
        mSocket.on(Constants.EVENT_CHAT, onChat);
        mSocket.on(Constants.EVENT_MESSAGE, onMessage);
        initView();
        setData();
    }

    @Override
    public void initView() {
        initAdapter();
    }

    public void initAdapter() {
        chat_list.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        chatAdapter = new ChatAdapter(this);
        chat_list.setAdapter(chatAdapter);

    }

    @Override
    public void initToolbar() {

    }

    @Override
    public void setData() {

    }


    private void attemptSend(String message) {
        // Store values at the time of the login attempt.
        if (!TextUtils.isEmpty(message)) {
            JSONObject data = new JSONObject();
            try {
                data.put("sessionId", sessionid);
                data.put("to", current_chat_username);
                data.put("message", message);
                current_send_message.from = current_username;
                current_send_message.to = current_chat_username;
                current_send_message.message = message;
                current_send_message.type = Message.Type.TEXT;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.emit(Constants.EVENT_CHAT, data);
        }
    }

    private Emitter.Listener onChat = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args[0] == null)
                        Toast.makeText(activity, "failed", Toast.LENGTH_SHORT).show();
                    else {
                        JSONObject data = (JSONObject) args[0];
                        String err;
                        try {
                            err = data.getString("err");
                        } catch (JSONException e) {
                            return;
                        }
                        if (err.equals("null")) {
                            messageList.add(current_send_message);
                            chatAdapter.setData(messageList);
                            Toast.makeText(activity, "success search", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "error search", Toast.LENGTH_SHORT).show();
                        }
                        LogUtil.e("chat end");
                    }
                }
            });
        }
    };

    private Emitter.Listener onMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args[0] == null)
                        Toast.makeText(activity, "failed", Toast.LENGTH_SHORT).show();
                    else {
                        JSONObject data = (JSONObject) args[0];
                        String err;
                        String from;
                        String message;
                        try {
                            err = data.getString("err");
                            from = data.getString("from");
                            message = data.getString("message");
                        } catch (JSONException e) {
                            return;
                        }
                        Message mMessage = new Message();
                        mMessage.message = message;
                        mMessage.from = from;
                        mMessage.type = Message.Type.TEXT_OTHER;
                        messageList.add(mMessage);
                        if (err.equals("null")) {
                            chatAdapter.setData(messageList);
                            Toast.makeText(activity, "success search", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "error search", Toast.LENGTH_SHORT).show();
                        }
                        LogUtil.e("message end");
                    }
                }
            });
        }
    };
}
