import React, { useState } from 'react'
import '@sral/yasgui/build/yasgui.min.css'
import { UserData } from 'react-oidc'
import { Form } from 'react-bootstrap'
import { SparqlEditor } from './SparqlEditor'

const App = () => {
  const [visibility, setVisibility] = useState('')

  return (
    <UserData.Consumer>
      {(context) => (
        <div>
          <Form.Group controlId="formVisibility" style={{ marginTop: '1em' }}>
            <Form.Control
              type="text"
              placeholder="Enter visiblity for new or updated triples (must match authorization(s))"
              value={visibility}
              onChange={(e) => setVisibility(e.target.value)}
            />
          </Form.Group>
          <SparqlEditor accessToken={context.user!.access_token} />
        </div>
      )}
    </UserData.Consumer>
  )
}

export default App
