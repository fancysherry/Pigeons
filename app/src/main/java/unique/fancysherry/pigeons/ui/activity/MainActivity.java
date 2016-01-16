package unique.fancysherry.pigeons.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import unique.fancysherry.pigeons.R;
import unique.fancysherry.pigeons.io.SocketIOUtil;
import unique.fancysherry.pigeons.ui.fragment.AllFriendFragment;
import unique.fancysherry.pigeons.ui.fragment.AllGroupFragment;
import unique.fancysherry.pigeons.ui.fragment.DrawerFragment;
import unique.fancysherry.pigeons.util.config.LocalConfig;


public class MainActivity extends BaseActivity implements DrawerFragment.NavigationDrawerCallbacks {
    public FragmentManager fragmentManager;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private DrawerFragment mDrawerFragment;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeToolbar();
        activity = this;
        initView();
    }

    @Override
    public void initView() {
        mDrawerFragment = (DrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawerLayout));
        fragmentManager = getSupportFragmentManager();

        // default load ar_me
//        fragmentManager
//                .beginTransaction()
//                .replace(R.id.container,
//                        InboxShareFragment.newInstance())
//                .commit();
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_settings:
                    LocalConfig.setFirstLaunch(true);
                    finish();
                    SocketIOUtil.disconnect();
                    break;
            }
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void setData() {

    }

    @Override
    public void onNavigationDrawerItemSelected(String item_name) {
        if (fragmentManager == null)
            fragmentManager = getSupportFragmentManager();

        if (item_name.equals(DrawerFragment.ITEM_HISTORY)) {

        } else if (item_name.equals(DrawerFragment.ITEM_ALL_FRIEND)) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container,
                            new AllFriendFragment())
                    .commit();
        } else if (item_name.equals(DrawerFragment.ITEM_ALL_GROUP)) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container,
                            new AllGroupFragment())
                    .commit();
        }
    }
}
