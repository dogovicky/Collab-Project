import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { toast } from "react-toastify";
import { useAuth } from '../context/AuthContext';
import { loginUser } from "../api/authA"; // Corrected import path
import "./CssSheets/SignIn.css";
import Header from "../components/header";

const SignIn = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [formData, setFormData] = useState({
    username: "", // Changed from email to username
    password: "",
  });

  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  const validateForm = () => {
    const newErrors = {};
    if (!formData.username.trim()) newErrors.username = "Username is required"; // Updated validation
    if (!formData.password.trim()) newErrors.password = "Password is required";
    return newErrors;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });

    if (errors[name]) {
      setErrors({ ...errors, [name]: "" });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const formErrors = validateForm();

    if (Object.keys(formErrors).length > 0) {
      setErrors(formErrors);
      toast.error("Please fill in all the required fields");
      return;
    }

    setIsSubmitting(true);

    try {
      const response = await loginUser(formData); // Call API

      if (!response.token) {
        throw new Error("Authentication failed. Invalid credentials.");
      }

      login(response.token); // Use the login function from auth context
      toast.success("Logged in successfully!");
      navigate("/home");
    } catch (error) {
      if (error.response?.data?.errors) {
        setErrors(error.response.data.errors); // Set form-specific errors from API
      }
      toast.error(error.response?.data?.message || error.message || "Login failed. Please try again.");
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleForgotPassword = () => {
    navigate("/forgot-password");
  };

  return (
    <>
    <header />
    <div className="sign-in">
      <div className="login-container">
        <div className="login-form-wrapper">
          <h1>Nexus</h1> 
          <h2>Log Into Nexus</h2> 
          <form onSubmit={handleSubmit} className="login-form">
            <div className="form-group">
              <label htmlFor="username">Username</label>
              <input
                type="text"
                id="username"
                name="username"
                value={formData.username} // value binding
                onChange={handleChange}
                className={errors.username ? "error" : ""}
                disabled={isSubmitting}
                placeholder="Enter your username"
              />
              {errors.username && (
                <span className="error-message">{errors.username}</span>
              )}
            </div>
            <div className="form-group">
              <label htmlFor="password">Password</label>
              <input
                type="password"
                id="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                className={errors.password ? "error" : ""}
                disabled={isSubmitting}
                placeholder="Enter 8 digit password"
              />
              {errors.password && (
                <span className="error-message">{errors.password}</span>
              )}
            </div>
            <button
              type="submit"
              className="login-button"
              disabled={isSubmitting}
            >
              {isSubmitting ? "Logging in..." : "Log In"}
            </button>
            <div className="forgot-password">
              <Link to="/forgot-password" className="forgot-password-link">
                Forgot Password?
              </Link>
            </div>
            <div className="signup-link">
              Don't have an account? <Link to="/">Sign Up</Link>
            </div>
          </form>
        </div>
      </div>
    </div>
    </>
  );
};

export default SignIn;
