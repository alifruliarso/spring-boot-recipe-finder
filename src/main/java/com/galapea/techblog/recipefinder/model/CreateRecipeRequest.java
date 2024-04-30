package com.galapea.techblog.recipefinder.model;

import java.sql.Blob;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateRecipeRequest {
    String name;
    String description;
    Blob image;
}
