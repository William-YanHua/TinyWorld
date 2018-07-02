package com.gyg.lenovo.world;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private SearchTask searchTask = null;

    private View mSearchForm;
    private View mResultForm;
    private Spinner mSpinner;
    private EditText mInput;
    private ListView mResult;

    private MyApp myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        myApp = (MyApp) getApplication();

        mSearchForm = findViewById(R.id.search_form);
        mResultForm = findViewById(R.id.result_form);
        mSpinner = (Spinner) findViewById(R.id.search_spinner);
        mInput = (EditText) findViewById(R.id.input);
        mResult = (ListView) findViewById(R.id.search_result);

        Button mSearchButton = (Button) findViewById(R.id.search_button);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSearch();
            }
        });

        Button mReturnButton = (Button) findViewById(R.id.return_button);
        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showList(false);
            }
        });
    }

    private void attemptSearch() {
        if(searchTask != null) {
            return;
        }

        mInput.setError(null);

        long method = mSpinner.getSelectedItemId();
        String value = mInput.getText().toString();
        Integer id = myApp.user_id;

        if(TextUtils.isEmpty(value)) {
            mInput.setError(getString(R.string.error_field_required));
            mInput.requestFocus();
        } else {
            searchTask = new SearchTask(method,value,id);
            searchTask.execute((Void) null);
        }
    }

    private void showList(boolean show) {
        if(show) {
            mResultForm.setVisibility(View.VISIBLE);
            mSearchForm.setVisibility(View.GONE);
        } else {
            mResultForm.setVisibility(View.GONE);
            mSearchForm.setVisibility(View.VISIBLE);
        }
    }

    public class SearchTask extends AsyncTask<Void, Void, JSONArray> {

        private final long mMethod;
        private final String mValue;
        private final Integer mId;

        SearchTask(long method, String value, Integer id) {
            mMethod = method;
            mValue = value;
            mId = id;
        }

        @Override
        protected JSONArray doInBackground(Void... voids) {
            try {
                JSONObject json = new JSONObject();
                json.put("method",mMethod);
                json.put("id",mId);
                json.put("value",mValue);
                JSONObject obj = Conn.doJsonPost("/relationship/filter",json);
                Integer code = obj.getInt("code");
                String msg = obj.getString("msg");
                //System.out.println(res);
                if(code == 200) {
                    JSONArray array = obj.getJSONArray("result");
                    return array;
                } else {
                    System.out.println(msg);
                    return null;
                }
                // Simulate network access.
                // Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
            searchTask = null;
            List<String> names = new ArrayList<>();
            List<Uri> images = new ArrayList<>();
            List<String> telephone = new ArrayList<>();
            List<String> id = new ArrayList<>();

            for(int i=0;i<jsonArray.length();i++) {
                try {
                    JSONObject json = (JSONObject)jsonArray.get(i);
                    names.add(json.getString("nickname"));
                    images.add(Uri.parse(json.getString("profile_photo")));
                    telephone.add(json.getString("phone_number"));
                    id.add(json.getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            SearchAdapter searchAdapter = new SearchAdapter(images,names,telephone,id);
            mResult.setAdapter(searchAdapter);

            showList(true);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            searchTask = null;
            showList(false);
        }
    }
}
