#!/bin/bash
# Run sql initialization scripts with user $1 and password $2

mysql --user=$1 --password=$2 <${BASH_SOURCE%/*}/db_setup.sql
mysql --user=$1 --password=$2 <${BASH_SOURCE%/*}/db_inserts.sql
