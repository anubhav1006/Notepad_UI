package com.aj.notepad;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private RecyclerView mList;
    final Context context = this;
    EditText et_new_title, et_new_user;
    private Connections connections;

    SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Notepad> notepadList;
    private NotepadAdapter adapter;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        mList = findViewById(R.id.main_list);
        notepadList = new ArrayList<>();

        adapter = new NotepadAdapter(getApplicationContext(),notepadList);
        connections = new ConnectionRequests(context, adapter);


        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());

        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        //mList.addItemDecoration(dividerItemDecoration);
        mList.setAdapter(adapter);
        connections.getData(notepadList);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.simpleSwipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            new Handler().postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
                connections.getUpdatedData(notepadList);
            }, 1000);
        });



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {

            AlertDialog builder = onCreateDialog(savedInstanceState);
            builder.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            builder.show();
            adapter.notifyDataSetChanged();

        });


    }


    public AlertDialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater li = LayoutInflater.from(context);
        View createNewView = li.inflate(R.layout.create_new_form, null);
        createNewView.setBackgroundColor(getColor(R.color.light_grey));
        //createNewView.setW

        ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.Theme_AppCompat_Dialog_Alert);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        et_new_title = (EditText) createNewView.findViewById(R.id.title);
        et_new_user = (EditText) createNewView.findViewById(R.id.user);


        builder.setView(createNewView)
                // Add action buttons

                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        String title = String.valueOf(et_new_title.getText());
                        String user = String.valueOf(et_new_user.getText());
                        connections.sendData(title, user, "");
                        Handler handler = new Handler();
                        handler.postDelayed(()->{
                            connections.getUpdatedData(notepadList);
                        },1000);

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
            AlertDialog dialog = onCreateDialog();
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public AlertDialog onCreateDialog() {

        LayoutInflater li = LayoutInflater.from(context);
        View createNewView = li.inflate(R.layout.delete_all, null);
        EditText et_new;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        et_new = (EditText) createNewView.findViewById(R.id.modify_single);
        et_new.setHint("Enter pass");
        builder.setView(createNewView)
                // Add action buttons

                .setPositiveButton(R.string.Enter, (dialog, id) -> {

                    String data = String.valueOf(et_new.getText());
                    if(data.equals("1298")) {
                        connections.deleteAll();
                        dialog.cancel();
                        Handler handler = new Handler();
                        handler.postDelayed(()->{
                            connections.getUpdatedData(notepadList);
                        },1000);
                    }
                    else{
                        Toast.makeText(context, "Wrong pass",Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }

                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());
        return builder.create();
    }


}
