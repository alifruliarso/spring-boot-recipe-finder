package com.galapea.techblog.recipefinder.controller;

import com.galapea.techblog.recipefinder.entity.Recipe;
import com.galapea.techblog.recipefinder.service.RecipeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipesController {
    private static final Logger log = LoggerFactory.getLogger(RecipesController.class);
    private final RecipeService recipeService;

    /**
     * Fetches all recipes from the database, optionally filtering by keyword and/or
     * keywordIngredient.
     *
     * @param model The model to which the fetched recipes are added
     * @param keyword An optional keyword to filter recipes by
     * @param keywordIngredient An optional keyword to filter recipes by ingredients
     * @return The view name "recipes" to render
     */
    @GetMapping
    String recipes(Model model, String keyword, String keywordIngredient) {
        List<Recipe> recipes = recipeService.fetchAll(keyword, keywordIngredient);
        model.addAttribute("recipes", recipes);
        if (keywordIngredient != null && !keywordIngredient.isBlank()) {
            model.addAttribute("keywordIngredient", keywordIngredient);
        } else {
            model.addAttribute("keyword", keyword);
        }
        return "recipes";
    }

    /**
     * Retrieves a recipe based on the provided id, fetches ingredients for the recipe, and adds the
     * recipe and its ingredients to the model.
     *
     * @param model The model to which recipe and ingredients are added
     * @param id The id of the recipe to fetch
     * @return The view name "ingredient" to render
     */
    @GetMapping("/{id}")
    String recipe(Model model, @PathVariable("id") String id) {
        Recipe recipe = recipeService.fetchOne(id);
        log.info("Fetched recipe: {}", recipe);
        model.addAttribute("recipe", recipe);
        model.addAttribute("ingredients", recipeService.fetchIngredients(id));
        return "ingredient";
    }
}
