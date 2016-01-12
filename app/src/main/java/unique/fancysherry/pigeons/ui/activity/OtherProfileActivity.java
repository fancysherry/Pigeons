package unique.fancysherry.pigeons.ui.activity;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.ButterKnife;
import butterknife.InjectView;
import unique.fancysherry.pigeons.R;
import unique.fancysherry.pigeons.account.AccountManager;
import unique.fancysherry.pigeons.io.Constants;

public class OtherProfileActivity extends ToolbarCastActivity {
    private Activity activity;
    private String sessionid;

    @InjectView(R.id.toolbar_other_profile)
    Toolbar toolbar_other_profile;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
//        mSocket.off(Constants.EVENT_USER_SERACH, onSearch);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);
        ButterKnife.inject(this);
        initializeToolbar(toolbar_other_profile);
        activity = this;
        mSocket.connect();
//        mSocket.on(Constants.EVENT_USER_SERACH, onSearch);
        sessionid = AccountManager.getInstance().sessionid;
        initView();
        setData();
    }


}
