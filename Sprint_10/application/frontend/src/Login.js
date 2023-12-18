import React from 'react';
import logo from './images/logo-no-background.png';
import email from './images/email.png';
import password from './images/password.png';
import './App.css';

function App() {
  return (
    <div className="App">
      <img src={logo} alt="logo" />

      <div id="login">
        <h1>Log in</h1>
        <p>Please sign in to continue</p>

        <form action="/login" method="POST">
          <div className="input-field">
            <img className="icon" src={email} alt="email" style={{ width: '30px' }} />
            <input className="field" name="email" type="email" id="email" placeholder="Email" required />
          </div>

          <div className="input-field">
            <img className="icon" src={password} alt="password" style={{ width: '25px' }} />
            <input className="field" name="password" type="password" id="password" placeholder="Password" required />
          </div>

          <a href="">Forgot Password?</a>

          <input id="login-button" type="submit" value="Login" />
        </form>

        <div id="signup">
          Don't have an account? <a href="/register">Sign Up</a>
        </div>
      </div>
    </div>
  );
}

export default App;
