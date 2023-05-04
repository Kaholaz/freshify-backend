docker run --rm -p 56000:7000 --name freshify_dev_database -e MYSQL_ROOT_PASSWORD=freshify -e MYSQL_DATABASE=freshify_dev -d mysql --port 7000
echo "Waiting for database to start..."
sleep 10
mvn spring-boot:start && mvn spring-boot:stop
docker exec -i freshify_dev_database mysql -uroot -pfreshify < ./src/main/resources/insert_item_types.sql
mvn spring-boot:run
