docker run --rm -p 56000:7000 --name freshify_dev_database -e MYSQL_ROOT_PASSWORD=freshify -e MYSQL_DATABASE=freshify_dev -d mysql --port 7000 --character-set-server=utf8 --collation-server=utf8_unicode_ci
echo "Waiting for database to start..."
timeout /t 10
mvn spring-boot:run &
APP_PID=$!

kill $APP_PID
docker exec -i freshify_dev_database mysql -uroot -pfreshify < ./src/main/resources/insert_item_types.sql
docker exec -i freshify_dev_database mysql -uroot -pfreshify < ./src/main/resources/insert_recipes.sql
mvn spring-boot:run
