package unique.fancysherry.pigeons.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import unique.fancysherry.pigeons.R;
import unique.fancysherry.pigeons.account.AccountManager;
import unique.fancysherry.pigeons.io.Constants;
import unique.fancysherry.pigeons.io.SocketIOUtil;
import unique.fancysherry.pigeons.io.model.User;
import unique.fancysherry.pigeons.ui.adapter.AllContactAdapter;
import unique.fancysherry.pigeons.util.LogUtil;

public class NewGroupActivity extends ToolbarCastActivity {
    private Socket mSocket = SocketIOUtil.getSocket();
    private AllContactAdapter allContactAdapter;
    private List<User> user_list = new ArrayList<>();
    private Activity activity;
    private List<String> group_username_list = new ArrayList<>();
    @InjectView(R.id.select_group_member)
    TextView select_group_member;
    @InjectView(R.id.toolbar_new_group)
    Toolbar toolbar_new_group;
    @InjectView(R.id.create_group_name)
    EditText create_group_name;
    @InjectView(R.id.prepare_group_all_member_list)
    RecyclerView prepare_group_all_member_list;
    @InjectView(R.id.create_group_verify)
    ImageView create_group_verify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        activity = this;
        ButterKnife.inject(this);
        initializeToolbar(toolbar_new_group);
        initView();
        LogUtil.e("SOCKET ON");
        mSocket.on(Constants.EVENT_ALL_FRIEND, onContact);
        mSocket.on("group.add", onAddGroup);
        attemptAll();
        create_group_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptCreate();
            }
        });
    }


    private void attemptAll() {
        String sessionid = AccountManager.getInstance().sessionid;
        JSONObject data = new JSONObject();
        try {
            data.put("sessionId", sessionid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit(Constants.EVENT_ALL_FRIEND, data);
    }

    private void attemptCreate() {
        String sessionid = AccountManager.getInstance().sessionid;
        JSONObject data = new JSONObject();
        try {
            data.put("sessionId", sessionid);
            data.put("groupname", create_group_name.getText().toString());
            data.put("members", new JSONArray(group_username_list));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("group.add", data);
    }


    public void initView() {
        initAdapter();
    }

    public void initAdapter() {
        prepare_group_all_member_list.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        allContactAdapter = new AllContactAdapter(this);
        prepare_group_all_member_list.setAdapter(allContactAdapter);
        allContactAdapter
                .setOnItemClickListener(new AllContactAdapter.OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, User data) {
                        if (group_username_list.contains(data.username))
                            group_username_list.add(data.username);
                        else
                            group_username_list.remove(data.username);
                        showGroupMember();
                    }
                });
    }

    private void showGroupMember() {
        String group_username_list_string = "";
        for (String username : group_username_list) {
            if (username != null) {
                if ((!username.equals("")))
                    group_username_list_string = group_username_list_string + username + "  ";
            }
        }
        select_group_member.setText(group_username_list_string);
    }

    private Emitter.Listener onAddGroup = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            LogUtil.e("search start");
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
                            user_array_json = data.getJSONArray("contacts");
                        } catch (JSONException e) {
                            return;
                        }
                        if (err.equals("null") && user_array_json.toString() != null) {
                            user_list = new Gson().fromJson(user_array_json.toString(), new TypeToken<List<User>>() {
                            }.getType());
                            allContactAdapter.setData(user_list);
                        } else {
                        }
                        LogUtil.e("search end");
                    }
                }
            });
        }
    };

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

    private Emitter.Listener onContact = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            LogUtil.e("search start");
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
                            user_array_json = data.getJSONArray("contacts");
                        } catch (JSONException e) {
                            return;
                        }
                        if (err.equals("null") && user_array_json.toString() != null) {
                            user_list = new Gson().fromJson(user_array_json.toString(), new TypeToken<List<User>>() {
                            }.getType());
                            allContactAdapter.setData(user_list);
                        } else {
                        }
                        LogUtil.e("search end");
                    }
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.off(Constants.EVENT_ALL_FRIEND, onContact);
        mSocket.off("group.add", onAddGroup);
    }


}
