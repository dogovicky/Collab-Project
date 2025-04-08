import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import './CssSheets/ForgotPassword.css';
import { requestResetCode } from "../api/authA";

const ForgotPassword = () => {
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const handleEmailSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setMessage('');
    
    const resetPasswordUrl = `${window.location.origin}/reset-password`; // Base URL for reset password page
    
    try {
      const response = await requestResetCode({ email, resetPasswordUrl });
      setMessage('If an account exists with this email, you will receive password reset instructions.');
    } catch (err) {
      setError('An error occurred. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="forgot-password-container">
      <div className="forgot-password-form-wrapper">
        <form onSubmit={handleEmailSubmit} className="forgot-password-form">
          <h2>Forgot Password</h2>
          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              type="email"
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              placeholder="Enter your email"
            />
          </div>
          <button 
            type="submit" 
            disabled={loading} 
            className="reset-button"
          >
            {loading ? 'Sending...' : 'Send Reset Link'}
          </button>
          {message && <div className="message success-message">{message}</div>}
          {error && <div className="message error-message">{error}</div>}
          <div className="back-to-login">
            <Link to="/signin">Return to Login</Link>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ForgotPassword;
