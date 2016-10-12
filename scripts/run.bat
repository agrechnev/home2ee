rem Run the sql script temp.sql with user %1 and password %2 and write output to temp.dat

mysql --user=%1 --password=%2  -t <temp.sql >temp.dat
