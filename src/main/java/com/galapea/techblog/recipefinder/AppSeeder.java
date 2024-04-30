package com.galapea.techblog.recipefinder;

import com.galapea.techblog.recipefinder.entity.User;
import com.galapea.techblog.recipefinder.model.CreateIngredientRequest;
import com.galapea.techblog.recipefinder.model.CreateRecipeRequest;
import com.galapea.techblog.recipefinder.model.CreateUser;
import com.galapea.techblog.recipefinder.service.RecipeService;
import com.galapea.techblog.recipefinder.service.UserService;
import com.toshiba.mwcloud.gs.GSException;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.NamedCsvRecord;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppSeeder implements CommandLineRunner {
    private final Logger log = LoggerFactory.getLogger(AppSeeder.class);
    private final RecipeService recipeService;
    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        log.info("run....");
        seedUsers();
        Instant start = Instant.now();
        seedRecipesFromCsv();
        Instant end = Instant.now();
        log.info(
                "Seed recipes execution time: {} seconds",
                Duration.between(start, end).toSeconds());
    }

    /**
     * Seed recipes from a CSV file.
     *
     * @throws IOException     if an I/O error occurs
     */
    private void seedRecipesFromCsv() throws IOException {
        Resource resource = new ClassPathResource("recipes.csv");
        Path path = Path.of(resource.getURI());
        try (CsvReader<NamedCsvRecord> namedCsv = CsvReader.builder().ofNamedCsvRecord(path)) {
            int recipeCount = 0;
            for (final NamedCsvRecord csvRecord : namedCsv) {
                List<CreateIngredientRequest> ingredients = new ArrayList<>(19);
                for (int idxOfIngredient = 1; idxOfIngredient <= 19; idxOfIngredient++) {
                    if (csvRecord.getField("Quantity" + idxOfIngredient).isBlank()) {
                        break;
                    }
                    String quantityStr = csvRecord.getField("Quantity" + idxOfIngredient);
                    String unitOfMeasure = csvRecord.getField("Unit" + idxOfIngredient);
                    String ingredientName = csvRecord.getField("Ingredient" + idxOfIngredient);
                    ingredients.add(CreateIngredientRequest.builder()
                            .name(ingredientName)
                            .measurement(quantityStr + " " + unitOfMeasure)
                            .build());
                }

                List<String> fields = csvRecord.getFields();
                String recipeName = fields.get(0);
                String recipeDesc = fields.get(1);
                try {
                    User user = userService.getRandomUser();
                    recipeService.createWithIngredients(
                            user,
                            CreateRecipeRequest.builder()
                                    .name(recipeName)
                                    .description(recipeDesc)
                                    .build(),
                            ingredients);
                } catch (GSException e) {
                    log.info("Recipe: {} = {}", recipeName, recipeDesc);
                    ingredients.forEach(ingredient -> {
                        log.info("{}", ingredient);
                    });
                    e.printStackTrace();
                }
                recipeCount += 1;
            }
            log.info("Seeded {} recipes", recipeCount);
        }
    }

    private void seedUsers() throws GSException {
        log.info("seed users....");
        CreateUser user = CreateUser.builder()
                .email("random.rand1@mail.com")
                .fullName("Mr. Random rand")
                .build();
        userService.create(user);
        user = CreateUser.builder()
                .email("woman.random@mail.com")
                .fullName("Mrs Woman Random")
                .build();
        userService.create(user);
        user = CreateUser.builder()
                .email("dom.dom.rand@mail.com")
                .fullName("Dom dom rand")
                .build();
        userService.create(user);
        user = CreateUser.builder()
                .email("makachakalalakala@mail.com")
                .fullName("Makachakalalakala")
                .build();
        userService.create(user);
    }
}
