USE freshify_dev;
START TRANSACTION;
INSERT INTO allergen(id, name) VALUES (1, 'Gluten');
INSERT INTO allergen(id, name) VALUES (2, 'Lactose');
INSERT INTO allergen(id, name) VALUES (3, 'Shellfish');
INSERT INTO allergen(id, name) VALUES (4, 'Nuts');
INSERT INTO allergen(id, name) VALUES (5, 'Eggs');
INSERT INTO allergen(id, name) VALUES (6, 'Fish');
INSERT INTO allergen(id, name) VALUES (7, 'Soy');
INSERT INTO allergen(id, name) VALUES (8, 'Wheat');
INSERT INTO allergen(id, name) VALUES (9, 'Sesame');


INSERT INTO recipe_category(id, name) VALUES (1, 'Italian');
INSERT INTO recipe_category(id, name) VALUES (2, 'Meat Dish');
INSERT INTO recipe_category(id, name) VALUES (3, 'Vegetarian');
INSERT INTO recipe_category(id, name) VALUES (4, 'Vegan');
INSERT INTO recipe_category(id, name) VALUES (5, 'Dessert');
INSERT INTO recipe_category(id, name) VALUES (6, 'Appetizer');
INSERT INTO recipe_category(id, name) VALUES (7, 'Salad');
INSERT INTO recipe_category(id, name) VALUES (8, 'Soup');
INSERT INTO recipe_category(id, name) VALUES (9, 'Asian');
INSERT INTO recipe_category(id, name) VALUES (10, 'Mexican');
INSERT INTO recipe_category(id, name) VALUES (11, 'Mediterranean');
INSERT INTO recipe_category(id, name) VALUES (12, 'Beverage');
INSERT INTO recipe_category(id, name) VALUES (13, 'Breakfast');
INSERT INTO recipe_category(id, name) VALUES (14, 'Sandwich');
INSERT INTO recipe_category(id, name) VALUES (15, 'Seafood');

INSERT INTO recipe (id, estimated_time, name, description, steps, image) VALUES (1, 60, 'Pesto Pasta', 'En enkel og smakfull pastarett med hjemmelaget pesto', '1. Kok pastaen i en stor gryte med saltet vann.\n 2. Mens pastaen koker, lag pesto ved å blande basilikum, pinjekjerner, hvitløk, parmesan, olivenolje og salt i en blender eller food processor.\n 3. Sil av pastaen og bland den med pestoen.\n 4.Server varm eller kald.\n', 'https://www.renmat.no/photos/8_Oppskrifter/_large/Pestopasta.jpg');
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (500, 'g', 632, 1);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (2, 'bunter', 52, 1);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (50, 'g', 657, 1);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (2, 'fedd', 340, 1);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (50, 'g', 629, 1);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (100, 'ml', 602, 1);
INSERT INTO recipe_category_association (recipe_id, category_id) VALUES (1, 1);
INSERT INTO recipe_category_association (recipe_id, category_id) VALUES (1, 3);
INSERT INTO recipe_category_association (recipe_id, category_id) VALUES (1, 8);
INSERT INTO recipe_allergen_association (recipe_id, allergen_id) VALUES (1, 1);


INSERT INTO recipe (id, estimated_time, name, description, steps, image) VALUES (2, 30, 'Tomatsuppe', 'En varm og deilig suppe med masse smak av tomat', '1. Varm opp olivenolje i en stor gryte.\n 2. Tilsett løk og hvitløk og stek til de er myke.\n 3. Tilsett tomater, vann, buljongterning og krydder og la det småkoke i 20 minutter.\n 4. Bruk en stavmikser eller blender for å lage en jevn suppe.\n 5. Server varm med brød eller kjeks.\n', 'https://gladkokken.imgix.net/DSC_0638-2-kopi.jpg?auto=format%2Ccompress&crop=focalpoint&fit=crop&fm=jpg&fp-x=0.5&fp-y=0.5&h=1000&q=90&w=1800&s=907bf644bdfc80583227887a779aecd5');
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'l', 966, 2);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'kg', 935, 2);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'stk', 502, 2);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (2, 'fedd', 340, 2);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'stk', 417, 2);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'ts', 776, 2);
INSERT INTO recipe_category_association (recipe_id, category_id) VALUES (2, 8);
INSERT INTO recipe_category_association (recipe_id, category_id) VALUES (2, 7);
INSERT INTO recipe_allergen_association (recipe_id, allergen_id) VALUES (2, 8);


