import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';
import { makeAuthenticator, makeUserManager, Callback } from 'react-oidc'
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link
} from 'react-router-dom'

import userManagerConfig from './oidcConfig'

const userManager = makeUserManager(userManagerConfig)
const AppWithAuth = makeAuthenticator({
  userManager: userManager
})(App)

ReactDOM.render(
  <React.StrictMode>
    <Router>
      <Switch>
        <Route
          path="/callback"
          render={routeProps => (
            <Callback
              onSuccess={user => {
                // `user.state` will reflect the state that was passed in via signinArgs.
                routeProps.history.push('/')
              }}
              userManager={userManager}
            />
          )}
        />
        <AppWithAuth />
      </Switch>
    </Router>
  </React.StrictMode>,
  document.getElementById('root')
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();

