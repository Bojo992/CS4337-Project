#!/bin/bash
mysqld --initialize-insecure --user=mysql

until mysql -h master-db -u root -ppassword -e "SELECT 1" > /dev/null 2>&1; do
  echo "Waiting for primary to be available..."
  sleep 5
done

MASTER_STATUS=$(mysql -h master-db -u root -ppassword -e "SHOW BINARY LOG STATUS;")
LOG_FILE=$(echo "$MASTER_STATUS" | grep -v File | awk '{ print $1 }')
LOG_POS=$(echo "$MASTER_STATUS" | grep -v File | awk '{ print $2 }')

mysqld

mysql -u root -e "
CHANGE REPLICATION SOURCE TO
  SOURCE_HOST='master-db',
  SOURCE_USER='replica',
  SOURCE_PASSWORD='replicapassword',
  SOURCE_LOG_FILE='$LOG_FILE',
  SOURCE_LOG_POS=$LOG_POS;
"
mysql -u root -e "START REPLICA;"
