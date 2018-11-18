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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.pg.koobecaf.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ContactsFragment extends Fragment {
    public ListView lv;
    ArrayList<HashMap<String, String>> contactList;
    Parcelable state;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contacts_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        lv = view.findViewById(R.id.contact_list_view);
        contactList = new ArrayList<>();
        new JsonQuery().execute(NetworkUtils.buildUrl(NetworkUtils.CONTACTS, null, null));
    }


    @Override
    public void onPause() {
        state = lv.onSaveInstanceState();
        super.onPause();
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
                        String email = reader.getString("email");
                        HashMap<String, String> contact = new HashMap<>();
                        contact.put("name", name);
                        contact.put("email", email);
                        contactList.add(contact);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ListAdapter adapter = new SimpleAdapter(getContext(), contactList,
                        R.layout.list_contacts, new String[]{"name", "email"},
                        new int[]{R.id.contact_name, R.id.contact_email});
                lv.setAdapter(adapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("message/rfc822");
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{contactList.get(position).get("email")});
                        i.putExtra(Intent.EXTRA_SUBJECT, "Message to: " + contactList.get(position).get("name"));
                        i.putExtra(Intent.EXTRA_TEXT, "");
                        try {
                            startActivity(Intent.createChooser(i, "Send mail..."));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                        }

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
