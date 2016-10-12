rem Run sql initialization scripts with user %1 and password %2

mysql --user=%1 --password=%2  <%0\..\db_setup.sql
mysql --user=%1 --password=%2  <%0\..\db_inserts.sql
