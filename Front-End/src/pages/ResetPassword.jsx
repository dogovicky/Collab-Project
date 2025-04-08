import React, { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import "./CssSheets/ForgotPassword.css";

const ResetPassword = () => {
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const { token } = useParams();
  const navigate = useNavigate();
  const [resetPasswordRequest, setResetPasswordRequest] = useState({
    newPassword: "",
  });
  const handleChange = (e) => {
    const { name, value } = e.target;
    setResetPasswordRequest((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (password !== confirmPassword) {
      setError("Passwords do not match");
      return;
    }

    setLoading(true);
    setError("");
    try {
      const response = await fetch("/auth/reset-password", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ resetPasswordRequest }),
      });
      const data = await response.json();

      if (response.ok) {
        setMessage("Password reset successful");
        setTimeout(() => navigate("/signin"), 2000);
      } else {
        setError(data.message);
      }
    } catch (err) {
      setError("Failed to reset password. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="forgot-password-container">
      <div className="forgot-password-form-wrapper">
        <form onSubmit={handleSubmit} className="forgot-password-form">
          <h2>Reset Password</h2>
          <div className="form-group">
            <label htmlFor="password">New Password</label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              minLength="6"
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
              minLength="6"
            />
          </div>
          <button type="submit" disabled={loading} className="reset-button">
            {loading ? "Resetting..." : "Reset Password"}
          </button>
          {message && <div className="message success-message">{message}</div>}
          {error && <div className="message error-message">{error}</div>}
        </form>
      </div>
    </div>
  );
};

export default ResetPassword;
