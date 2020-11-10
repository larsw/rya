#!/bin/sh

set -e

PROTOCOL=https
SERVER=keycloak.localhost
PORT=443
REALM=rya
CLIENT_ID=sparql-frontend
USERNAME=a-user
PASSWORD=a-user
SCOPES="sparql:query sparql:update keywords"

TOKEN_ENDPOINT="$PROTOCOL://$SERVER:$PORT/auth/realms/$REALM/protocol/openid-connect/token"

curl -s \
     -k \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "grant_type=password" \
     -d "response_type=token" \
     -d "client_id=$CLIENT_ID" \
     -d "client_secret=$CLIENT_SECRET" \
     -d "username=$USERNAME" \
     -d "password=$PASSWORD" \
     -d "scope=$SCOPES" \
     $TOKEN_ENDPOINT | jq -r .access_token

