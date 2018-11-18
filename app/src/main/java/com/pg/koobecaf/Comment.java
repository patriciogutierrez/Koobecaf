package com.pg.koobecaf;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.pg.koobecaf.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Comment extends AppCompatActivity {
    public TextView publicacion;
    public TextView cuerpo;
    public ListView lv;
    public ArrayList<HashMap<String,String>> comentarios;
    public Bundle extras;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        lv = findViewById(R.id.comments_list);
        publicacion = findViewById(R.id.set_post_title);
        cuerpo = findViewById(R.id.set_post_body);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        comentarios =new ArrayList<>();
        extras = getIntent().getExtras();
        publicacion.setText(extras.get("postTitle").toString());
        cuerpo.setText(extras.get("postBody").toString());
        new JsonQuery().execute(NetworkUtils.buildUrl(NetworkUtils.COMMENTS,"postId",extras.get("id").toString()));

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
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
                        String name = reader.getString("name");
                        String body = reader.getString("body");
                        HashMap<String, String> contact = new HashMap<>();
                        contact.put("name", name);
                        contact.put("body", body);
                        comentarios.add(contact);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ListAdapter adapter = new SimpleAdapter(getBaseContext(), comentarios,
                        R.layout.lists_comments, new String[]{"name", "body"},
                        new int[]{R.id.comment_name, R.id.comment_body});

                lv.setAdapter(adapter);
            }
        }
    }
}
