import { UserManagerSettings } from 'oidc-client'

const basePath = window.location.origin + window.location.pathname

const config : UserManagerSettings = {
  authority: "https://keycloak.localhost/auth/realms/rya",
  client_id: "sparql-frontend",
  response_type: "code",
  scope: "openid sparql sparql:query sparql:update keywords",
  redirect_uri: window.location.origin + window.location.pathname + "callback",
  post_logout_redirect_uri: "https://ryaweb.localhost:8082/"
}

export default config
