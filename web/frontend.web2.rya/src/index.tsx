import React, { ComponentPropsWithRef } from 'react';
import ReactDOM from 'react-dom';
import { Container, Row, Col, Navbar, Nav } from 'react-bootstrap'
import {
  BrowserRouter as Router,
  Switch,
  Route,
} from 'react-router-dom'
import { UserData } from 'react-oidc'
import { makeAuthenticator, makeUserManager, Callback } from 'react-oidc'

import App from './App';
import * as serviceWorker from './serviceWorker';
import userManagerConfig from './oidcConfig'

import './flatly.min.css'
import './index.css'

const userManager = makeUserManager(userManagerConfig)

const Layout = (props : ComponentPropsWithRef<any>) => {
  const children = props.children
  return <UserData.Consumer>
    {context => (<Container fluid>
    <Row>
      <Col>
        <Navbar bg="dark" expand="lg">
          <Navbar.Brand href="#home">Apache Rya Web</Navbar.Brand>
          <Navbar.Toggle aria-controls="basic-navbar-nav" />
          <Navbar.Collapse id="basic-navbar-nav">
            <Nav className="mr-auto">
              <Nav.Link href="#">Home</Nav.Link>
            </Nav>
          </Navbar.Collapse>
          <Navbar.Collapse className="justify-content-end">
            <Navbar.Text>
              {
                context && context.user ? <>Signed in as <b>{context.user.profile.name}</b>{' '}
                 with authorizations: <b>{context.user!.profile.keywords}</b>{' '}
                 (<button className="link-button" onClick={async e => { await context.userManager?.signoutRedirect()}}>Sign out</button>)
                 </>
                : "Not signed in."
              }
            </Navbar.Text>
          </Navbar.Collapse>
        </Navbar>
      </Col>
    </Row>
    <Row>
      <Col>
        {children}
      </Col>
    </Row>
  </Container>)}
</UserData.Consumer>
}
const LayoutWithAuth = makeAuthenticator({userManager: userManager})(Layout)

ReactDOM.render(
  <React.StrictMode>
    <Router>
      <Switch>
        <Route
          path="/callback"
          render={routeProps => (
            <Callback
              onSuccess={user => {
                routeProps.history.push('/')
              }}
              onError={err => { console.error(err) }}
              userManager={userManager}
            />
          )}
        />
        <LayoutWithAuth>
          <App />
        </LayoutWithAuth>
      </Switch>
    </Router>
  </React.StrictMode>,
  document.getElementById('root')
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
