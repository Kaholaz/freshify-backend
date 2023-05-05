docker run --rm -p 56000:7000 --name freshify_dev_database -e MYSQL_ROOT_PASSWORD=freshify -e MYSQL_DATABASE=freshify_dev -d mysql --port 7000 --character-set-server=utf8 --collation-server=utf8_unicode_ci
echo "Waiting for database to start..."
sleep 30
mvn spring-boot:start && mvn spring-boot:stop
docker exec -i freshify_dev_database mysql -uroot -pfreshify < ./src/main/resources/insert_item_types.sql
docker exec -i freshify_dev_database mysql -uroot -pfreshify < ./src/main/resources/insert_recipes.sql
docker exec -i freshify_dev_database mysql -uroot -pfreshify < ./src/main/resources/insert_users.sql
docker exec -i freshify_dev_database mysql -uroot -pfreshify < ./src/main/resources/insert_households.sql
docker exec -i freshify_dev_database mysql -uroot -pfreshify < ./src/main/resources/insert_shopping_lists.sql
docker exec -i freshify_dev_database mysql -uroot -pfreshify < ./src/main/resources/insert_items.sql
mvn spring-boot:run
