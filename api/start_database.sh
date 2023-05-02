docker run --rm -p 56000:7000 --name freshify_dev_database -e MYSQL_ROOT_PASSWORD=freshify -e MYSQL_DATABASE=freshify_dev -d mysql --port 7000
