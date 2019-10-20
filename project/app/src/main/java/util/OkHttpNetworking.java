package util;


import android.os.AsyncTask;


import com.nfc.application.BuildConfig;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;



public class OkHttpNetworking extends AsyncTask<String, Void, String> {

    private String textToProcess;

    public OkHttpNetworking(String textToProcess) {
        this.textToProcess = textToProcess;
    }

    private static final String api_key = BuildConfig.ER_API_KEY;

    public String callNamedERAPI(String extractedText){
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/octet-stream");
        RequestBody body = RequestBody.create(mediaType, extractedText );
        Request request = new Request.Builder()
                .url("http://api.intellexer.com/recognizeNeText?apikey=" + api_key + "&loadNamedEntities=true")
                .post(body)
                .addHeader("cache-control", "no-cache")
                .build();
        try {
            Response response = client.newCall(request).execute();
            JSONObject Jobject = new JSONObject(response.body().string());
            JSONArray ErArray = Jobject.getJSONArray("entities");
            for(int i = 0; i < ErArray.length(); i++){
                JSONObject temp = ErArray.getJSONObject(i);
                if(temp.getInt("type") == 1){
                    return temp.getString("text");
                }
            }
            try {
                String[] arr = extractedText.split("\\s+");
                return arr[0] + " " + arr[1];
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        return callNamedERAPI(textToProcess);
    }
}