package unique.fancysherry.pigeons.ui.activity;

import android.os.Bundle;

import io.socket.client.Socket;
import unique.fancysherry.pigeons.R;
import unique.fancysherry.pigeons.io.SocketIOUtil;
import unique.fancysherry.pigeons.util.config.SApplication;

/**
 * Created by fancysherry on 15-12-22.
 */
public class ProfileActivity extends ToolbarCastActivity {
    private Socket mSocket = SocketIOUtil.getSocket();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        // Show the Up button in the action bar.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
