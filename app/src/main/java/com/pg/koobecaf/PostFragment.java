package com.pg.koobecaf;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.pg.koobecaf.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class PostFragment extends Fragment {
    public ListView lv;
    public ArrayList<HashMap<String, String>> postsList;
    Parcelable state;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.posts_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        lv = view.findViewById(R.id.posts_list_view);
        postsList = new ArrayList<>();
        new JsonQuery().execute(NetworkUtils.buildUrl(NetworkUtils.POSTS, null, null));


    }

    @Override
    public void onPause() {
        state = lv.onSaveInstanceState();
        super.onPause();
    }

    protected void showComments(String idParam, String title, String body) {
        Intent i = new Intent(getContext(), Comment.class);
        i.putExtra("id", idParam);
        i.putExtra("postTitle", title);
        i.putExtra("postBody", body);
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
                        String title = reader.getString("title");
                        String body = reader.getString("body");
                        String postId = reader.getString("id");
                        String userId = reader.getString("userId");
                        HashMap<String, String> post = new HashMap<>();
                        post.put("title", title);
                        post.put("body", body);
                        post.put("postId", postId);
                        post.put("userId", userId);
                        postsList.add(post);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ListAdapter adapter = new SimpleAdapter(getContext(), postsList,
                        R.layout.list_posts, new String[]{"title", "body", "postId"},
                        new int[]{R.id.post_title, R.id.post_body});
                lv.setAdapter(adapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String postId = ((HashMap) lv.getAdapter().getItem(position)).get("postId").toString();
                        String title = ((HashMap) lv.getAdapter().getItem(position)).get("title").toString();
                        String body = ((HashMap) lv.getAdapter().getItem(position)).get("body").toString();
                        showComments(postId, title, body);

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
