package com.aj.notepad;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mList;
    final Context context = this;
    EditText et_new_title, et_new_user;

    SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Notepad> notepadList;
    private NotepadAdapter adapter;
    private String url = "http://192.168.31.100:8080/";
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mList = findViewById(R.id.main_list);

        notepadList = new ArrayList<>();

        adapter = new NotepadAdapter(getApplicationContext(),notepadList);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());

        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        mList.addItemDecoration(dividerItemDecoration);
        mList.setAdapter(adapter);
        getData();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.simpleSwipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            swipeRefreshLayout.setRefreshing(false);
            getUpdatedData();
        }, 2000));



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {

            AlertDialog builder = onCreateDialog(savedInstanceState);
            builder.show();

            adapter.notifyDataSetChanged();
        });

    }


    public AlertDialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater li = LayoutInflater.from(context);
        View createNewView = li.inflate(R.layout.create_new_form, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        et_new_title = (EditText) createNewView.findViewById(R.id.title);
        et_new_user = (EditText) createNewView.findViewById(R.id.user);
        builder.setView(createNewView)
                // Add action buttons

                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        String title = String.valueOf(et_new_title.getText());
                        String user = String.valueOf(et_new_user.getText());
                        sendData(title, user, "");
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
       return builder.create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendData(String title, String user, String text){
        Map<String, String> jsonRequest = new HashMap<>();
        jsonRequest.put("title",title);
        jsonRequest.put("text",text);
        jsonRequest.put("user",user);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "/notepad/create", new JSONObject(jsonRequest), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(MainActivity.this, String.valueOf(response), Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, String.valueOf(error), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

    }

    private void getData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,url+"notepads", null, response -> {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject jsonObject = response.getJSONObject(i);

                    Notepad notepad = new Notepad();
                    notepad.setTitle(jsonObject.getString("title"));
                    notepad.setText(jsonObject.getString("text"));
                    notepad.setUser(jsonObject.getString("user"));
                    notepad.setId(jsonObject.getLong("id"));

                    notepadList.add(notepad);
                    //Toast.makeText(MainActivity.this, "Response",Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
        }, error -> {
            Log.e("Errrrrrr",String.valueOf(error));
            Toast.makeText(MainActivity.this, String.valueOf(error),Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        });


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);

    }

    private void getUpdatedData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        notepadList = new ArrayList<>();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,url+"notepads", null, response -> {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject jsonObject = response.getJSONObject(i);

                    Notepad notepad = new Notepad();
                    notepad.setTitle(jsonObject.getString("title"));
                    notepad.setText(jsonObject.getString("text"));
                    notepad.setUser(jsonObject.getString("user"));
                    notepad.setId(jsonObject.getLong("id"));

                    notepadList.add(notepad);
                    //Toast.makeText(MainActivity.this, "Response",Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }
            adapter.updateData(notepadList);
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
        }, error -> {
            Log.e("Errrrrrr",String.valueOf(error));
            Toast.makeText(MainActivity.this, String.valueOf(error),Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        });


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);

    }
}
