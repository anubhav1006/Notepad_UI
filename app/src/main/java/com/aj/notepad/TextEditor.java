package com.aj.notepad;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TextEditor extends AppCompatActivity {

    Context context;
    EditText editText;
    TextView tv1, tv2;
    Button saveButton, cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_editor);

        editText = (EditText) findViewById(R.id.editText_editor);
        tv1 = findViewById(R.id.editor_text_title);
        tv2 = findViewById(R.id.editor_text_user);
        saveButton = findViewById(R.id.save_button);
        cancelButton = findViewById(R.id.cancel_button);

        Bundle b;
        b = getIntent().getExtras();
        Notepad notepad= (Notepad) b.get("Notepad");
        editText.setText(notepad.getText());
        tv1.setText(notepad.getTitle());
        tv2.setText("By:- "+notepad.getUser());

        saveButton.setOnClickListener(view -> {

            String text = String.valueOf(editText.getText());
            if(!text.equals(notepad.getText())) {
                sendData(notepad.getTitle(), notepad.getUser(), text, notepad.getId());
            }
            Intent intent = new Intent(TextEditor.this, MainActivity.class);
            startActivity(intent);
        });
        cancelButton.setOnClickListener(view -> {
            String text = String.valueOf(editText.getText());
            if(!text.equals(notepad.getText())) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                Intent intent = new Intent(TextEditor.this, MainActivity.class);
                                startActivity(intent);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(TextEditor.this);
                builder.setMessage("Unsaved Data. Are you sure want to cancel?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }

        });

    }

    private void sendData(String title, String user, String text, Long id){
        Map<String, String> jsonRequest = new HashMap<>();
        jsonRequest.put("title",title);
        jsonRequest.put("text",text);
        jsonRequest.put("user",user);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, getResources().getString(R.string.url) + "notepad/update/"+id, new JSONObject(jsonRequest),
                response ->
                Toast.makeText(TextEditor.this, "Response: "+response, Toast.LENGTH_SHORT).show(),
                error ->
                Toast.makeText(TextEditor.this, "Error: "+error+"@ "+getResources().getString(R.string.url) + "notepad/update/"+id, Toast.LENGTH_SHORT).show());
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

    }
}
