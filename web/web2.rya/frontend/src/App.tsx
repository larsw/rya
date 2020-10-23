import React, { useEffect, useState } from 'react'
import './App.css'
import Yasgui from '@triply/yasgui'
import '@triply/yasgui/build/yasgui.min.css'
import { UserData } from 'react-oidc'

const YG = (props:any) => {
  useEffect(() => {
    // eslint-disable-next-line
    console.log("setting up Yasgui")
    const yasgui = new Yasgui(document.getElementById("yasgui")!, {
      autofocus: false,
      requestConfig: {
        headers: () => ({
           Authorization: "Bearer " + props.accessToken 
        })
      }
    })
    yasgui.on("query", (instance: Yasgui, tab) => {
      console.log(instance)
    })
  }, [props.accessToken])
  return <div id="yasgui" />
}

const App = () => {
  const [visibility, setVisibility] = useState("")


  return (<UserData.Consumer>
     {context => (<div> 
       <div>
         Logged in as: <b>{context.user!.profile.name!} (<em>{context.user!.profile.email!}</em>)</b>
         <p>Authorizations: <b>{context.user!.profile.keywords}</b><br />
         Visiblity: <input type="text" value={visibility} onChange={e => setVisibility(e.target.value)} />
         </p>
       </div>
       
       <YG accessToken={context.user!.access_token} />
     </div>)
     }
   </UserData.Consumer>
  )
}

export default App

