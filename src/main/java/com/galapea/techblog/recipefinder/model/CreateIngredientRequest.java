package com.galapea.techblog.recipefinder.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateIngredientRequest {
    String name;
    String measurement;
}
