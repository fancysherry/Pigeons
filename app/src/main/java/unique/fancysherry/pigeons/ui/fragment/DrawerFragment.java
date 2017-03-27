package unique.fancysherry.pigeons.ui.fragment;

import android.content.Intent;
import android.app.Activity;
import android.net.Uri;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;


import butterknife.ButterKnife;

import butterknife.InjectView;
import butterknife.OnClick;
import io.socket.client.Socket;
import unique.fancysherry.pigeons.R;
import unique.fancysherry.pigeons.account.AccountManager;
import unique.fancysherry.pigeons.io.Constants;
import unique.fancysherry.pigeons.io.SocketIOUtil;
import unique.fancysherry.pigeons.ui.activity.InviteFriendActivity;
import unique.fancysherry.pigeons.ui.activity.NewGroupActivity;
import unique.fancysherry.pigeons.ui.activity.ProfileActivity;
import unique.fancysherry.pigeons.ui.activity.SettingActivity;


/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a
 * href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class DrawerFragment extends BaseFragment {
    public static final String ITEM_HISTORY = "item_history";
    public static final String ITEM_ALL_FRIEND = "item_all_friend";
    public static final String ITEM_ALL_GROUP = "item_all_group";
    public static final String ITEM_INVITE_FRIEND = "item_invite_friend";
    public static final String ITEM_NEW_GROUP = "item_new_group";
    public static final String ITEM_SETTING = "item_setting";
    public static final String ITEM_PROFILE = "item_profile";
    private Socket mSocket = SocketIOUtil.getSocket();
    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    @InjectView(R.id.drawer_header_name)
    TextView nickname;
    @InjectView(R.id.drawer_header_portrait)
    SimpleDraweeView portrait;
    @InjectView(R.id.drawer_history)
    LinearLayout drawer_history;
    @InjectView(R.id.drawer_all_friend)
    LinearLayout drawer_all_friend;
    @InjectView(R.id.drawer_all_group)
    LinearLayout drawer_all_group;
    @InjectView(R.id.drawer_invite_friend)
    LinearLayout drawer_invite_friend;
    @InjectView(R.id.drawer_new_group)
    LinearLayout drawer_new_group;

    @InjectView(R.id.drawer_setting)
    LinearLayout drawer_setting;
    @InjectView(R.id.drawer_profile)
    LinearLayout drawer_profile;


    private DrawerLayout mDrawerLayout;
    private View mFragmentContainerView;

    public DrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.navigation_drawer, container, false);
        ButterKnife.inject(this, view);
        String username = AccountManager.getInstance().getCurrentUser().getAccountBean().username;
        nickname.setText(username);
        portrait.setImageURI(Uri.parse(Constants.BASE_URL + "avatar/" + username));
        return view;
    }

    @OnClick(R.id.drawer_header_portrait)
    public void profile(View view) {
        closeDrawerLayout();
        Intent mIntent = new Intent(getActivity(), ProfileActivity.class);
        startActivity(mIntent);
    }

    @OnClick({R.id.drawer_history,
            R.id.drawer_all_friend,
            R.id.drawer_all_group,
            R.id.drawer_invite_friend,
            R.id.drawer_new_group,
            R.id.drawer_setting,
            R.id.drawer_profile})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.drawer_history:
                selectItem(ITEM_HISTORY);
                break;
            case R.id.drawer_all_friend:
                selectItem(ITEM_ALL_FRIEND);
                break;
            case R.id.drawer_all_group:
                selectItem(ITEM_ALL_GROUP);
                break;
            case R.id.drawer_invite_friend:
                closeDrawerLayout();
                Intent intent_invite = new Intent(getActivity(), InviteFriendActivity.class);
                startActivity(intent_invite);
                break;
            case R.id.drawer_new_group:
                Intent intent_new_group = new Intent(getActivity(), NewGroupActivity.class);
                startActivity(intent_new_group);
                break;
            case R.id.drawer_setting:
                closeDrawerLayout();
                Intent intent_setting = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent_setting);
                break;
            case R.id.drawer_profile:
                closeDrawerLayout();
                Intent intent_profile = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent_profile);
                break;
        }
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerLayout.setStatusBarBackground(R.color.drawer_header_background);
        // set up the drawer's list view with items and click listener
    }


    private void selectItem(String item_name) {
        closeDrawerLayout();
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(item_name);
        }
    }

    private void closeDrawerLayout() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(String item_name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
