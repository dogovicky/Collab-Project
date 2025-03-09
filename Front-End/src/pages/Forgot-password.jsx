import React, { useState } from 'react';

const ForgotPassword = () => {
  const [step, setStep] = useState(1);
  const [email, setEmail] = useState('');
  const [code, setCode] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const handleEmailSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setMessage('');
    try {
      // Replace with your actual API call
      await sendResetCode({ email });
      setMessage('A reset code has been sent to your email.');
      setStep(2);
    } catch (err) {
      setError('Failed to send reset code. Please try again.');
    } finally {
      setLoading(false);
    }
  };

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
      // Replace with your actual API call
      await resetPassword({ code, newPassword });
      setMessage('Your password has been successfully reset.');
      setStep(1);
    } catch (err) {
      setError('Failed to reset password. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      {step === 1 && (
        <form onSubmit={handleEmailSubmit}>
          <h2>Forgot Password</h2>
          <label>
            Email:
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </label>
          <button type="submit" disabled={loading}>
            {loading ? 'Sending...' : 'Send Reset Code'}
          </button>
          {message && <p>{message}</p>}
          {error && <p style={{ color: 'red' }}>{error}</p>}
        </form>
      )}
      {step === 2 && (
        <form onSubmit={handlePasswordSubmit}>
          <h2>Reset Password</h2>
          <label>
            Reset Code:
            <input
              type="text"
              value={code}
              onChange={(e) => setCode(e.target.value)}
              required
            />
          </label>
          <label>
            New Password:
            <input
              type="password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              required
            />
          </label>
          <label>
            Confirm Password:
            <input
              type="password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              required
            />
          </label>
          <button type="submit" disabled={loading}>
            {loading ? 'Resetting...' : 'Reset Password'}
          </button>
          {message && <p>{message}</p>}
          {error && <p style={{ color: 'red' }}>{error}</p>}
        </form>
      )}
    </div>
  );
};

// Replace with your actual API call functions
const sendResetCode = async (data) => {
  // Implement API call to send reset code
};

const resetPassword = async (data) => {
  // Implement API call to reset password
};

export default ForgotPassword;
