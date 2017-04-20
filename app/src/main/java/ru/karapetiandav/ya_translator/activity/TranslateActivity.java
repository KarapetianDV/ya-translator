package ru.karapetiandav.ya_translator.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import ru.karapetiandav.ya_translator.R;
import ru.karapetiandav.ya_translator.fragments.ItemClickInterface;
import ru.karapetiandav.ya_translator.fragments.PagerFragment;
import ru.karapetiandav.ya_translator.fragments.PreferencesFragment;
import ru.karapetiandav.ya_translator.fragments.TranslateFragment;

public class TranslateActivity extends AppCompatActivity implements ItemClickInterface {

    private static final String TAG = TranslateActivity.class.getSimpleName();

    private FragmentManager manager = getSupportFragmentManager();
    private TranslateFragment translateFragment = new TranslateFragment();
    private PagerFragment pagerFragment = new PagerFragment();
    private PreferencesFragment prefFragment = new PreferencesFragment();
    private BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentTransaction transaction = manager.beginTransaction();

            // я так понял что фрагмент не сохраняет состояние из-за метода replace
            // попробовал это исправить с помощью transaction.hide и show
            // вышло не очень, как и другие способы
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (!translateFragment.isAdded()) {
                        transaction.replace(
                                R.id.container,
                                translateFragment)
                                .commit();
                    }
                    return true;
                case R.id.navigation_dashboard:
                    if (!pagerFragment.isAdded()) {
                        transaction.replace(
                                R.id.container,
                                pagerFragment)
                                .commit();
                    }
                    return true;
                case R.id.navigation_notifications:
                    if(!prefFragment.isAdded()) {
                        transaction.replace(
                                R.id.container,
                                prefFragment)
                                .commit();
                    }
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if(manager.findFragmentById(R.id.container) == null)
            manager.beginTransaction().replace(R.id.container, translateFragment).commit();
    }

    @Override
    public void clicked(TranslateFragment fragment) {
        manager
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        navigation.getMenu().getItem(0).setChecked(true);
    }
}