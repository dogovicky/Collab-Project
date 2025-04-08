import React, { useState } from "react";
import { Link } from "react-router-dom";
import "./CssSheets/ForgotPassword.css";
import { requestResetCode } from "../api/authA";
import axios from "axios";

const ForgotPassword = () => {
  const [email, setEmail] = useState("");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [token, setToken] = useState("");
  const [passwordResetRequest, setPasswordResetRequest] = useState({
    email: "",
  });

  const handleEmailSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setMessage("");

    // const resetPasswordUrl = `${window.location.origin}/reset-password`; // Base URL for reset password page
    const API_URL = "http://localhost:8080/auth/forgot-password";

    try {
      // const response = await requestResetCode({ email, resetPasswordUrl });
      const response = await axios.post(API_URL, passwordResetRequest, {
        headers: {
          "Content-Type": "application/json",
        },
      });
      if (response.data.status === 200) {
        console.log(response.data);
        setToken(response.data.token);
        localStorage.setItem("authToken", response.data);
        setMessage("Password reset instructions have been sent to your email.");
      } else {
        console.error(
          "Error sending password reset email:",
          response.data.message
        );
      }
      setMessage(
        "If an account exists with this email, you will receive password reset instructions."
      );
    } catch (err) {
      setError("An error occurred. Please try again later.");
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
              name="email"
              value={passwordResetRequest.email}
              onChange={(e) => {
                setPasswordResetRequest((prev) => ({
                  ...prev,
                  email: e.target.value,
                }));
              }}
              required
              placeholder="Enter your email"
            />
          </div>
          <button type="submit" disabled={loading} className="reset-button">
            {loading ? "Sending..." : "Send Reset Link"}
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
