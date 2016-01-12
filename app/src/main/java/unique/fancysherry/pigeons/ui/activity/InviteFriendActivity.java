package unique.fancysherry.pigeons.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import io.socket.emitter.Emitter;
import unique.fancysherry.pigeons.R;
import unique.fancysherry.pigeons.account.AccountBean;
import unique.fancysherry.pigeons.account.AccountManager;
import unique.fancysherry.pigeons.io.Constants;
import unique.fancysherry.pigeons.io.model.User;
import unique.fancysherry.pigeons.ui.adapter.SearchAdapter;
import unique.fancysherry.pigeons.ui.adapter.SearchMemberAdapter;
import unique.fancysherry.pigeons.util.LogUtil;

public class InviteFriendActivity extends ToolbarCastActivity {

    @InjectView(R.id.toolbar_invite_friend)
    Toolbar toolbar_invite_friend;
    @InjectView(R.id.search_member_list)
    RecyclerView search_member_list;

    private String sessionid;
    private Activity activity;
    private ArrayList<User> user_list = new ArrayList<>();
    private SearchMemberAdapter searchMemberAdapter;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off(Constants.EVENT_USER_SERACH, onSearch);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);
        ButterKnife.inject(this);
        initializeToolbar(toolbar_invite_friend);
        activity = this;
        mSocket.connect();
        mSocket.on(Constants.EVENT_USER_SERACH, onSearch);
        sessionid = AccountManager.getInstance().sessionid;
        initView();
        setData();
    }


    @Override
    public void initView() {
        initAdapter();
    }

    public void initAdapter() {
        search_member_list.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        searchMemberAdapter = new SearchMemberAdapter(this);
        search_member_list.setAdapter(searchMemberAdapter);
        searchMemberAdapter
                .setOnItemClickListener(new SearchMemberAdapter.OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, User data) {
                        Intent mIntent = new Intent(activity, OtherProfileActivity.class);
                        mIntent.putExtra("username", data.username);
                        startActivity(mIntent);
                    }
                });
    }

    @Override
    public void setData() {

    }


    private void attemptSearch(String search_name) {
        // Store values at the time of the login attempt.
        if (!TextUtils.isEmpty(search_name)) {
            JSONObject data = new JSONObject();
            try {
                data.put("sessionId", sessionid);
                data.put("pattern", search_name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.emit(Constants.EVENT_USER_SERACH, data);
        }
    }

    private Emitter.Listener onSearch = new Emitter.Listener() {
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
                            user_list = new Gson().fromJson(user_array_json.toString(), new TypeToken<List<User>>() {
                            }.getType());
                            searchMemberAdapter.setData(user_list);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_invite_friend, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_search:
                loadToolBarSearch();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    public void loadToolBarSearch() {
        View view = this.getLayoutInflater().inflate(R.layout.view_toolbar_search, null);
        LinearLayout parentToolbarSearch = (LinearLayout) view.findViewById(R.id.parent_toolbar_search);
        ImageView imgToolBack = (ImageView) view.findViewById(R.id.img_tool_back);
        final EditText edtToolSearch = (EditText) view.findViewById(R.id.edt_tool_search);
        ImageView imgToolMic = (ImageView) view.findViewById(R.id.img_tool_mic);
        final ListView listSearch = (ListView) view.findViewById(R.id.list_search);
        final TextView txtEmpty = (TextView) view.findViewById(R.id.txt_empty);

        setListViewHeightBasedOnChildren(listSearch);

        edtToolSearch.setHint("Search User");

        final Dialog toolbarSearchDialog = new Dialog(this, R.style.MaterialSearch);
        toolbarSearchDialog.setContentView(view);
        toolbarSearchDialog.setCancelable(false);
        toolbarSearchDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        toolbarSearchDialog.getWindow().setGravity(Gravity.BOTTOM);
        toolbarSearchDialog.show();

        toolbarSearchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        final ArrayList<String> itemStored = new ArrayList<>();//todo null
//        itemStored.add("a");
//        itemStored.add("ab");
//        itemStored.add("c");
        final SearchAdapter searchAdapter = new SearchAdapter(this, itemStored, false);

        listSearch.setVisibility(View.VISIBLE);
        listSearch.setAdapter(searchAdapter);


        listSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String country = String.valueOf(adapterView.getItemAtPosition(position));
                edtToolSearch.setText(country);
                listSearch.setVisibility(View.GONE);
            }
        });
        edtToolSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                listSearch.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<String> filterList = new ArrayList<String>();
                boolean isNodata = false;
                if (s.length() > 0) {
                    for (int i = 0; i < itemStored.size(); i++) {
                        if (itemStored.get(i).toLowerCase().startsWith(s.toString().trim().toLowerCase())) {
                            filterList.add(itemStored.get(i));
                            listSearch.setVisibility(View.VISIBLE);
                            searchAdapter.updateList(filterList, true);
                            isNodata = true;
                        }
                    }
                    if (!isNodata) {
                        listSearch.setVisibility(View.GONE);
                        txtEmpty.setVisibility(View.VISIBLE);
                        txtEmpty.setText("No data found");
                    }
                } else {
                    listSearch.setVisibility(View.GONE);
                    txtEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        imgToolBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbarSearchDialog.dismiss();
            }
        });

        imgToolMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbarSearchDialog.dismiss();
                attemptSearch(edtToolSearch.getText().toString());
            }
        });
    }


}
