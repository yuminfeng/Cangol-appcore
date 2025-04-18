package mobi.cangol.mobile.appcore.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.appcore.demo.R;
import mobi.cangol.mobile.logging.Log;


public class DynamicActivity extends AppCompatActivity {
    private static final String TAG = "DynamicActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleIntent(getIntent());
        ((CoreApplication) getApplication()).addActivityToManager(this);
        this.setActionBar(new Toolbar(this));
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onDestroy() {
        ((CoreApplication) getApplication()).delActivityFromManager(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void handleIntent(Intent intent) {
        String className = intent.getStringExtra("class");
        Bundle bundle = intent.getBundleExtra("args");
        try {
            toFragment((Class<? extends Fragment>) Class.forName(className), bundle, false);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onNavigateUp() {
        FragmentManager fm = this.getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 1) {
            fm.popBackStack();
            return true;
        } else {
            finish();
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = this.getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 1) {
            fm.popBackStack();
        } else {
            finish();
        }
    }

    public void toFragment(Class<? extends Fragment> fragmentClass, Bundle bundle, boolean newStack) {
        Log.i("fragment ");
        if (newStack) {
            Intent intent = new Intent(this, DynamicActivity.class);
            intent.putExtra("class", fragmentClass.getName());
            intent.putExtra("args", bundle);
            startActivity(intent);
        } else {
            FragmentManager fm = this.getSupportFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.framelayout, Fragment.instantiate(this, fragmentClass.getName(), bundle))
                    .addToBackStack(fragmentClass.getName())
                    .commit();
        }
    }

}
