package com.aj.notepad;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotepadAdapter extends RecyclerView.Adapter<NotepadAdapter.ViewHolder> {

    private Context context;
    private List<Notepad> list;
    Connections connections;

    public NotepadAdapter(Context context, List<Notepad> list) {
        this.context = context;
        this.list = list;
        connections = new ConnectionRequests(context);
    }

    public void updateData(List<Notepad> dataset) {
        list.clear();
        list.addAll(dataset);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notepad notepad = list.get(position);

        holder.textTitle.setText(notepad.getTitle());
        //holder.textText.setText(notepad.getText());
        holder.textUser.setText("By:- " + notepad.getUser());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView textTitle, textText, textUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.main_title);
            //textText = itemView.findViewById(R.id.main_text);
            textUser = itemView.findViewById(R.id.main_user);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Notepad notepad = list.get(getAdapterPosition());

            Intent intent = new Intent(context, TextEditor.class);
            intent.putExtra("Notepad", notepad);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);

        }

        @Override
        public boolean onLongClick(View view) {
            //Toast.makeText(context, "LongClicked",Toast.LENGTH_SHORT).show();
            ActionMode currentActionMode;

            // Define the callback when ActionMode is activated
            ActionMode.Callback modeCallBack = new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                    actionMode.setTitle("Actions");
                    actionMode.getMenuInflater().inflate(R.menu.long_click_options, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                    Notepad notepad = list.get(getAdapterPosition());
                    switch (menuItem.getItemId()) {
                        case R.id.remove : {
                            connections.deleteData(notepad.getId());
                            actionMode.finish();
                        }
                        case R.id.change_title: {
                            AlertDialog dialog = onCreateDialog(notepad,"Title");
                            dialog.show();
                            actionMode.finish();
                        }
                        case R.id.change_user: {
                            AlertDialog dialog = onCreateDialog(notepad,"User");
                            dialog.show();
                            actionMode.finish();
                        }
                    }
                    return true;

                }

                @Override
                public void onDestroyActionMode(ActionMode actionMode) {
                    //currentActionMode =null;
                }

            };

            return true;
        }

        public AlertDialog onCreateDialog(Notepad notepad, String modify_item) {

            LayoutInflater li = LayoutInflater.from(context);
            View createNewView = li.inflate(R.layout.modify_single, null);
            EditText et_new;

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            et_new = (EditText) createNewView.findViewById(R.id.modify_single);
            builder.setView(createNewView)
                    // Add action buttons

                    .setPositiveButton(R.string.update, (dialog, id) -> {

                       String data = String.valueOf(et_new.getText());
                       if(modify_item.equals("Title"))
                           notepad.setTitle(data);
                       else if(modify_item.equals("User"))
                           notepad.setUser(data);

                       connections.updatetext(notepad);

                    })
                    .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());
            return builder.create();
        }

    }
}
