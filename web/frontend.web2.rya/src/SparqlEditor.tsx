import React, { useEffect } from 'react'
import Yasgui from '@sral/yasgui'

export const SparqlEditor = (props: any) => {
  useEffect(() => {
    // eslint-disable-next-line
    console.log('setting up Yasgui')
    const yasgui = new Yasgui(document.getElementById('yasgui')!, {
      autofocus: false,
      requestConfig: {
        headers: () => ({
          Authorization: 'Bearer ' + props.accessToken,
        }),
      },
    })
    yasgui.on('query', (instance: Yasgui, tab) => {
      console.log(instance)
    })
  }, [props.accessToken])
  return <div id="yasgui" />
}
