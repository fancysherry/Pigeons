package unique.fancysherry.pigeons.ui.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.socket.client.Socket;
import unique.fancysherry.pigeons.R;
import unique.fancysherry.pigeons.io.SocketIOUtil;
import unique.fancysherry.pigeons.util.config.SApplication;

public class SettingActivity extends ToolbarCastActivity {
    @InjectView(R.id.toolbar_setting)
    Toolbar toolbar_setting;
    private Socket mSocket = SocketIOUtil.getSocket();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.inject(this);
        initializeToolbar(toolbar_setting);
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

}
