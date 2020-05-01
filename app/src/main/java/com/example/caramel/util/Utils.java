package com.example.caramel.util;

import com.example.caramel.persist.Position;

import java.util.ArrayList;
import java.util.Iterator;

public class Utils {

    public static void updatePositionsList(ArrayList<Position> positions, int categoryId) {
        Iterator<Position> iterator = positions.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getCategoryId() != categoryId) {
                iterator.remove();
            }
        }
    }
}
