package com.aj.notepad;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, PopupMenu.OnMenuItemClickListener {

        View v;
        PopupMenu popup;
        public TextView textTitle, textText, textUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textTitle = itemView.findViewById(R.id.main_title);
            //textText = itemView.findViewById(R.id.main_text);
            textUser = itemView.findViewById(R.id.main_user);
            this.v = itemView;
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
            //Toast.makeText(context, "LongClicked", Toast.LENGTH_SHORT).show();
            Context wrapper = new ContextThemeWrapper(context, R.style.MyPopupMenu);
            popup = new PopupMenu(wrapper, view);
            popup.setOnMenuItemClickListener(this);// to implement on click event on items of menu
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.long_click_options, popup.getMenu());
            popup.show();
            return true;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            Notepad notepad = list.get(getAdapterPosition());
            switch (menuItem.getItemId()) {
                case R.id.remove: {
                    connections.deleteData(notepad.getId());
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    break;
                }
                case R.id.change_title: {

                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupView = inflater.inflate(R.layout.modify_single, null);

                    // create the popup window
                    int width = LinearLayout.LayoutParams.MATCH_PARENT;
                    int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    boolean focusable = true; // lets taps outside the popup also dismiss it
                    final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                    // show the popup window
                    // which view you pass in doesn't matter, it is only used for the window tolken
                    popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);


                    EditText et_new;
                    et_new = (EditText) popupView.findViewById(R.id.modify_single);
                    // dismiss the popup window when touched
                    Button button = (Button) popupView.findViewById(R.id.save_single);
                    button.setOnClickListener(view -> {
                        String data = String.valueOf(et_new.getText());
                        notepad.setTitle(data);
                        connections.updatetext(notepad);
                        notifyDataSetChanged();
                        popupWindow.dismiss();
                    });
                    popupView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            popupWindow.dismiss();
                            return true;
                        }
                    });
                    break;
                }
                case R.id.change_user: {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupView = inflater.inflate(R.layout.modify_single, null);

                    int width = LinearLayout.LayoutParams.MATCH_PARENT;
                    int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    boolean focusable = true;
                    final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);


                    popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);


                    EditText et_new;
                    et_new = (EditText) popupView.findViewById(R.id.modify_single);
                    Button button = (Button) popupView.findViewById(R.id.save_single);
                    button.setOnClickListener(view -> {
                        String data = String.valueOf(et_new.getText());
                        notepad.setUser(data);
                        connections.updatetext(notepad);
                        notifyDataSetChanged();
                        popupWindow.dismiss();
                    });
                    popupView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            popupWindow.dismiss();
                            return true;
                        }
                    });
                    break;
                }
            }
            return true;
        }
    }
}
