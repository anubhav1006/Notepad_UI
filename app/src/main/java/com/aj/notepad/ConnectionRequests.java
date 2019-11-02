package com.aj.notepad;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionRequests implements Connections {

    Context context;
    NotepadAdapter adapter;
    List<Notepad> notepadList;

    public ConnectionRequests(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public NotepadAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(NotepadAdapter adapter) {
        this.adapter = adapter;
    }

    public List<Notepad> getNotepadList() {
        return notepadList;
    }

    public void setNotepadList(List<Notepad> notepadList) {
        this.notepadList = notepadList;
    }

    public ConnectionRequests(Context context, NotepadAdapter adapter, List<Notepad> notepadList) {
        this.context = context;
        this.adapter = adapter;
        this.notepadList = notepadList;
    }

    public ConnectionRequests(Context context, NotepadAdapter adapter) {
        this.context = context;
        this.adapter = adapter;
    }


    @Override
    public void sendData(String title, String user, String text){
        Map<String, String> jsonRequest = new HashMap<>();
        jsonRequest.put("title",title);
        jsonRequest.put("text",text);
        jsonRequest.put("user",user);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, context.getResources().getString(R.string.url) + "/notepad/create", new JSONObject(jsonRequest),
                response -> {},
                error -> {});
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);

    }

    @Override
    public void getData(List<Notepad> notepadList) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,context.getResources().getString(R.string.url)+"notepads", null, response -> {
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
                    //Toast.makeText(context, e.getMessage(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
        }, error -> {
            Log.e("Errrrrrr",String.valueOf(error));
            Toast.makeText(context, String.valueOf(error),Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        });


        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonArrayRequest);

    }

    @Override
    public void getUpdatedData(List<Notepad> notepadList) {

        this.notepadList = new ArrayList<>();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,context.getResources().getString(R.string.url)+"notepads", null, response -> {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject jsonObject = response.getJSONObject(i);

                    Notepad notepad = new Notepad();
                    notepad.setTitle(jsonObject.getString("title"));
                    notepad.setText(jsonObject.getString("text"));
                    notepad.setUser(jsonObject.getString("user"));
                    notepad.setId(jsonObject.getLong("id"));

                    this.notepadList.add(notepad);
                    //Toast.makeText(context, "Response"+response,Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Toast.makeText(context, e.getMessage(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
            adapter.updateData(this.notepadList);
            adapter.notifyDataSetChanged();
        }, error -> {
            Log.e("Errrrrrr",String.valueOf(error));
            Toast.makeText(context, String.valueOf(error),Toast.LENGTH_LONG).show();
        });


        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonArrayRequest);

    }

    @Override
    public void deleteData(long id) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, context.getResources().getString(R.string.url) + "/notepad/delete/"+id,null,
                response ->{

                        //Toast.makeText(context, String.valueOf(response), Toast.LENGTH_SHORT).show();
                },

                error ->{
                    Log.e("Error", String.valueOf(error));
                    Toast.makeText(context, String.valueOf(error), Toast.LENGTH_SHORT).show();
                });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }


    @Override
    public void updatetext(Notepad notepad) {
        long id = notepad.getId();
        Map<String, String> jsonRequest = new HashMap<>();
        jsonRequest.put("title",notepad.getTitle());
        jsonRequest.put("text",notepad.getText());
        jsonRequest.put("user",notepad.getUser());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, context.getResources().getString(R.string.url) + "notepad/update/"+id, new JSONObject(jsonRequest),
                response ->{},
                        //Toast.makeText(context, "Response: "+response, Toast.LENGTH_SHORT).show(),
                error ->
                        Toast.makeText(context, "Error: "+error, Toast.LENGTH_SHORT).show());
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void deleteAll() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, context.getResources().getString(R.string.url) + "notepad/deleteAll", null,
                response ->{},
                        //Toast.makeText(context, "Response: "+response, Toast.LENGTH_SHORT).show(),
                error ->
                        Toast.makeText(context, "Error: "+error, Toast.LENGTH_SHORT).show());
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }


}
