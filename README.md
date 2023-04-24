# Setup database
```
sudo mysql
CREATE DATABASE freshify_dev;
CREATE USER 'freshify_dev'@'localhost' IDENTIFIED BY 'freshify_dev';
GRANT CREATE, ALTER, DROP, INSERT, UPDATE, DELETE, SELECT, REFERENCES on freshify_dev.* TO 'freshify_dev'@'localhost' WITH GRANT OPTION;
```
