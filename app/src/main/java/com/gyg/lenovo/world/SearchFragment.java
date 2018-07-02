package com.gyg.lenovo.world;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.INotificationSideChannel;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class SearchFragment extends Fragment {

    private SearchTask searchTask = null;

    private View mSearchForm;
    private View mResultForm;
    private Spinner mSpinner;
    private EditText mInput;
    private ListView mResult;

    private MyApp myApp;

    private List<String> id;

    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myApp = (MyApp) getActivity().getApplication();
        mSearchForm = view.findViewById(R.id.search_form);
        mResultForm = view.findViewById(R.id.result_form);
        mSpinner = (Spinner) view.findViewById(R.id.search_spinner);
        mInput = (EditText) view.findViewById(R.id.input);
        mResult = (ListView) view.findViewById(R.id.search_result);

        Button mSearchButton = (Button) view.findViewById(R.id.search_button);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSearch();
            }
        });

        Button mReturnButton = (Button) view.findViewById(R.id.return_button);
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
            id = new ArrayList<>();

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
            Log.i("in click","yes");
            mResult.setOnItemClickListener(new ListView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long i) {
                    Intent intent = new Intent();
                    Bundle b = new Bundle();
                    Log.i("in click","yes");
                    b.putInt("id", Integer.valueOf(id.get(position)));
                    b.putBoolean("isFriend", true);
                    intent.putExtra("data",b);
                    intent.setClassName("com.gyg.lenovo.world",
                            "com.gyg.lenovo.world.PersonActivity");
                    startActivity(intent);
                }
            });
            Log.i("setclicklistener","success");
        }



        @Override
        protected void onCancelled() {
            super.onCancelled();
            searchTask = null;
            showList(false);
        }
    }
}
