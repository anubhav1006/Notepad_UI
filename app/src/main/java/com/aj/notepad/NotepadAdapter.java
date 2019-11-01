package com.aj.notepad;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class NotepadAdapter extends RecyclerView.Adapter<NotepadAdapter.ViewHolder>{

    private Context context;
    private List<Notepad> list;

    public NotepadAdapter(Context context, List<Notepad> list) {
        this.context = context;
        this.list = list;
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
        holder.textUser.setText("By:- "+notepad.getUser());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView textTitle, textText, textUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.main_title);
            //textText = itemView.findViewById(R.id.main_text);
            textUser = itemView.findViewById(R.id.main_user);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Notepad notepad = list.get(getAdapterPosition());

        }
    }
}
