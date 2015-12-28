package unique.fancysherry.pigeons.ui.activity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.WindowManager;

import unique.fancysherry.pigeons.R;

/**
 * Created by fancysherry on 15-12-22.
 */
public class ToolbarCastActivity extends AppCompatActivity {
    // Resolve the given attribute of the current theme
    private int getAttributeColor(int resId) {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(resId, typedValue, true);
        int color = 0x000000;
        if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT && typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            // resId is a color
            color = typedValue.data;
        } else {
            // resId is not a color
        }
        return color;
    }

    protected void initializeToolbar(Toolbar mToolbar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getAttributeColor(R.attr.colorPrimaryDark));
        }
//        Toolbar mToolbar = (Toolbar) findViewById(R.id.comment_activity_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
//    mToolbar.setOnMenuItemClickListener(onMenuItemClick);

    }

}
