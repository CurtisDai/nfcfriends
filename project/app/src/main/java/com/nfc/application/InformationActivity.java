//package com.nfc.application;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.DocumentReference;
//
//import com.google.firebase.auth.FirebaseAuth;
//
//public class InformationActivity extends BaseActivity {
//
//    private FirebaseFirestore db;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        db = FirebaseFirestore.getInstance();
//        setContentView(R.layout.activity_information);
//        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        getFriends(currentuser);
//
//    }
//
//    private void getFriends(String currentuser) {
//        DocumentReference documentReference = db.collection("users").document(
//                currentuser);
//        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        String info = document.getData().get("name").toString();
//                        Toast.makeText(InformationActivity.this, info, Toast.LENGTH_LONG).show();
//                        Log.d("getFriends", "DocumentSnapshot data: " + document.getData().get("name").toString());
//                    } else {
//                        Log.d("getFriends", "No such document");
//                    }
//                } else {
//                    Log.d("getFriends", "get failed with ", task.getException());
//                }
//            }
//        });
//    }
//}
