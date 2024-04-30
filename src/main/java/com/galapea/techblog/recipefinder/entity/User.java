package com.galapea.techblog.recipefinder.entity;

import com.toshiba.mwcloud.gs.RowKey;
import java.util.Date;
import lombok.Data;

@Data
public class User {
    @RowKey
    String id;

    String email;
    String fullName;
    Date createdAt;
}
