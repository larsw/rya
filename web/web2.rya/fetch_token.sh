#!/bin/sh

set -e

SERVER=192.168.2.15
PORT=8080
REALM=rya
CLIENT_ID=sparql-frontend
CLIENT_SECRET=615c76ee-7400-40ef-bf1c-72917d16e69b
USERNAME=a-user
PASSWORD=a-user
SCOPES="sparql:query sparql:update keywords"

curl -s \
     -X POST \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "grant_type=password" \
     -d "response_type=token" \
     -d "client_id=$CLIENT_ID" \
     -d "client_secret=$CLIENT_SECRET" \
     -d "username=$USERNAME" \
     -d "password=$PASSWORD" \
     -d "scope=$SCOPES" \
     "http://$SERVER:$PORT/auth/realms/$REALM/protocol/openid-connect/token" \ | jq -r .access_token

