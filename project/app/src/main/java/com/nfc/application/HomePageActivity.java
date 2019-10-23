package com.nfc.application;

import android.Manifest;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.storage.images.FirebaseImageLoader;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import adapter.BusinessCardAdapter;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
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

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.content.Context;

public class HomePageActivity extends AppCompatActivity implements CreateNdefMessageCallback {

    private DrawerLayout mDrawerLayout;
    private ArrayList<BusinessCard> businessCardList = new ArrayList<BusinessCard>();
    private RecyclerView recyclerView;
    private LinearSnapHelper linearSnapHelper = null;
    private CardScaleHelper cardScaleHelper;
    private int mLastPos = -1;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private BusinessCardAdapter adapter;
    private ArrayList<String> friends;
    private CircleImageView circleImageView;
    private SharedPreferences preferences;
    private Uri imageUri;
    private String friendUri;
    private String currentuser;
    //NFC Adapter
    private NfcAdapter mNfcAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        db = FirebaseFirestore.getInstance();
        storage  = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        initialize();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        //initialize recycleview
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        mDrawerLayout =  findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_view);
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
                File outputImage = new File(Environment.getExternalStorageDirectory(), "output_image.jpg");
                try {
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT >= 24){
                    imageUri = FileProvider.getUriForFile(HomePageActivity.this,
                            "com.example.nfc.application.fileprovider", outputImage);
                }else{
                    imageUri = Uri.fromFile(outputImage);
                }
                //start camera
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 1);
            }
        });

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
                removeFriends(currentuser, friendUri);
                Toast.makeText(this, "to do", Toast.LENGTH_LONG).show();
                break;
            case R.id.log_out:
                SharedPreferences.Editor editor=preferences.edit();
                editor.putBoolean("main",false);
                editor.apply();
                Intent intent1 = new Intent(HomePageActivity.this,LoginActivity.class);
                startActivity(intent1);
                HomePageActivity.this.finish();
                break;
            default:
                break;
        }
        return true;
    }

    public void initialize(){
        currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DocumentReference documentReference = db.collection("users").document(currentuser);
        Log.d("current user", currentuser);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        StorageReference cover_url = storageRef.child("cover/" + currentuser + ".jpg");
                        StorageReference profilePic_url = storageRef.child("portrait/" + currentuser + ".jpg");
                        setMyself(document, cover_url, profilePic_url);

                        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                        Menu menu = navigationView.getMenu();
                        MenuItem name = menu.findItem(R.id.profile);
                        name.setTitle(document.getData().get("name").toString());
                        MenuItem call = menu.findItem(R.id.call);
                        call.setTitle(document.getData().get("telephone").toString());
                        MenuItem job = menu.findItem(R.id.job);
                        job.setTitle(document.getData().get("job").toString());
                        MenuItem orgnization = menu.findItem(R.id.orgnization);
                        orgnization.setTitle(document.getData().get("organization").toString());
                        MenuItem email = menu.findItem(R.id.email);
                        email.setTitle(document.getData().get("email").toString());

                        TextView mail = findViewById(R.id.mail);
                        mail.setText(document.getData().get("email").toString());
                        TextView username = findViewById(R.id.username);
                        username.setText(document.getData().get("name").toString());

                        CircleImageView icon_image = findViewById(R.id.icon_image);
                        Glide.with(HomePageActivity.this)
                                .load(profilePic_url)
                                .apply(RequestOptions.circleCropTransform())
                                .into(icon_image);

                        friends = (ArrayList<String>) document.getData().get("friends");
                        Log.d("friends", friends.toString());
                        db.collection("users").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            int i = 0;
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                if (friends.contains(document.getId())) {
                                                    StorageReference cover_url = storageRef.child("cover/" + friends.get(i) + ".jpg");
                                                    StorageReference profilePic_url = storageRef.child("portrait/" + friends.get(i) + ".jpg");
                                                    BusinessCard businessCard = new BusinessCard();
                                                    i = i + 1;
                                                    setInformation(document, businessCard, cover_url, profilePic_url);
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
                else{
                    Log.d("HomePageActivity", "document doesn't exist");
                }
            }
        });
    }

    public void setInformation(QueryDocumentSnapshot document, BusinessCard mbusinessCard, StorageReference cover, StorageReference profilePic){
        mbusinessCard.setName(document.get("name").toString());
        mbusinessCard.setEmail(document.get("email").toString());
        mbusinessCard.setTelephone(document.get("telephone").toString());
        mbusinessCard.setAddress(document.get("address").toString());
        mbusinessCard.setOrganization(document.get("organization").toString());
        mbusinessCard.setFront(true);
        mbusinessCard.setCover_url(cover);
        mbusinessCard.setProfilePic_url(profilePic);
        businessCardList.add(mbusinessCard);
    }

    public void setMyself(DocumentSnapshot document, StorageReference cover, StorageReference profilePic){
        BusinessCard mbusinessCard = new BusinessCard();
        mbusinessCard.setName(document.getData().get("name").toString());
        mbusinessCard.setEmail(document.getData().get("email").toString());
        mbusinessCard.setTelephone(document.getData().get("telephone").toString());
        mbusinessCard.setAddress(document.getData().get("address").toString());
        mbusinessCard.setOrganization(document.getData().get("organization").toString());
        mbusinessCard.setFront(true);
        mbusinessCard.setCover_url(cover);
        mbusinessCard.setProfilePic_url(profilePic);
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
        String message = currentuser;
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
            friendUri = text;
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
        if (resultCode == RESULT_OK) {
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = AllFunction.getObject().compressFromCamera();
                circleImageView.setImageBitmap(bitmap);
            }
        }
    }

    private void setFriends(String currentuser, String friendUri){
        if(friendUri == null){
            Log.d("message", "empty");
        }
        else{
            db.collection("users").document(currentuser).update("friends", FieldValue.arrayUnion(friendUri));
            db.collection("users").document(friendUri).update("friends", FieldValue.arrayUnion(currentuser));
        }
    }

    private void removeFriends(String currentuser, String friendUri){
        if(friendUri == null){
            Log.d("message", "empty");
        }
        else {
            db.collection("users").document(currentuser).update("friends", FieldValue.arrayRemove(friendUri));
            db.collection("users").document(friendUri).update("friends", FieldValue.arrayRemove(currentuser));
        }
    }

}
