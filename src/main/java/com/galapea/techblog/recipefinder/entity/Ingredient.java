package com.galapea.techblog.recipefinder.entity;

import com.toshiba.mwcloud.gs.RowKey;
import java.util.Date;
import lombok.Data;

@Data
public class Ingredient {
    @RowKey
    String id;

    String name;
    String measurement;
    String recipeId;
    Date createdAt;
}
