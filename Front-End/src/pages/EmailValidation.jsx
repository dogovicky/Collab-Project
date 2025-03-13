import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const EmailValidation = () => {
  const [email, setEmail] = useState('');
  const [isValid, setIsValid] = useState(true);
  const [codeSent, setCodeSent] = useState(false);
  const [userCode, setUserCode] = useState('');
  const [isVerified, setIsVerified] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const validateEmail = (email) => {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(String(email).toLowerCase());
  };

  const handleChange = (e) => {
    setEmail(e.target.value);
    setIsValid(validateEmail(e.target.value));
  };

  const handleSendCode = async () => {
    if (isValid) {
      setLoading(true);
      setError(null);
      try {
        const response = await axios.post('/api/send-verification', { email });
        setCodeSent(true);
        alert('Verification code sent to your email!');
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to send verification code');
      } finally {
        setLoading(false);
      }
    } else {
      alert('Please enter a valid email address.');
    }
  };

  const handleVerifyCode = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await axios.post('/api/verify-code', {
        email,
        code: userCode
      });
      setIsVerified(true);
      alert('Email verified successfully!');
      // Redirect to home page after successful verification
      navigate('/home');
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid verification code');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2>Email Validation</h2>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <div>
        <label>Email:</label>
        <input
          type="email"
          value={email}
          onChange={handleChange}
          style={{ borderColor: isValid ? 'black' : 'red' }}
          disabled={loading}
        />
        <button 
          onClick={handleSendCode} 
          disabled={!isValid || codeSent || loading}
        >
          {loading ? 'Sending...' : 'Send Verification Code'}
        </button>
      </div>
      {codeSent && (
        <div>
          <label>Enter Verification Code:</label>
          <input
            type="text"
            value={userCode}
            onChange={(e) => setUserCode(e.target.value)}
            disabled={loading}
          />
          <button 
            onClick={handleVerifyCode}
            disabled={loading}
          >
            {loading ? 'Verifying...' : 'Verify Code'}
          </button>
        </div>
      )}
      {!isValid && <p style={{ color: 'red' }}>Please enter a valid email address.</p>}
      {isVerified && <p style={{ color: 'green' }}>Email verified successfully!</p>}
    </div>
  );
};

export default EmailValidation;
