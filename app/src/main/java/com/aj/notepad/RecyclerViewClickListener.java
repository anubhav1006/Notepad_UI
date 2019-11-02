package com.aj.notepad;

import android.view.View;

public interface RecyclerViewClickListener extends View.OnClickListener {
    void onClick(View view, int position);

    @Override
    void onClick(View view);
}
