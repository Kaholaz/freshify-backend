# Freshify backend
This is the backend for freshify.no. The backend can be accessed on api.freshify.no. It can also be run locally.

## Dependencies
- Docker v23.0.2
- Apache Maven v3.6.3
- OpenJDK Runtime Environment v17.0.6

## How to run
For both linux and Windows, the backend can be easily started using using a simple shell script.

### Linux
Open a terminal in the root of this project.
```
cd api
docker pull mysql
./start_application.sh
```

### Windows
Open a command prompt in the root of this project.
```
cd api
docker pull mysql
start_application.bat
```

## How to use
After launching the backend with the script, the database should be populated with various test data. 

Two users are created:
- username: super@user.com
- password: superuser
- username: normal@user.no
- password: normaluser

The users are both in the same household, with the roles superuser and user respectively. The household comes loaded with various test data to explore the statistics, inventory, shopping list, and recipe features.

