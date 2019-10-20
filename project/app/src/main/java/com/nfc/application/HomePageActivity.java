package com.nfc.application;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import adapter.BusinessCardAdapter;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import library.CardScaleHelper;

;


public class HomePageActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ArrayList<BusinessCard> businessCardList = new ArrayList<BusinessCard>();
    private RecyclerView recyclerView;
    private LinearSnapHelper linearSnapHelper = null;
    private CardScaleHelper cardScaleHelper;
    private int mLastPos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setItemIconTintList(null);
        navView.setCheckedItem(R.id.organization);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Log.d("zxc","success");
                Intent intent = new Intent(HomePageActivity.this, organizationMapActivity.class);
                startActivity(intent);
                Toast.makeText(HomePageActivity.this, "you clicked tht button", Toast.LENGTH_LONG).show();
                return true;
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        navView.setCheckedItem(R.id.call);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        intitBusinessCard();
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);


        // mRecyclerView绑定scale效果
        cardScaleHelper = new CardScaleHelper();
        cardScaleHelper.setCurrentItemPos(2);
        cardScaleHelper.attachToRecyclerView(recyclerView);

        BusinessCardAdapter adapter = new BusinessCardAdapter(this, businessCardList);
        recyclerView.setAdapter(adapter);
    }

    private void intitBusinessCard(){
        for(int i = 0; i < 10; i++) {
            BusinessCard Jason = new BusinessCard();
            Jason.setName("Jason");
            Jason.setFront(true);
            businessCardList.add(Jason);
            BusinessCard Hanson = new BusinessCard();
            Hanson.setName("Hanson");
            Hanson.setFront(true);
            businessCardList.add(Hanson);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.Edit:
                Intent intent = new Intent(HomePageActivity.this, BasicInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.Delete:
                //Todo
                Toast.makeText(this, "to do", Toast.LENGTH_LONG).show();
                break;
            case R.id.Scan:
                Intent scan = new Intent(HomePageActivity.this, CardScanActivity.class);
                startActivity(scan);
                break;

            default:
        }
        return true;
    }

}
