import React, { useEffect } from 'react';
import logo from './logo.svg';
import './App.css';
import Yasgui from "@triply/yasgui";
import "@triply/yasgui/build/yasgui.min.css";

function YG() {
  useEffect(() => {
    const yasgui = new Yasgui(document.getElementById("yasgui")!, {});
  });

  return <div id="yasgui" />;
}

function App() {
  return (
    <div className="App">
      <YG />
    </div>
  );
}

export default App;
