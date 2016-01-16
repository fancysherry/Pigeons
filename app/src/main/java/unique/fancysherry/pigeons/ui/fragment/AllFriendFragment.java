package unique.fancysherry.pigeons.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import unique.fancysherry.pigeons.io.model.Group;
import unique.fancysherry.pigeons.io.model.User;
import unique.fancysherry.pigeons.ui.activity.OtherProfileActivity;
import unique.fancysherry.pigeons.ui.adapter.AllContactAdapter;
import unique.fancysherry.pigeons.ui.adapter.AllGroupAdapter;
import unique.fancysherry.pigeons.util.LogUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllFriendFragment extends Fragment {
    private Socket mSocket = SocketIOUtil.getSocket();
    private AllContactAdapter allContactAdapter;
    private List<User> user_list = new ArrayList<>();
    @InjectView(R.id.all_friend_list)
    RecyclerView all_friend_list;

    public AllFriendFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_all_friend, container, false);
        ButterKnife.inject(this, mView);
        initView();
        LogUtil.e("SOCKET ON");
        mSocket.on(Constants.EVENT_ALL_FRIEND, onContact);
        attemptAll();
        return mView;
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

    public void initView() {
        initAdapter();
    }

    public void initAdapter() {
        all_friend_list.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        allContactAdapter = new AllContactAdapter(getActivity());
        all_friend_list.setAdapter(allContactAdapter);
        allContactAdapter
                .setOnItemClickListener(new AllContactAdapter.OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, User data) {
                        Intent mIntent = new Intent(getActivity(), OtherProfileActivity.class);
                        mIntent.putExtra("username", data.username);
                        startActivity(mIntent);
                    }
                });
    }


    private Emitter.Listener onContact = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            LogUtil.e("search start");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args[0] == null)
                        Toast.makeText(getActivity(), "failed", Toast.LENGTH_SHORT).show();
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
    public void onDetach() {
        super.onDetach();
        mSocket.off(Constants.EVENT_ALL_FRIEND, onContact);
    }

}
