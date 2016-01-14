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
        activity = this;
        mSocket.connect();
        mSocket.on(Constants.EVENT_CHAT, onChat);
        mSocket.on(Constants.EVENT_MESSAGE, onMessage);
        sessionid = AccountManager.getInstance().sessionid;
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
//                data.put("to", search_name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.emit(Constants.EVENT_USER_SERACH, data);
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
                        JSONArray user_array_json;
                        try {
                            err = data.getString("err");
                            user_array_json = data.getJSONArray("users");
                            LogUtil.e(user_array_json.toString());
                        } catch (JSONException e) {
                            return;
                        }
                        if (err.equals("null") && user_array_json.toString() != null) {
//                            searchMemberAdapter.setData(user_list);
                            Toast.makeText(activity, "success search", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "error search", Toast.LENGTH_SHORT).show();
                        }
                        LogUtil.e("search end");
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
                        mMessage.from=from;
                        if (mMessage.from.equals(current_username))
                            mMessage.type = Message.Type.TEXT;
                        else
                            mMessage.type = Message.Type.TEXT_OTHER;
                        messageList.add(mMessage);
                        if (err.equals("null")) {
                            chatAdapter.setData(messageList);
                            Toast.makeText(activity, "success search", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "error search", Toast.LENGTH_SHORT).show();
                        }
                        LogUtil.e("search end");
                    }
                }
            });
        }
    };
}
