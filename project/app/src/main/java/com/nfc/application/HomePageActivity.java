package com.nfc.application;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import java.io.FileNotFoundException;
import java.util.ArrayList;

import adapter.BusinessCardAdapter;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import library.CardScaleHelper;

//import for NFC function
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Parcelable;
import android.provider.Settings;
import android.widget.Toast;
import java.nio.charset.Charset;


public class HomePageActivity extends AppCompatActivity implements CreateNdefMessageCallback {

    private DrawerLayout mDrawerLayout;
    private ArrayList<BusinessCard> businessCardList = new ArrayList<BusinessCard>();
    private RecyclerView recyclerView;
    private LinearSnapHelper linearSnapHelper = null;
    private CardScaleHelper cardScaleHelper;
    private int mLastPos = -1;
    private FirebaseFirestore db;
    private BusinessCardAdapter adapter;
    private ArrayList<String> friends;
    private CircleImageView circleImageView;
    private static final int CHOOSE_PHOTO = 1;
    private Uri chosenImageUri;
    private Bitmap bitmap;

    //NFC Adapter
    private NfcAdapter mNfcAdapter = null;

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

        //find the button of upload photo
        View navHeaderView = navView.getHeaderView(0);
        circleImageView = navHeaderView.findViewById(R.id.icon_image);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(HomePageActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(HomePageActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);
                    Toast.makeText(HomePageActivity.this, "you clicked tht button", Toast.LENGTH_LONG).show();
                }else{
                    openAlbum();
                }
            }
        });

        //initialize recycleview
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        //NFC Function Call
        checkNFCFunction();
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
            default:
                break;
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
        mbusinessCard.setTelephone(document.getData().get("telephone").toString());
        mbusinessCard.setAddress(document.getData().get("address").toString());
        mbusinessCard.setOrganization(document.getData().get("organization").toString());
        mbusinessCard.setFront(true);
        businessCardList.add(mbusinessCard);
    }

    //checkNFCFunction Implementation
    private void checkNFCFunction(){
        //getting the default NFC adapter.
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(mNfcAdapter==null){
            Toast.makeText(getApplicationContext(),"Sorry, this device does not have NFC.",Toast.LENGTH_LONG).show();
            return;
        } else {
            if(!mNfcAdapter.isEnabled()){
                Toast.makeText(getApplicationContext(),"NFC is not Enabled!",Toast.LENGTH_LONG).show();
                Intent setnfc = new Intent(Settings.ACTION_NFC_SETTINGS);
                startActivity(setnfc);
                return;
            } else if(!mNfcAdapter.isNdefPushEnabled()){
                Toast.makeText(getApplicationContext(),"NFC Beam is not Enabled!",Toast.LENGTH_LONG).show();
                Intent setnfc = new Intent(Settings.ACTION_NFCSHARING_SETTINGS);
                startActivity(setnfc);
                return;
            }else {
                mNfcAdapter.setNdefPushMessageCallback(this,this);
                Toast.makeText(getApplicationContext(),"NFC is ready to use.",Toast.LENGTH_LONG).show();
                return;
            }
        }
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent){
        String message = "nfc test test";
        NdefRecord ndefRecord = NdefRecord.createMime("text/plain",message.getBytes());
        NdefMessage ndefMessage = new NdefMessage(ndefRecord);
        return ndefMessage;
    }

    protected void onResume(){
        super.onResume();
        Intent intent = getIntent();
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())){
            Parcelable[] rawMessage = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage message = (NdefMessage) rawMessage[0];
            String text = new String(message.getRecords()[0].getPayload());
            Log.d("suc",text);
        }else{
            Log.d("unsuc","Where is my fucking text???");
        }
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
    }

    //achieve upload photo fucntion
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    chosenImageUri = data.getData();
                    Log.e("chosenImageUri", chosenImageUri.toString());
                    //use content interface
                    ContentResolver cr = this.getContentResolver();
                    try {
                        bitmap = BitmapFactory.decodeStream(cr.openInputStream(chosenImageUri));
                        circleImageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        Log.e("Exception", e.getMessage(), e);
                    }
                } else {
                    Log.i("HomePageActivity", "operation error");
                }
            default:
                break;
        }
    }

    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int RequestCode, String[] permissions, int[] grantResults){
        switch (RequestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
}
