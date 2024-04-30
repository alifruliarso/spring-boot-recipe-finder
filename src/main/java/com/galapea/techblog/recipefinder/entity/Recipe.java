package com.galapea.techblog.recipefinder.entity;

import com.toshiba.mwcloud.gs.RowKey;
import java.sql.Blob;
import java.util.Date;
import lombok.Data;

@Data
public class Recipe {
    @RowKey
    String id;

    String name;
    String description;
    Blob image;
    String userId;
    Date createdAt;
}
