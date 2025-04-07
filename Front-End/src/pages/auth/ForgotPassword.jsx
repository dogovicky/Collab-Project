import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import './CssSheets/ForgotPassword.css';
import { requestResetCode, updatePassword } from "../api/authA";

const ForgotPassword = () => {
  const [step, setStep] = useState(1);//track which step(1=enter email, 2=entercode &new password)
  const [email, setEmail] = useState('');
  const [code, setCode] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
// send reset code
  const handleEmailSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setMessage('');
    try {
      const response = await sendResetCode({ email });
      setMessage(response.message || 'A reset code has been sent to your email.');
      setStep(2);
    } catch (err) {
      setError(err.message || 'Failed to send reset code. Please try again.');
    } finally {
      setLoading(false);
    }
  };
//enter code and new password
  const handlePasswordSubmit = async (e) => {
    e.preventDefault();
    if (newPassword !== confirmPassword) {
      setError('Passwords do not match.');
      return;
    }
    setLoading(true);
    setError('');
    setMessage('');
    try {
      const response = await resetPassword({ code, newPassword });
      setMessage(response.message || 'Your password has been successfully reset.');
      setStep(1);//go back to step1 after succesful reset
      setEmail('');
      setCode('');
      setNewPassword('');
      setConfirmPassword('');
    } catch (err) {
      setError(err.message || 'Failed to reset password. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="forgot-password-container">
      <div className="forgot-password-form-wrapper">
        {step === 1 && (
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
              {loading ? 'Sending...' : 'Send Reset Code'}
            </button>
            {message && <div className="message success-message">{message}</div>}
            {error && <div className="message error-message">{error}</div>}
            <div className="back-to-login">
              <Link to="/signin">Return to Login</Link>
            </div>
          </form>
        )}
        {step === 2 && (
          <form onSubmit={handlePasswordSubmit} className="forgot-password-form">
            <h2>Reset Password</h2>
            <div className="form-group">
              <label htmlFor="code">Reset Code</label>
              <input
                type="text"
                id="code"
                value={code}
                onChange={(e) => setCode(e.target.value)}
                required
                placeholder="Enter the code from your email"
              />
            </div>
            <div className="form-group">
              <label htmlFor="newPassword">New Password</label>
              <input
                type="password"
                id="newPassword"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                required
                placeholder="Enter new password"
              />
            </div>
            <div className="form-group">
              <label htmlFor="confirmPassword">Confirm Password</label>
              <input
                type="password"
                id="confirmPassword"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
                placeholder="Confirm new password"
              />
            </div>
            <button 
              type="submit" 
              disabled={loading} 
              className="reset-button"
            >
              {loading ? 'Resetting...' : 'Reset Password'}
            </button>
            {message && <div className="message success-message">{message}</div>}
            {error && <div className="message error-message">{error}</div>}
            <div className="back-to-login">
              <Link to="/signin">Return to Login</Link>
            </div>
          </form>
        )}
      </div>
    </div>
  );
};

const sendResetCode = async (data) => {
  try {
    const response = await requestResetCode(data); // Call API
    return response;
  } catch (error) {
    throw new Error(error.message || "Failed to send reset code.");
  }
};
//send the new password to the API
const resetPassword = async (data) => {
  try {
    const response = await updatePassword(data); // Call API
    return response;
  } catch (error) {
    throw new Error(error.message || "Failed to reset password.");
  }
};

export default ForgotPassword;
