#!/bin/sh

SERVER=keycloak.localhost
REALM=rya
CLIENT_ID=web2
CLIENT_SECRET=615c76ee-7400-40ef-bf1c-72917d16e69b
USERNAME=a-user
PASSWORD=a-user

curl -s \
     -X POST \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "grant_type=password" \
     -d "response_type=token" \
     -d "client_id=$CLIENT_ID" \
     -d "client_secret=$CLIENT_SECRET" \
     -d "username=$USERNAME" \
     -d "password=$PASSWORD" \
     "http://$SERVER/auth/realms/$REALM/protocol/openid-connect/token" \ | jq -r .access_token

