package unique.fancysherry.pigeons.ui.activity;


import android.support.v7.app.AppCompatActivity;

/**
 * Created by fancysherry on 15-12-9.
 */
import android.app.ActivityOptions;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import unique.fancysherry.pigeons.R;
import unique.fancysherry.pigeons.util.LogUtil;
import unique.fancysherry.pigeons.util.system.ResourceHelper;

/**
 * Abstract activity with toolbar, navigation drawer and cast support. Needs to be extended by
 * any activity that wants to be shown as a top level activity.
 *
 * The requirements for a subclass is to call {@link #initializeToolbar()} on onCreate, after
 * setContentView() is called and have three mandatory layout elements:
 * a {@link Toolbar} with id 'toolbar',
 * a {@link DrawerLayout} with id 'drawerLayout' and
 * a {@link ListView} with id 'drawerList'.
 * 在抽象类中抽离出了toolbar和drawertoggle的逻辑
 */
public abstract class BaseActivity extends AppCompatActivity
{
  public Toolbar mToolbar;
  private ActionBarDrawerToggle mDrawerToggle;
  private DrawerLayout mDrawerLayout;

  private boolean mToolbarInitialized;

  private int mItemToOpenWhenDrawerCloses = -1;

  public abstract void initView();


  public abstract void setData();

  private DrawerLayout.DrawerListener mDrawerListener = new DrawerLayout.DrawerListener() {
    @Override
    public void onDrawerClosed(View drawerView) {
      LogUtil.e("drawer close");
      if (mDrawerToggle != null) mDrawerToggle.onDrawerClosed(drawerView);
      int position = mItemToOpenWhenDrawerCloses;
      if (position >= 0) {
        Bundle extras = ActivityOptions.makeCustomAnimation(
                BaseActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();

      }
    }

    @Override
    public void onDrawerStateChanged(int newState) {
      if (mDrawerToggle != null) mDrawerToggle.onDrawerStateChanged(newState);
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
      if (mDrawerToggle != null) mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
    }

    @Override
    public void onDrawerOpened(View drawerView) {
      LogUtil.e("drawer open");
    }
  };

  private FragmentManager.OnBackStackChangedListener mBackStackChangedListener =
          new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
              updateDrawerToggle();
            }
          };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    LogUtil.d("Activity onCreate");

  }

  @Override
  protected void onStart() {
    super.onStart();
    if (!mToolbarInitialized) {
      throw new IllegalStateException("You must run super.initializeToolbar at " +
              "the end of your onCreate method");
    }
  }


  // 使默认的图标变为三条杆
  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    if (mDrawerToggle != null) {
      mDrawerToggle.syncState();
    }
  }

  @Override
  public void onResume() {
    super.onResume();

    // Whenever the fragment back stack changes, we may need to update the
    // action bar toggle: only top level screens show the hamburger-like icon, inner
    // screens - either Activities or fragments - show the "Up" icon instead.
    getFragmentManager().addOnBackStackChangedListener(mBackStackChangedListener);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (mDrawerToggle != null) {
      mDrawerToggle.onConfigurationChanged(newConfig);
    }
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
      return true;
    }
    // If not handled by drawerToggle, home needs to be handled by returning to previous
    if (item != null && item.getItemId() == android.R.id.home) {
      onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onPause() {
    super.onPause();
    getFragmentManager().removeOnBackStackChangedListener(mBackStackChangedListener);
  }


  @Override
  public void onBackPressed() {
    // If the drawer is open, back will close it
    if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
      mDrawerLayout.closeDrawers();
      return;
    }
    // Otherwise, it may return to the previous fragment stack
    FragmentManager fragmentManager = getFragmentManager();
    if (fragmentManager.getBackStackEntryCount() > 0) {
      fragmentManager.popBackStack();
    } else {
      // Lastly, it will rely on the system behavior for back
      super.onBackPressed();
    }
  }

  @Override
  public void setTitle(CharSequence title) {
    super.setTitle(title);
    mToolbar.setTitle(title);
  }

  protected void initializeToolbar() {
    mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
    if (mToolbar == null) {
      throw new IllegalStateException("Layout is required to include a Toolbar with id " +
              "'toolbar'");
    }

    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
    if (mDrawerLayout != null) {

      // Create an ActionBarDrawerToggle that will handle opening/closing of the drawer:
      mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
              mToolbar, R.string.open_content_drawer, R.string.close_content_drawer);
      mDrawerLayout.setDrawerListener(mDrawerListener);
      mDrawerLayout.setStatusBarBackgroundColor(
              ResourceHelper.getThemeColor(this, R.attr.colorPrimary, android.R.color.black));
      setSupportActionBar(mToolbar);
      updateDrawerToggle();
    }
    else {
      setSupportActionBar(mToolbar);
    }

    mToolbarInitialized = true;
  }



  protected void updateDrawerToggle() {
    if (mDrawerToggle == null) {
      return;
    }
    boolean isRoot = getFragmentManager().getBackStackEntryCount() == 0;
    mDrawerToggle.setDrawerIndicatorEnabled(isRoot);
    getSupportActionBar().setDisplayShowHomeEnabled(!isRoot);
    getSupportActionBar().setDisplayHomeAsUpEnabled(!isRoot);
    getSupportActionBar().setHomeButtonEnabled(!isRoot);
    if (isRoot) {
      mDrawerToggle.syncState();
    }
  }


}