INSERT INTO recipe (id, estimated_time, name, description, steps, image) VALUES (3, 60, 'Grønnsakscurry', 'En smaksrik vegetarisk curry med masse grønnsaker', '1. Varm opp olivenolje i en stor gryte.\n 2. Tilsett løk og hvitløk og stek til de er myke.\n 3. Tilsett currypulver og la det steke i 1 minutt.\n 4. Tilsett grønnsaker, kokosmelk, vann og buljongterning og la det småkoke i 30 minutter.\n 5. Server med ris eller naanbrød.\n', 'https://www.helios.no/globalassets/connect-media/image/52/9484_hotgronnsakscurry2.jpg?preset=recipe-large');
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'stk', 502, 3);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (2, 'fedd', 340, 3);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (2, 'ts', 4, 3);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'boks', 440, 3);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (2, 'stk', 287, 3);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'stk', 624, 3);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'stk', 73, 3);
INSERT INTO recipe_category_association (recipe_id, category_id) VALUES (3, 3);
INSERT INTO recipe_category_association (recipe_id, category_id) VALUES (3, 9);
INSERT INTO recipe_allergen_association (recipe_id, allergen_id) VALUES (3, 2);
INSERT INTO recipe_allergen_association (recipe_id, allergen_id) VALUES (3, 7);


INSERT INTO recipe (id, estimated_time, name, description, steps, image) VALUES (4, 45, 'Kyllingsalat med avocado', 'En frisk og sunn salat med kylling og avocado', '1. Stek kyllingbryst i en stekepanne.\n 2. Kutt opp grønnsaker og avokado i små biter.\n 3. Bland sammen grønnsaker, avokado og kylling i en stor bolle.\n 4. Lag en dressing av olivenolje, sitronsaft, salt og pepper, og hell over salaten.\n 5. Server umiddelbart.\n', 'https://www.bama.no/siteassets/fotoware/2022/12/gf_salater_jan_23-4042.jpg?width=750&height=750&mode=crop');
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (2, 'stk', 469, 4);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'stk', 33, 4);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'stk', 502, 4);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (3, 'stk', 133, 4);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'stk', 3, 4);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (2, 'ss', 602, 4);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'ts', 813, 4);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'ts', 776, 4);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'klype', 643, 4);
INSERT INTO recipe_category_association (recipe_id, category_id) VALUES (4, 7);
INSERT INTO recipe_category_association (recipe_id, category_id) VALUES (4, 3);
INSERT INTO recipe_allergen_association (recipe_id, allergen_id) VALUES (4, 5);
INSERT INTO recipe_allergen_association (recipe_id, allergen_id) VALUES (4, 6);

INSERT INTO recipe (id, estimated_time, name, description, steps, image) VALUES (5, 30, 'Vegansk sjokoladekake', 'En deilig, saftig kake uten animalske produkter', '1. Forvarm ovnen til 180°C.\n 2. Bland sammen mel, sukker, kakao, natron og salt i en bolle.\n 3. Tilsett vegetabilsk olje, eddik, vaniljeekstrakt og vann til blandingen og rør godt sammen.\n 4. Hell røren i en smurt kakeform.\n 5. Stek i ovnen i 25-30 minutter, eller til kaken er gjennomstekt.\n 6. Avkjøl før servering.\n', 'https://fruityfriendly.com/cdn/shop/articles/Design_uten_navn-132.png?v=1663919307&width=1000');
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (2, 'dl', 337, 5);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (3, 'dl', 692, 5);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'dl', 371, 5);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'ts', 578, 5);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'klype', 776, 5);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'dl', 799, 5);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (2, 'ss', 170, 5);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'ts', 995, 5);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (2, 'dl', 967, 5);
INSERT INTO recipe_category_association (recipe_id, category_id) VALUES (5, 5);
INSERT INTO recipe_category_association (recipe_id, category_id) VALUES (5, 15);
INSERT INTO recipe_allergen_association (recipe_id, allergen_id) VALUES (5, 2);
INSERT INTO recipe_allergen_association (recipe_id, allergen_id) VALUES (5, 9);

