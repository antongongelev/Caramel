package com.example.caramel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Utils {

    public static void updatePositionsList(ArrayList<Position> positions, int categoryId) {
        Iterator<Position> iterator = positions.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getCategoryId() != categoryId) {
                iterator.remove();
            }
        }
    }

    public static double getFilteredRevenue(int categoryId, List<Position> soldPositions) {
        double filteredRevenue = 0;
        for (Position soldPosition : soldPositions) {
            if (soldPosition.getCategoryId() == categoryId) {
                filteredRevenue += soldPosition.getPrice();
            }
        }
        return filteredRevenue;
    }
}
