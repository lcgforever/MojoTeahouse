package com.mojoteahouse.mojotea.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mojoteahouse.mojotea.MojoTeaApp;
import com.mojoteahouse.mojotea.R;
import com.mojoteahouse.mojotea.data.MojoData;
import com.mojoteahouse.mojotea.data.MojoMenu;
import com.mojoteahouse.mojotea.data.OrderItem;
import com.mojoteahouse.mojotea.data.Topping;
import com.mojoteahouse.mojotea.fragment.MojoMenuFragment;
import com.mojoteahouse.mojotea.fragment.dialog.ProgressDialogFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MojoMenuActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        MojoMenuFragment.MojoMenuClickListener, View.OnClickListener {

    private static final String EXTRA_MOJO_DATA = "EXTRA_MOJO_DATA";
    private static final String EXTRA_ORDER_ITEM_LIST = "EXTRA_ORDER_ITEM_LIST";

    private Toolbar toolbar;
    private DrawerLayout navigationDrawer;
    private ActionBarDrawerToggle navigationDrawerToggle;
    private NavigationView navigationView;
    private Button goToCartButton;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private DatabaseReference baseReference;
    private MojoDataChildEventListener mojoDataChildEventListener;
    private MojoData mojoData;
    private ArrayList<OrderItem> orderItemList;

    public static void start(Context context) {
        Intent intent = new Intent(context, MojoMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mojo_menu);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(R.string.nav_menu_title);
        }

        navigationDrawer = (DrawerLayout) findViewById(R.id.mojo_menu_drawer_layout);
        navigationDrawerToggle = new ActionBarDrawerToggle(this, navigationDrawer, toolbar,
                R.string.action_open_navigation_drawer, R.string.action_close_navigation_drawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (navigationView.getMenu().findItem(R.id.nav_orders).isChecked()) {
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            MojoMenuActivity.this, toolbar, getString(R.string.toolbar_transition));
                    MojoOrderActivity.start(MojoMenuActivity.this, orderItemList, optionsCompat.toBundle());
                } else if (navigationView.getMenu().findItem(R.id.nav_settings).isChecked()) {
                    SettingsActivity.start(MojoMenuActivity.this);
                }
                navigationView.setCheckedItem(R.id.nav_menu);
            }
        };
        navigationDrawer.addDrawerListener(navigationDrawerToggle);

        navigationView = (NavigationView) findViewById(R.id.mojo_menu_nav_view);
        navigationView.setCheckedItem(R.id.nav_menu);
        navigationView.setNavigationItemSelectedListener(this);

        goToCartButton = (Button) findViewById(R.id.bottom_action_button);
        goToCartButton.setOnClickListener(this);

        tabLayout = (TabLayout) findViewById(R.id.mojo_menu_tab_layout);
        viewPager = (ViewPager) findViewById(R.id.mojo_menu_view_pager);

        baseReference = FirebaseDatabase.getInstance().getReference().child("data");
        mojoDataChildEventListener = new MojoDataChildEventListener();

        if (savedInstanceState != null) {
            mojoData = savedInstanceState.getParcelable(EXTRA_MOJO_DATA);
            orderItemList = savedInstanceState.getParcelableArrayList(EXTRA_ORDER_ITEM_LIST);
            updateTabLayoutAndViewPager();
            updateGoToCartButton();
        } else {
            mojoData = new MojoData();
            orderItemList = new ArrayList<>();
            showLoadingDialog();
            baseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dismissLoadingDialog();
                    if (dataSnapshot != null && dataSnapshot.exists()) {
                        ArrayList<MojoMenu> mojoMenuList = new ArrayList<>();
                        ArrayList<Topping> toppingList = new ArrayList<>();
                        ArrayList<String> availableZipList = new ArrayList<>();
                        Map<String, Map<String, Object>> mojoDataMap = (Map<String, Map<String, Object>>) dataSnapshot.getValue();
                        Map<String, Object> toppingDataMap = mojoDataMap.get("topping");
                        for (Object toppingData : toppingDataMap.values()) {
                            toppingList.add(new Topping((Map<String, Object>) toppingData));
                        }
                        Map<String, Object> menuDataMap = mojoDataMap.get("menu");
                        for (Object menuData : menuDataMap.values()) {
                            mojoMenuList.add(new MojoMenu((Map<String, Object>) menuData));
                        }
                        availableZipList.addAll((List<String>) mojoDataMap.get("zip"));

                        mojoData.setMojoMenuList(mojoMenuList);
                        mojoData.setToppingList(toppingList);
                        mojoData.setAvailableZipList(availableZipList);

                        updateTabLayoutAndViewPager();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    dismissLoadingDialog();
                    Log.e("findme", "Load menu data failed: " + databaseError.getMessage());
                }
            });
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        navigationDrawerToggle.syncState();
    }

    @Override
    protected void onStart() {
        super.onStart();
        baseReference.addChildEventListener(mojoDataChildEventListener);
    }

    @Override
    protected void onStop() {
        baseReference.removeEventListener(mojoDataChildEventListener);
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(EXTRA_MOJO_DATA, mojoData);
        outState.putParcelableArrayList(EXTRA_ORDER_ITEM_LIST, orderItemList);
        super.onSaveInstanceState(outState);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_action_button:
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this, toolbar, getString(R.string.toolbar_transition));
                CartActivity.startForResult(this, mojoData, orderItemList, optionsCompat.toBundle());
                break;
        }
    }

    @Override
    public void onMojoMenuClicked(MojoMenu mojoMenu) {
        EditMojoItemActivity.startForResult(this, mojoMenu, mojoData.getToppingList());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MojoTeaApp.EDIT_MOJO_MENU_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                OrderItem orderItem = data.getParcelableExtra(EditMojoItemActivity.EXTRA_ORDER_ITEM);
                orderItemList.add(orderItem);
                updateGoToCartButton();
            }
        } else if (requestCode == MojoTeaApp.SHOW_CART_REQUEST_CODE) {
            orderItemList = data.getParcelableArrayListExtra(CartActivity.EXTRA_ORDER_ITEM_LIST);
            updateGoToCartButton();
        }
    }

    private void showLoadingDialog() {
        ProgressDialogFragment.showWithMessage(getFragmentManager(), getString(R.string.loading_data_message));
    }

    private void dismissLoadingDialog() {
        ProgressDialogFragment.dismiss(getFragmentManager());
    }

    private void updateTabLayoutAndViewPager() {
        Map<String, ArrayList<MojoMenu>> mojoMenuMap = new HashMap<>();
        for (MojoMenu mojoMenu : mojoData.getMojoMenuList()) {
            String category = mojoMenu.getCategory();
            ArrayList<MojoMenu> categoryMenuList = mojoMenuMap.containsKey(category)
                    ? mojoMenuMap.get(category)
                    : new ArrayList<MojoMenu>();
            categoryMenuList.add(mojoMenu);
            mojoMenuMap.put(category, categoryMenuList);
        }
        ArrayList<String> categoryList = new ArrayList<>(mojoMenuMap.keySet());
        Collections.sort(categoryList);

        int categoryCount = categoryList.size();
        tabLayout.setTabMode(categoryCount > 3 ? TabLayout.MODE_SCROLLABLE : TabLayout.MODE_FIXED);
        viewPager.setAdapter(new MojoMenuFragmentPagerAdapter(getSupportFragmentManager(), categoryList, mojoMenuMap));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager);
    }

    private void updateGoToCartButton() {
        int totalQuantity = 0;
        for (OrderItem orderItem : orderItemList) {
            totalQuantity += orderItem.getQuantity();
        }
        goToCartButton.setText(String.format(getString(R.string.go_to_cart_button_text),
                getResources().getQuantityString(R.plurals.cart_quantity_text, totalQuantity, totalQuantity)));
        goToCartButton.setVisibility(totalQuantity > 0 ? View.VISIBLE : View.GONE);
    }


    private class MojoMenuFragmentPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<String> categoryList;
        private MojoMenuFragment[] mojoMenuFragments;

        MojoMenuFragmentPagerAdapter(FragmentManager fragmentManager,
                                     ArrayList<String> categoryList,
                                     Map<String, ArrayList<MojoMenu>> mojoMenuMap) {
            super(fragmentManager);

            this.categoryList = categoryList;
            int count = categoryList.size();
            mojoMenuFragments = new MojoMenuFragment[count];
            for (int i = 0; i < count; i++) {
                ArrayList<MojoMenu> menuList = new ArrayList<>(mojoMenuMap.get(categoryList.get(i)));
                mojoMenuFragments[i] = MojoMenuFragment.newInstance(menuList);
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return categoryList.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return mojoMenuFragments[position];
        }

        @Override
        public int getCount() {
            return categoryList == null ? 0 : categoryList.size();
        }
    }

    private class MojoDataChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            // No-op
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            // No-op
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
}
