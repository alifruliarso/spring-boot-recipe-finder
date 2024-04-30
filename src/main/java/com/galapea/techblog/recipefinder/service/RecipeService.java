package com.galapea.techblog.recipefinder.service;

import com.galapea.techblog.recipefinder.AppConstant;
import com.galapea.techblog.recipefinder.entity.Ingredient;
import com.galapea.techblog.recipefinder.entity.Recipe;
import com.galapea.techblog.recipefinder.entity.User;
import com.galapea.techblog.recipefinder.model.CreateIngredientRequest;
import com.galapea.techblog.recipefinder.model.CreateRecipeRequest;
import com.toshiba.mwcloud.gs.Collection;
import com.toshiba.mwcloud.gs.GSException;
import com.toshiba.mwcloud.gs.Query;
import com.toshiba.mwcloud.gs.RowSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RecipeService {
    private final Logger log = LoggerFactory.getLogger(RecipeService.class);
    Collection<String, Recipe> recipeCollection;
    Collection<String, User> userCollection;
    Collection<String, Ingredient> ingredientCollection;

    public RecipeService(
            Collection<String, Recipe> recipeCollection,
            Collection<String, User> userCollection,
            Collection<String, Ingredient> ingredientCollection) {
        this.recipeCollection = recipeCollection;
        this.userCollection = userCollection;
        this.ingredientCollection = ingredientCollection;
    }

    public List<Recipe> fetchAll(String searchRecipe, String searchIngredient) {
        List<Recipe> recipes = new ArrayList<>(0);
        try {
            if (searchIngredient != null && !searchIngredient.isBlank()) {
                log.info("Search by ingredient: {}", searchIngredient.toLowerCase());
                Query<Ingredient> queryIngredient = ingredientCollection.query(
                        "SELECT * FROM " + AppConstant.INGREDIENTS_CONTAINER + " WHERE LOWER(name) LIKE '%"
                                + searchIngredient.toLowerCase() + "%'",
                        Ingredient.class);
                RowSet<Ingredient> rowIngredientSet = queryIngredient.fetch();
                while (rowIngredientSet.hasNext()) {
                    Recipe recipe = fetchOne(rowIngredientSet.next().getRecipeId());
                    if (recipe != null) {
                        recipes.add(recipe);
                    }
                }
                return recipes;
            }

            String tql = "SELECT * ";
            if (searchRecipe != null && !searchRecipe.isBlank()) {
                log.info("Search by recipe:{}", searchRecipe.toLowerCase());
                tql = "SELECT * FROM " + AppConstant.RECIPES_CONTAINER + " WHERE LOWER(name) LIKE '%"
                        + searchRecipe.toLowerCase() + "%'";
            }

            Query<Recipe> query = recipeCollection.query(tql, Recipe.class);
            RowSet<Recipe> rowSet = query.fetch();
            while (rowSet.hasNext()) {
                recipes.add(rowSet.next());
            }
        } catch (GSException e) {
            log.error("Error search recipes", e);
        }
        return recipes;
    }

    public Recipe fetchOne(String id) {
        Query<Recipe> query;
        try {
            query = recipeCollection.query("SELECT * WHERE id='" + id + "'", Recipe.class);
            RowSet<Recipe> rowSet = query.fetch();
            if (rowSet.hasNext()) {
                return rowSet.next();
            }
        } catch (GSException e) {
            log.error("Error fetch a recipe", e);
        }
        return null;
    }

    public Recipe fetchOneByName(String name) throws GSException {
        Query<Recipe> query = recipeCollection.query("SELECT * WHERE name='" + name + "'", Recipe.class);
        RowSet<Recipe> rowSet = query.fetch();
        if (rowSet.hasNext()) {
            return rowSet.next();
        }
        return null;
    }

    public Recipe create(CreateRecipeRequest request, User user) throws GSException {
        Recipe recipe = new Recipe();
        recipe.setId(KeyGenerator.next("rcp"));
        recipe.setName(request.getName());
        recipe.setDescription(request.getDescription());
        recipe.setUserId(user.getId());
        recipe.setCreatedAt(new Date());
        recipeCollection.put(recipe);
        log.info("Created recipe: {}", recipe);
        return recipe;
    }

    public void createWithIngredients(
            User user, CreateRecipeRequest recipeRequest, List<CreateIngredientRequest> ingredients)
            throws GSException {
        recipeCollection.setAutoCommit(false);
        ingredientCollection.setAutoCommit(false);
        log.debug("Trying to fetch by name: {}", recipeRequest.getName());
        Recipe fetchedRecipe = fetchOneByName(recipeRequest.getName());
        log.debug("Fetched {}", fetchedRecipe);

        if (fetchedRecipe != null) {
            return;
        }

        Recipe createdRecipe = create(recipeRequest, user);
        Date createdAt = new Date();
        for (CreateIngredientRequest createIngredientRequest : ingredients) {
            Ingredient ingredient = new Ingredient();
            ingredient.setId(KeyGenerator.next("ing"));
            ingredient.setName(createIngredientRequest.getName());
            ingredient.setMeasurement(createIngredientRequest.getMeasurement());
            ingredient.setCreatedAt(createdAt);
            ingredient.setRecipeId(createdRecipe.getId());
            ingredientCollection.put(ingredient);
            log.info("Created ingredient: {}", ingredient);
        }
        recipeCollection.commit();
        ingredientCollection.commit();
    }

    public List<Ingredient> fetchIngredients(String recipeId) {
        List<Ingredient> ingredients = new ArrayList<>(0);
        try (Query<Ingredient> query = ingredientCollection.query(
                "SELECT * FROM " + AppConstant.INGREDIENTS_CONTAINER + " WHERE recipeId='" + recipeId + "'",
                Ingredient.class)) {
            RowSet<Ingredient> rowSet = query.fetch();
            while (rowSet.hasNext()) {
                ingredients.add(rowSet.next());
            }
        } catch (GSException e) {
            log.error("Error fetch ingredients", e);
        }
        log.info(recipeId + " ingredients: {}", ingredients);
        return ingredients;
    }
}
