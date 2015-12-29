package unique.fancysherry.pigeons.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

import unique.fancysherry.pigeons.R;
import unique.fancysherry.pigeons.ui.fragment.DrawerFragment;


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
        activity=this;
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

    @Override
    public void setData() {

    }

    @Override
    public void onNavigationDrawerItemSelected(String item_name) {
        if (fragmentManager == null)
            fragmentManager = getSupportFragmentManager();

        if (item_name.equals(DrawerFragment.ITEM_HISTORY)) {

        } else if (item_name.equals(DrawerFragment.ITEM_ALL_FRIEND)) {

        } else if (item_name.equals(DrawerFragment.ITEM_ALL_GROUP)) {

        }
    }
}
