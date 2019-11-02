package com.aj.notepad;

import java.util.List;

public interface Connections {

     void sendData(String title, String user, String text);
     void getData(List<Notepad> notepadList);
     void getUpdatedData(List<Notepad> notepadList);
     void deleteData(long id);
     void updatetext(Notepad notepad);
}
