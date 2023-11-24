import React from 'react';
import logo from './images/logo-no-background.png';
import email from './images/email.png';
import password from './images/password.png';
import usericon from './images/usericon.png'; 

import './App.css';

function App() {
  return (
    <div className="App">
      <h1 style={{ textAlign: 'center' }}>Create Account</h1>

      <div id="login">
        <form action="/register" method="POST">
          <div className="input-field">
            <img className="icon" src={usericon} alt="user" style={{ width: '30px' }} />
            <input className="field" name="name" type="text" id="name" placeholder="Full Name" required />
          </div>

          <div className="input-field">
            <img className="icon" src={email} alt="email" style={{ width: '30px' }} />
            <input className="field" name="email" type="email" id="email" placeholder="Email" required />
          </div>

          <div className="input-field">
            <img className="icon" src={password} alt="password" style={{ width: '25px' }} />
            <input className="field" name="password" type="password" id="password" placeholder="Password" required />
          </div>

          <div className="input-field">
            <img className="icon" src={password} alt="password" style={{ width: '25px' }} />
            <input className="field" name="confirm" type="password" id="confirm" placeholder="Confirm Password" required />
          </div>

          <input id="login-button" type="submit" value="Register" />
        </form>

        <div id="signup">
          Already have an account? <a href="/login">Log in</a>
        </div>
      </div>
    </div>
  );
}

export default App;
