//package com.nfc.application;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.core.content.FileProvider;
//
//import android.Manifest;
//import android.content.ContentResolver;
//import android.content.pm.PackageManager;
//import android.content.res.AssetManager;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Build;
//import android.os.Bundle;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Environment;
//import android.provider.ContactsContract;
//import android.provider.MediaStore;
//
//import android.util.Base64;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.i18n.phonenumbers.PhoneNumberMatch;
//import com.google.i18n.phonenumbers.PhoneNumberUtil;
//import com.googlecode.tesseract.android.TessBaseAPI;
//
//import org.json.JSONObject;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.Locale;
//import java.util.concurrent.ExecutionException;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//
//
//public class CardScanActivity extends AppCompatActivity {
//
//    Bitmap image;
//    private TessBaseAPI mTess;
//    String datapath = "";
//    public static final int TAKE_PHOTO = 1;
//    public static final int CHOOSE_PHOTO = 2;
//
//    ImageView testImage;
//    TextView runOCR;
//    TextView displayText;
//    TextView displayEmail;
//    TextView displayPhone;
//    TextView displayName;
//    ImageButton take;
//    ImageButton choose;
//
//    TextView openContacts;
//    Bitmap bitmap;
//    private Uri chosenImageUri;
//    private Uri imageUri;
//    private ImageView picture;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        runOCR = findViewById(R.id.textView);
//        openContacts = findViewById(R.id.textView6);
//
//        take = findViewById(R.id.cameraButton);
//        choose = findViewById(R.id.photoButton);
//
//        displayText = findViewById(R.id.textView2);
//        displayName = findViewById(R.id.textView5);
//        displayPhone = findViewById(R.id.textView4);
//        displayEmail = findViewById(R.id.textView3);
//        picture = findViewById(R.id.picture);
//
//
//        //initialize Tesseract API
//        String language = "eng";
//        datapath = getFilesDir() + "/tesseract/";
//        mTess = new TessBaseAPI();
//
//        checkFile(new File(datapath + "tessdata/"));
//        mTess.init(datapath, language);
//
//        take.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
//                try {
//                    if (outputImage.exists()) {
//                        outputImage.delete();
//                    }
//                    outputImage.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (Build.VERSION.SDK_INT >= 24) {
//                    imageUri = FileProvider.getUriForFile(MainActivity.this,
//                            "com.example.nfc.application.fileprovider", outputImage);
//                } else {
//                    imageUri = Uri.fromFile(outputImage);
//                }
//                //start camera
//                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                startActivityForResult(intent, TAKE_PHOTO);
//            }
//        });
//
//        choose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if(ContextCompat.checkSelfPermission(CardScanActivity.this,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//                    ActivityCompat.requestPermissions(CardScanActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                            1);
//                }else{
//                    Toast.makeText(CardScanActivity.this, "here",Toast.LENGTH_SHORT).show();
//                    openAlbum();
//
//                }
//            }
//        });
//
//
//
//
//        //run the OCR on the test_image...
//        runOCR.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                processImage();
//            }
//        });
//
//
//        //Add the extracted info from Business Card to the phone's contacts...
//        openContacts.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                addToContacts();
//            }
//        });
//    }
//
//    private void openAlbum(){
//
//        Intent intent = new Intent("android.intent.action.GET_CONTENT");
//        intent.setType("image/*");
//
//        startActivityForResult(intent, CHOOSE_PHOTO);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int RequestCode, String[] permissions, int[] grantResults){
//        switch (RequestCode){
//            case 1:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//
//                    openAlbum();
//                }else{
//                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
//                }
//                break;
//            default:
//        }
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case TAKE_PHOTO:
//                if (resultCode == RESULT_OK) {
//                    try {
//                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
//                        picture.setImageBitmap(bitmap);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                }
//                break;
//            case CHOOSE_PHOTO:
//                if(resultCode == RESULT_OK) {
//                    chosenImageUri = data.getData();
//                    Log.e("chosenImageUri", chosenImageUri.toString());
//                    //use content interface
//                    ContentResolver cr = this.getContentResolver();
//                    try{
//                        bitmap = BitmapFactory.decodeStream(cr.openInputStream(chosenImageUri));
//                        picture.setImageBitmap(bitmap);
//                    } catch (FileNotFoundException e){
//                        Log.e("Exception", e.getMessage(),e);
//                    }
//                }else{
//                    Log.i("MainActivtiy", "operation error");
//                }
//                super.onActivityResult(requestCode, resultCode, data);
//            default:
//                break;
//        }
//    }
//
//    public void processImage(){
//        String OCRresult = null;
//        //init image
//        mTess.setImage(bitmap);
//        OCRresult = mTess.getUTF8Text();
//        //displayText.setText(OCRresult);
//        extractName(OCRresult);
//        extractEmail(OCRresult);
//        extractPhone(OCRresult);
//    }
//
//    public void extractName(String str){
//        System.out.println("Getting the Name");
//        final String NAME_REGEX = "^([A-Z]([a-z]*|\\.) *){1,2}([A-Z][a-z]+-?)+$";
//        Pattern p = Pattern.compile(NAME_REGEX, Pattern.MULTILINE);
//        Matcher m =  p.matcher(str);
//        if(m.find()){
//            System.out.println(m.group());
//            displayName.setText(m.group());
//        }
//    }
//
//    public void extractEmail(String str) {
//        System.out.println("Getting the email");
//        final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
//        Pattern p = Pattern.compile(EMAIL_REGEX, Pattern.MULTILINE);
//        Matcher m = p.matcher(str);   // get a matcher object
//        if(m.find()){
//            System.out.println(m.group());
//            displayEmail.setText(m.group());
//        }
//    }
//
//    public void extractPhone(String str){
//        System.out.println("Getting Phone Number");
//        final String PHONE_REGEX="(?:^|\\D)(\\d{3})[)\\-. ]*?(\\d{3})[\\-. ]*?(\\d{4})(?:$|\\D)";
//        Pattern p = Pattern.compile(PHONE_REGEX, Pattern.MULTILINE);
//        Matcher m = p.matcher(str);   // get a matcher object
//        if(m.find()){
//            System.out.println(m.group());
//            displayPhone.setText(m.group());
//        }
//    }
//
//    private void checkFile(File dir) {
//        //directory does not exist, but we can successfully create it
//        if (!dir.exists()&& dir.mkdirs()){
//            copyFiles();
//        }
//        //The directory exists, but there is no data file in it
//        if(dir.exists()) {
//            String datafilepath = datapath+ "/tessdata/eng.traineddata";
//            File datafile = new File(datafilepath);
//            if (!datafile.exists()) {
//                copyFiles();
//            }
//        }
//    }
//
//    private void copyFiles() {
//        try {
//            //location we want the file to be at
//            String filepath = datapath + "/tessdata/eng.traineddata";
//
//            //get access to AssetManager
//            AssetManager assetManager = getAssets();
//
//            //open byte streams for reading/writing
//            InputStream instream = assetManager.open("tessdata/eng.traineddata");
//            OutputStream outstream = new FileOutputStream(filepath);
//
//            //copy the file to the location specified by filepath
//            byte[] buffer = new byte[1024];
//            int read;
//            while ((read = instream.read(buffer)) != -1) {
//                outstream.write(buffer, 0, read);
//            }
//            outstream.flush();
//            outstream.close();
//            instream.close();
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void addToContacts(){
//
//        // Creates a new Intent to insert a contact
//        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
//        // Sets the MIME type to match the Contacts Provider
//        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
//
//        //Checks if we have the name, email and phone number...
//        if(displayName.getText().length() > 0 && ( displayPhone.getText().length() > 0 || displayEmail.getText().length() > 0 )){
//            //Adds the name...
//            intent.putExtra(ContactsContract.Intents.Insert.NAME, displayName.getText());
//
//            //Adds the email...
//            intent.putExtra(ContactsContract.Intents.Insert.EMAIL, displayEmail.getText());
//            //Adds the email as Work Email
//            intent .putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
//
//            //Adds the phone number...
//            intent.putExtra(ContactsContract.Intents.Insert.PHONE, displayPhone.getText());
//            //Adds the phone number as Work Phone
//            intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
//
//            //starting the activity...
//            startActivity(intent);
//        }else{
//            Toast.makeText(getApplicationContext(), "No information to add to contacts!", Toast.LENGTH_LONG).show();
//        }
//
//
//    }
//
//
//    }
//
//
//
//
//    private static final int IMAGE_PERMISSION = 4 ;
//    private static final int IMAGE_CAPTURE_REQUEST = 1001;
//
//    private static final String TAG = "MainActivity";
//    private static final String INTENT_PHONE_NUMBER = "phoneNumber";
//    private static final String INTENT_NAME = "name";
//    private static final String INTENT_EMAIL = "email";
//
//    private ProgressBar progressBar;
//
//    private EditText obtainedText;
//    private Uri imageUri;
//
//    private String mCurrentPhotoPath;
//    private String phoneNumber;
//    private String contactName;
//    private String contactEmail;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            setContentView(R.layout.activity_card_scan);
//
//            //Get access to buttons and editTexts
//            ImageButton cameraButton = findViewById(R.id.cameraButton);
//            Button submitButton = findViewById(R.id.submitButton);
//            obtainedText = findViewById(R.id.obtainedText);
//
//            progressBar = findViewById(R.id.progressBar);
//            progressBar.setVisibility(View.GONE);
//
//            cameraButton.setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View v) {
//                    obtainedText.setText("");
//                    startCameraActivityIntent();
//
//
//                }
//            });
//
//            submitButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String results = obtainedText.getText().toString().trim();
//                    ArrayList<String> phoneNumbers = parseResults(results);
//
//                    if(phoneNumbers == null){
//                        phoneNumber = "Error";
//                    }else{
//                        if(!phoneNumbers.isEmpty())
//                            try {
//                                phoneNumber = phoneNumbers.get(0);
//                            }catch(IndexOutOfBoundsException e){
//                                e.printStackTrace();
//                                Toast.makeText(CardScanActivity.this, "There is no text!", Toast.LENGTH_SHORT).show();
//                            }
//                    }
//                    if(!results.isEmpty()) {
//                        try {
//                            contactName = parseName(results);
//                            contactEmail = parseEmail(results);
//                            Intent intent = new Intent(CardScanActivity.this, ContactsActivity.class);
//                            intent.putExtra(INTENT_PHONE_NUMBER, phoneNumber);
//                            intent.putExtra(INTENT_NAME, contactName);
//                            intent.putExtra(INTENT_EMAIL, contactEmail);
//                            startActivity(intent);
//                        }catch (NullPointerException e){
//                            e.printStackTrace();
//                            Toast.makeText(CardScanActivity.this, "Error", Toast.LENGTH_SHORT).show();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        } catch (ExecutionException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            });
//        }
//
//
//
//
//
//    /**
//     * Starts the camera and requests permission to use the camera if permission doesn't exist
//     *
//     */
//    public void startCameraActivityIntent(){
//        //Intent to startCamera
//
//        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
//        try {
//            if(outputImage.exists()){
//                outputImage.delete();
//            }
//            outputImage.createNewFile();
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//        if(Build.VERSION.SDK_INT >= 24){
//            imageUri = FileProvider.getUriForFile(CardScanActivity.this,
//                    "com.example.nfc.application.fileprovider", outputImage);
//        }else{
//            imageUri = Uri.fromFile(outputImage);
//        }
//        //start camera
//        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        startActivityForResult(intent, TAKE_PHOTO);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case TAKE_PHOTO:
//                if (resultCode == RESULT_OK) {
//                    try {
//                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
//                        picture.setImageBitmap(bitmap);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                }
//                break;
//            default:
//                break;
//        }
//
//    }
//
//
//        /**
//         * Creates and writes a new image to send in the post request to Google Vision API
//         *
//         * @return , The captured image file
//         */
//        private File createImageFile(){
//            //Create image filename
//            String imageFileName = "JPEG_00";
//
//            //Access storage directory for photos and create temporary image file
//            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//            File image = null;
//            try {
//                image = File.createTempFile(imageFileName,".jpg",storageDir);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            //Store file path for usage with intents
//            assert image != null;
//            mCurrentPhotoPath = image.getAbsolutePath();
//            return image;
//        }
//
//
//        /**
//         * Converts the captured image to a base 64 encoded string.
//         * Images are typically sent as long encoded strings in networks instead of bits and bytes of data
//         *
//         * Convert file to byteArrayOutputStream then to ByteArray and directly to a base64 encoded string
//         *
//         * @return , The encoded String that represents the captured image
//         */
//        private String convertImageToBase64EncodedString() {
//            File f = new File(mCurrentPhotoPath);
//            String base64EncodedString;
//
//            InputStream inputStream = null;
//            try {
//                inputStream = new FileInputStream(f);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//
//            byte[] buffer = new byte[8192];
//            int bytesRead;
//            ByteArrayOutputStream output = new ByteArrayOutputStream();
//            try{
//                assert inputStream != null;
//                while((bytesRead = inputStream.read(buffer)) != -1){
//                    output.write(buffer, 0, bytesRead);
//                }
//            }catch(IOException e){
//                e.printStackTrace();
//            }
//
//            byte[] bytes = output.toByteArray();
//            base64EncodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
//            return base64EncodedString;
//        }
//
//
//        /**
//         * Method to delete the image after base64 encoded string has been obtained from it
//         *
//         * Avoids storing images that are unnecessary after use
//         */
//        private void deleteCapturedImage() {
//            File fileToBeDeleted = new File(mCurrentPhotoPath);
//            if(fileToBeDeleted.exists()){
//                if(fileToBeDeleted.delete()){
//                    Log.w(TAG, "File Deleted: " + mCurrentPhotoPath);
//                } else {
//                    Log.w(TAG, "File Not Deleted " + mCurrentPhotoPath);
//                }
//            }
//        }
//
//
//        /**
//         * Parses phoneNumbers from a string using Google's libphonenumber library
//         *
//         * @param bCardText, The text obtained from the vision API processing
//         * @return ArrayList of parsed phone numbers from the vision API processed text string
//         */
//        private ArrayList<String> parseResults(String bCardText) {
//            PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
//            Iterable<PhoneNumberMatch> numberMatches = phoneNumberUtil.findNumbers(bCardText, Locale.US.getCountry());
//            ArrayList<String> data = new ArrayList<>();
//            for(PhoneNumberMatch number : numberMatches){
//                String s = number.rawString();
//                data.add(s);
//            }
//            return data;
//        }
//
//
//        /**
//         * Parses email from the string returned from Google Vision APi
//         * @param results, String returned from Google Vision API
//         * @return String that is the parsed email. Uses REGEX
//         */
//        private String parseEmail(String results) {
//            Matcher m = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+").matcher(results);
//            String parsedEmail = "Error";
//            while (m.find()) {
//                parsedEmail = m.group();
//            }
//            return parsedEmail;
//        }
//
//
//        /**
//         * Parses name from the string returned from Google Vision APi
//         * @param results, String returned from Google Vision API
//         * @return String that is the parsed email. Picks first two strings from the param
//         */
//        private String parseName(String results) throws ExecutionException, InterruptedException {
//            OkHttpNetworking okHttpNetworking = new OkHttpNetworking(results);
//            return okHttpNetworking.execute().get();
//        }
//
//    }
//
//
