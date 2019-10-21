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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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


public class HomePageActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ArrayList<BusinessCard> businessCardList = new ArrayList<BusinessCard>();
    private RecyclerView recyclerView;
    private LinearSnapHelper linearSnapHelper = null;
    private CardScaleHelper cardScaleHelper;
    private int mLastPos = -1;
    private FirebaseFirestore db;
    private BusinessCardAdapter adapter;
    private ArrayList<String> friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        db = FirebaseFirestore.getInstance();
        initialize();
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

        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

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
//            case R.id.Scan:
//                Intent scan = new Intent(HomePageActivity.this, CardScanActivity.class);
//                startActivity(scan);
//                break;

            default:
        }
        return true;
    }

    public void initialize(){
        final String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DocumentReference documentReference = db.collection("users").document(currentuser);
        Log.d("current user", currentuser);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    BusinessCard businessCard = new BusinessCard();
                    setMyself(document, businessCard);
                    friends = (ArrayList<String>) document.getData().get("friends");
                    Log.d("friends", friends.toString());
                    db.collection("users")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if (friends.contains(document.getId())) {
                                                BusinessCard businessCard = new BusinessCard();
                                                setInformation(document, businessCard);
                                            }
                                        }
                                        Log.d("size", String.valueOf(businessCardList.size()));
                                        cardScaleHelper = new CardScaleHelper();
                                        cardScaleHelper.setCurrentItemPos(2);
                                        cardScaleHelper.attachToRecyclerView(recyclerView);

                                        adapter = new BusinessCardAdapter(HomePageActivity.this, businessCardList);
                                        recyclerView.setAdapter(adapter);
                                    } else {

                                    }
                                }
                            });
                }
            }
        });
    }

    public void setInformation(QueryDocumentSnapshot document, BusinessCard mbusinessCard){
        mbusinessCard = new BusinessCard();
        mbusinessCard.setName(document.get("name").toString());
        mbusinessCard.setEmail(document.get("email").toString());
        mbusinessCard.setJob(document.get("job").toString());
        mbusinessCard.setTelephone(document.get("telephone").toString());
        mbusinessCard.setAddress(document.get("address").toString());
        mbusinessCard.setOrganization(document.get("organization").toString());
        mbusinessCard.setFront(true);
        businessCardList.add(mbusinessCard);
    }

    public void setMyself(DocumentSnapshot document, BusinessCard mbusinessCard){
        mbusinessCard = new BusinessCard();
        mbusinessCard.setName(document.getData().get("name").toString());
        mbusinessCard.setEmail(document.getData().get("email").toString());
        mbusinessCard.setJob(document.getData().get("job").toString());
        mbusinessCard.setTelephone(document.getData().get("telephone").toString());
        mbusinessCard.setAddress(document.getData().get("address").toString());
        mbusinessCard.setOrganization(document.getData().get("organization").toString());
        mbusinessCard.setFront(true);
        businessCardList.add(mbusinessCard);
    }

}