INSERT INTO recipe (id, estimated_time, name, description, steps, image) VALUES (6, 60, 'Vegetarisk lasagne', 'En smakfull lasagne uten kjøtt', '1. Forvarm ovnen til 200°C.\n 2. Stek løk og hvitløk i en stor stekepanne til løken er myk.\n 3. Tilsett sopp og stek i noen minutter til soppen har blitt myk.\n 4. Tilsett hakkede tomater, tomatpuré, basilikum, salt og pepper. La sausen småkoke i 15 minutter.\n 5. Legg lasagneplater i bunnen av en ildfast form.\n 6. Legg lagvis med saus, spinat og ost.\n 7. Fortsett å legge lasagneplater og lagvis med saus, spinat og ost til formen er full.\n 8. Stek i ovnen i 30-35 minutter, eller til lasagnen er gjennomstekt og osten er smeltet.\n', 'https://gladkokken.imgix.net/vegetar-lasagne-12_2022-02-01-130210_igmt.jpg?auto=format%2Ccompress&crop=focalpoint&fit=crop&fm=jpg&fp-x=0.5&fp-y=0.5&h=1000&q=90&w=1800&s=b50b3c19785c7a8a92380da5d6a58478');
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'stk', 502, 6);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (2, 'fedd', 304, 6);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (200, 'g', 885, 6);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'boks', 936, 6);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (2, 'ss', 938, 6);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'ts', 52, 6);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'klype', 776, 6);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'klype', 643, 6);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (300, 'g', 996, 6);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (150, 'g', 877, 6);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (200, 'g', 607, 6);
INSERT INTO recipe_category_association (recipe_id, category_id) VALUES (6, 3);
INSERT INTO recipe_category_association (recipe_id, category_id) VALUES (6, 11);
INSERT INTO recipe_allergen_association (recipe_id, allergen_id) VALUES (6, 1);
INSERT INTO recipe_allergen_association (recipe_id, allergen_id) VALUES (6, 8);

INSERT INTO recipe (id, estimated_time, name, description, steps, image) VALUES (7, 45, 'Nøttefri brownies', 'En deilig sjokoladekake uten nøtter', '1. Forvarm ovnen til 180°C.\n 2. Smelt smør og sjokolade i en kjele på lav varme. Rør jevnlig.\n 3. Pisk egg og sukker sammen i en stor bolle.\n 4. Rør inn sjokoladeblandingen i eggeblandingen.\n 5. Sikt inn mel, kakao og salt og rør til alt er godt blandet.\n 6. Hell røren i en ildfast form og stek i ovnen i 25-30 minutter.\n 7. Avkjøl kaken før du skjærer den i biter.', 'https://www.detsoteliv.no/sites/default/files/styles/large/public/fields/images/main/dsc06221.jpg?itok=45gBrYx1');
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (150, 'g', 845, 7);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (200, 'g', 818, 7);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (3, 'stk', 171, 7);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (250, 'g', 892, 7);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (100, 'g', 547, 7);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (50, 'g', 371, 7);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'klype', 776, 7);
INSERT INTO recipe_category_association (recipe_id, category_id) VALUES (7, 5);
INSERT INTO recipe_allergen_association (recipe_id, allergen_id) VALUES (7, 4);
INSERT INTO recipe_allergen_association (recipe_id, allergen_id) VALUES (7, 9);

