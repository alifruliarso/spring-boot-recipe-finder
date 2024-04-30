package com.galapea.techblog.recipefinder.config;

import com.galapea.techblog.recipefinder.AppConstant;
import com.galapea.techblog.recipefinder.entity.Ingredient;
import com.galapea.techblog.recipefinder.entity.Recipe;
import com.galapea.techblog.recipefinder.entity.User;
import com.toshiba.mwcloud.gs.*;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GridDBConfig {

    @Value("${GRIDDB_NOTIFICATION_MEMBER}")
    private String notificationMember;

    @Value("${GRIDDB_CLUSTER_NAME}")
    private String clusterName;

    @Value("${GRIDDB_USER}")
    private String user;

    @Value("${GRIDDB_PASSWORD}")
    private String password;

    @Bean
    public GridStore gridStore() throws GSException {
        // Acquiring a GridStore instance
        Properties properties = new Properties();
        properties.setProperty("notificationMember", notificationMember);
        properties.setProperty("clusterName", clusterName);
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        GridStore store = GridStoreFactory.getInstance().getGridStore(properties);
        return store;
    }

    @Bean
    public Collection<String, User> userCollection(GridStore gridStore) throws GSException {
        Collection<String, User> collection = gridStore.putCollection(AppConstant.USERS_CONTAINER, User.class);
        collection.createIndex("email");
        return collection;
    }

    @Bean
    public Collection<String, Recipe> recipeCollection(GridStore gridStore) throws GSException {
        gridStore.dropCollection(AppConstant.RECIPES_CONTAINER);
        Collection<String, Recipe> collection = gridStore.putCollection(AppConstant.RECIPES_CONTAINER, Recipe.class);
        collection.createIndex("name");
        return collection;
    }

    @Bean
    public Collection<String, Ingredient> ingredientCollection(GridStore gridStore) throws GSException {
        gridStore.dropCollection(AppConstant.INGREDIENTS_CONTAINER);
        Collection<String, Ingredient> collection =
                gridStore.putCollection(AppConstant.INGREDIENTS_CONTAINER, Ingredient.class);
        collection.createIndex("name");
        return collection;
    }
}
