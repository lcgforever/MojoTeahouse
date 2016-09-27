package com.mojoteahouse.mojotea.util;

import com.mojoteahouse.mojotea.data.Topping;

import java.util.List;

public class DataUtil {

    public static Topping getToppingById(List<Topping> toppingList, String toppingId) {
        if (toppingId == null) {
            return null;
        }
        for (Topping topping : toppingList) {
            if (toppingId.equals(topping.getId())) {
                return topping;
            }
        }
        return null;
    }
}
