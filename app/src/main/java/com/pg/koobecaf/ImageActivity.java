package com.pg.koobecaf;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;

import com.pg.koobecaf.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ImageActivity extends AppCompatActivity {
    private GridView imageGrid;
    private ArrayList<Bitmap> bitmapList;
    public ArrayList<HashMap<String, String>> imageList;
    private Bundle extras;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.imageList = new ArrayList<>();
        this.imageGrid = findViewById(R.id.images_grid_view);
        this.bitmapList = new ArrayList<>();
        extras = getIntent().getExtras();
        new JsonQuery().execute(NetworkUtils.buildUrl(NetworkUtils.PHOTOS, "albumId", extras.get("id").toString()));
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void setAdapter() {
        this.imageGrid.setAdapter(new ImageAdapter(getBaseContext(), this.bitmapList));

    }

    private Bitmap urlImageToBitmap(String imageUrl) throws Exception {
        Bitmap res = null;
        URL url = new URL(imageUrl);
        res = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        return res;
    }

    class DownloadFilesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Bitmap res = null;
            for (int i = 0; i < imageList.size(); i++) {
                try {
                    bitmapList.add(urlImageToBitmap(imageList.get(i).get("url")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return "1";
        }

        @Override
        protected void onPostExecute(String result) {
            setAdapter();
            findViewById(R.id.loadingPanel2).setVisibility(View.GONE);
        }
    }

    class JsonQuery extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String jsonSearchResults = null;
            try {
                jsonSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonSearchResults;
        }

        @Override
        protected void onPostExecute(String jsonSearchResults) {
            if (jsonSearchResults != null && !jsonSearchResults.equals("")) {
                JSONArray data;
                JSONObject reader;
                try {
                    data = new JSONArray(jsonSearchResults);

                    for (int i = 0; i < data.length(); i++) {
                        reader = data.getJSONObject(i);
                        String id = reader.getString("id");
                        String url = reader.getString("thumbnailUrl");
                        HashMap<String, String> image = new HashMap<>();
                        image.put("id", id);
                        image.put("url", url);
                        imageList.add(image);
                        // bitmapList.add(urlImageToBitmap(url));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new DownloadFilesTask().execute();

            }
        }
    }

}
