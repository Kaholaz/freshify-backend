@echo off
docker stop freshify_dev_database >nul 2>&1
docker run --rm -p 56000:7000 --name freshify_dev_database -e MYSQL_ROOT_PASSWORD=freshify -e MYSQL_DATABASE=freshify_dev -d mysql --port 7000 --character-set-server=utf8 --collation-server=utf8_unicode_ci
echo Waiting for database to start...
timeout /t 10
start /B mvn spring-boot:run >spring-boot.log 2>&1
:wait_for_app
findstr /C:"Started FreshifyApiApplication" spring-boot.log >nul 2>&1
if errorlevel 1 (
    echo Waiting for application startup...
    timeout /t 2 /nobreak >nul
    goto wait_for_app
)
echo Application is now running in the background
docker exec -i freshify_dev_database mysql -uroot -pfreshify --default-character-set=utf8 < ./src/main/resources/insert_item_types.sql
docker exec -i freshify_dev_database mysql -uroot -pfreshify --default-character-set=utf8 < ./src/main/resources/insert_recipes.sql
docker exec -i freshify_dev_database mysql -uroot -pfreshify --default-character-set=utf8 < ./src/main/resources/insert_users.sql
docker exec -i freshify_dev_database mysql -uroot -pfreshify --default-character-set=utf8 < ./src/main/resources/insert_households.sql
docker exec -i freshify_dev_database mysql -uroot -pfreshify --default-character-set=utf8 < ./src/main/resources/insert_shopping_lists.sql
docker exec -i freshify_dev_database mysql -uroot -pfreshify --default-character-set=utf8 < ./src/main/resources/insert_items.sql