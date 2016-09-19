package com.mojoteahouse.mojotea.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.mojoteahouse.mojotea.R;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout navigationDrawer;
    private ActionBarDrawerToggle navigationDrawerToggle;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(R.string.nav_menu_title);
        }

        navigationDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationDrawerToggle = new ActionBarDrawerToggle(this, navigationDrawer, toolbar,
                R.string.action_open_navigation_drawer, R.string.action_close_navigation_drawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (navigationView.getMenu().findItem(R.id.nav_orders).isChecked()) {

                } else if (navigationView.getMenu().findItem(R.id.nav_settings).isChecked()) {
                    SettingsActivity.start(MainActivity.this);
                }
                navigationView.setCheckedItem(R.id.nav_menu);
            }
        };
        navigationDrawer.addDrawerListener(navigationDrawerToggle);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_menu);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        navigationDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        navigationDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (navigationDrawer.isDrawerOpen(GravityCompat.START)) {
            navigationDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        // Handle navigation view item clicks here.
        navigationView.setCheckedItem(menuItem.getItemId());
        navigationDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
