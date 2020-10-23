import { UserManagerSettings } from 'oidc-client'

const config : UserManagerSettings = {
  authority: "http://192.168.2.15:8080/auth/realms/rya",
  client_id: "sparql-frontend",
  response_type: "code",
  scope: "openid sparql sparql:query sparql:update keywords",
  redirect_uri: window.location.origin + window.location.pathname + "callback"
}

export default config

