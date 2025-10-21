package com.magicyuan.print.swing;

import com.magicyuan.print.dto.PrintDTO;

import javax.swing.*;
import java.util.ArrayList;

public class MyListDataModel extends AbstractListModel<PrintDTO> {

    private ArrayList<PrintDTO> anArrayList;

    public MyListDataModel(ArrayList<PrintDTO> anArrayList) {
        this.anArrayList = anArrayList;
    }

    public int getSize() {
        return anArrayList.size();
    }

    public PrintDTO getElementAt(int index) {
        return anArrayList.get(index);
    }
}
