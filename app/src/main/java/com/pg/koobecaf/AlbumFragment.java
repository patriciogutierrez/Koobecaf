package com.pg.koobecaf;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.pg.koobecaf.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class AlbumFragment extends Fragment {
    public GridView lv;
    ArrayList<HashMap<String, String>> albumList;
    Parcelable state;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.album_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        lv = view.findViewById(R.id.album_gridview);
        albumList = new ArrayList<>();
        new JsonQuery().execute(NetworkUtils.buildUrl(NetworkUtils.ALBUMS, null, null));
    }

    @Override
    public void onPause() {
        state = lv.onSaveInstanceState();
        super.onPause();
    }

    protected void showImages(String idParam) {
        Intent i = new Intent(getContext(), ImageActivity.class);
        i.putExtra("id", idParam);
        startActivity(i);
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
                        String title = reader.getString("title");
                        HashMap<String, String> album = new HashMap<>();
                        album.put("id", id);
                        album.put("title", title);
                        albumList.add(album);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ListAdapter adapter = new SimpleAdapter(getContext(), albumList,
                        R.layout.lists_albums, new String[]{"title"},
                        new int[]{R.id.album_title});
                lv.setAdapter(adapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        showImages(((HashMap) lv.getAdapter().getItem(position)).get("id").toString());

                    }
                });
                if (state != null) {
                    lv.onRestoreInstanceState(state);
                }
            }
            getView().findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }
    }
}

