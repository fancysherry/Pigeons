package unique.fancysherry.pigeons.ui.activity;

import android.os.Bundle;

import unique.fancysherry.pigeons.R;

/**
 * Created by fancysherry on 15-12-22.
 */
public class ProfileActivity extends ToolbarCastActivity {
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