INSERT INTO recipe (id, estimated_time, name, description, steps, image) VALUES (8, 30, 'Gulrotsuppe', 'En varmende suppe på kalde dager', '1. Skrell gulrøttene og kutt dem i små biter.\n 2. Finhakk løken.\n 3. Varm opp olivenolje i en stor kjele og tilsett gulrøtter og løk. Stek i noen minutter til grønnsakene er myke.\n 4. Tilsett vann og buljongterning og la det koke i 20 minutter.\n 5. Bruk en stavmikser for å lage en jevn suppe.\n 6. Tilsett fløte og smak til med salt og pepper.\n 7. Server suppen varm med en skje rømme og hakket gressløk på toppen.', 'https://www.tine.no/_/recipeimage/w_1600%2Ch_900%2Cc_fill%2Cx_960%2Cy_540%2Cg_xy_center/recipeimage/ui9mlyfty1acfhpxykoz.jpg');
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (3, 'stk', 287, 8);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'stk', 502, 8);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (2, 'ss', 602, 8);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'stk', 417, 8);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (5, 'dl', 967, 8);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (2, 'dl', 225, 8);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'klype', 776, 8);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'klype', 643, 8);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'ss', 490, 8);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'ss', 267, 8);
INSERT INTO recipe_category_association (recipe_id, category_id) VALUES (8, 8);
INSERT INTO recipe_category_association (recipe_id, category_id) VALUES (8, 13);
INSERT INTO recipe_allergen_association (recipe_id, allergen_id) VALUES (8, 2);
INSERT INTO recipe_allergen_association (recipe_id, allergen_id) VALUES (8, 4);

INSERT INTO recipe (id, estimated_time, name, description, steps, image) VALUES (9, 60, 'Thai Red Curry', 'En spicy og smaksrik thailandsk rett', '1. Kutt kylling i små biter, og stek i en stor panne til gjennomstekt.\n 2. Kutt løk, paprika og gulrot i små biter og tilsett i pannen sammen med kyllingen. Stek i noen minutter til grønnsakene er myke.\n 3. Tilsett red curry paste og kokosmelk i pannen, og la det koke i 10 minutter.\n 4. Smak til med salt, pepper og evt. litt sukker.\n 5. Server curryen med ris og hakket koriander på toppen.', 'https://images.immediate.co.uk/production/volatile/sites/30/2020/08/thai-red-curry-34c1e6d.jpg?quality=90&webp=true&resize=375,341');
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (500, 'g', 469, 9);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'stk', 502, 9);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'stk', 624, 9);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (3, 'stk', 287, 9);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (2, 'ss', 997, 9);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (4, 'dl', 440, 9);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'klype', 776, 9);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'klype', 643, 9);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'ts', 892, 9);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (2, 'dl', 733, 9);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'ss', 448, 9);
INSERT INTO recipe_category_association (recipe_id, category_id) VALUES (9, 9);
INSERT INTO recipe_category_association (recipe_id, category_id) VALUES (9, 11);
INSERT INTO recipe_allergen_association (recipe_id, allergen_id) VALUES (9, 3);
INSERT INTO recipe_allergen_association (recipe_id, allergen_id) VALUES (9, 7);

INSERT INTO recipe (id, estimated_time, name, description, steps, image) VALUES (10, 45, 'Chili Con Carne', 'En klassisk meksikansk rett', '1. Finhakk løk og hvitløk, og stek i en stor panne sammen med karbonadedeig.\n 2. Tilsett paprika og chili, og stek videre i noen minutter.\n 3. Tilsett hermetiske tomater, røde bønner, og krydder i pannen. La det småkoke i 30-40 minutter.\n 4. Server chili con carne med ris og en skje rømme på toppen.', 'https://thecozycook.com/wp-content/uploads/2022/11/Chili-Con-Carne-2.jpg');
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (500, 'g', 395, 10);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'stk', 502, 10);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (2, 'fedd', 340, 10);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'stk', 624, 10);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (2, 'stk', 137, 10);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (3, 'stk', 934, 10);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'boks', 85, 10);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'ts', 140, 10);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'ts', 998, 10);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'ts', 626, 10);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'ts', 776, 10);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'klype', 643, 10);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (2, 'dl', 733, 10);
INSERT INTO recipe_ingredient (amount, unit, item_type_id, recipe_id) VALUES (1, 'ss', 490, 10);
INSERT INTO recipe_category_association (recipe_id, category_id) VALUES (10, 10);
INSERT INTO recipe_category_association (recipe_id, category_id) VALUES (10, 14);
INSERT INTO recipe_allergen_association (recipe_id, allergen_id) VALUES (10, 2);
INSERT INTO recipe_allergen_association (recipe_id, allergen_id) VALUES (10, 4);
COMMIT