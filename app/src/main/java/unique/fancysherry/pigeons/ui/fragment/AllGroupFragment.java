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
import unique.fancysherry.pigeons.ui.activity.ChatActivity;
import unique.fancysherry.pigeons.ui.activity.OtherProfileActivity;
import unique.fancysherry.pigeons.ui.adapter.AllGroupAdapter;
import unique.fancysherry.pigeons.ui.adapter.SearchMemberAdapter;
import unique.fancysherry.pigeons.util.LogUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllGroupFragment extends Fragment {
    private Socket mSocket = SocketIOUtil.getSocket();
    private AllGroupAdapter allGroupAdapter;
    private List<Group> group_list = new ArrayList<>();
    @InjectView(R.id.all_group_list)
    RecyclerView all_group_list;

    public AllGroupFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_all_group, container, false);
        ButterKnife.inject(this, mView);
        initView();
        mSocket.on(Constants.EVENT_ALL_GROUP, onGroup);
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
        mSocket.emit(Constants.EVENT_ALL_GROUP, data);
    }

    public void initView() {
        initAdapter();
    }

    public void initAdapter() {
        all_group_list.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        allGroupAdapter = new AllGroupAdapter(getActivity());
        all_group_list.setAdapter(allGroupAdapter);
        allGroupAdapter
                .setOnItemClickListener(new AllGroupAdapter.OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, Group data) {
                        Intent mIntent = new Intent(getActivity(), ChatActivity.class);
                        mIntent.putExtra("gid", data.gid);
                        startActivity(mIntent);
                    }
                });
    }


    private Emitter.Listener onGroup = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
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
                            user_array_json = data.getJSONArray("groups");
                        } catch (JSONException e) {
                            return;
                        }
                        if (err.equals("null") && user_array_json.toString() != null) {
                            group_list = new Gson().fromJson(user_array_json.toString(), new TypeToken<List<Group>>() {
                            }.getType());
                            allGroupAdapter.setData(group_list);
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
        mSocket.off(Constants.EVENT_ALL_GROUP, onGroup);
    }


}
