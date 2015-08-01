package com.feisty.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.feisty.BuildConfig;
import com.feisty.R;


/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.action_settings));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new FeistyPreferenceFragment())
                    .commit();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
//                overridePendingTransition(R.anim.scale_up, R.anim.slide_down);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        overridePendingTransition(R.anim.scale_up, R.anim.slide_down);
    }


    public static class FeistyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        Preference mDevOptionsContainer;
        Preference mVersion, mRateUs;

        private static final String KEY_DEBUG_MODE = "debug_mode_key";
        int debugModeClicks = 0;


        SharedPreferences mSharedPreferences;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

//            getPreferenceManager().setSharedPreferencesName(getString(R.string._gardens_preferences));
//            getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);

            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            addPreferencesFromResource(R.xml.pref_general);

            mDevOptionsContainer = findPreference("dev_options_category");
            mVersion = findPreference("version");
            mVersion.setSummary(BuildConfig.VERSION_NAME);

            mRateUs = findPreference("rate_us");

            if(!mSharedPreferences.contains(KEY_DEBUG_MODE)){
                getPreferenceScreen().removePreference(mDevOptionsContainer);
            }
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            if (preference.equals(mVersion)){
                debugModeClicks += !mSharedPreferences.contains(KEY_DEBUG_MODE) ? 1 : -1;
                if (debugModeClicks == 0){
                    mSharedPreferences.edit().remove(KEY_DEBUG_MODE).apply();
                    preferenceScreen.removePreference(mDevOptionsContainer);
                } else if (debugModeClicks == 10){
                    Toast.makeText(getActivity(), "Developer mode enabled", Toast.LENGTH_SHORT).show();
                    mSharedPreferences.edit().putBoolean(KEY_DEBUG_MODE, true).apply();
                    preferenceScreen.addPreference(mDevOptionsContainer);
                }
                return true;
            } else if (preference.equals(mRateUs)) {
                final String appPackageName = BuildConfig.APPLICATION_ID;
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                return true;
            } else {
                return super.onPreferenceTreeClick(preferenceScreen, preference);
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        }
    }
}
