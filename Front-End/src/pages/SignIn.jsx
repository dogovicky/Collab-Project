import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { toast } from "react-toastify";
import { useAuth } from '../context/AuthContext';
import "./CssSheets/SignIn.css";

const SignIn = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });

  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  const validateForm = () => {
    const newErrors = {};
    if (!formData.email.trim()) newErrors.email = "Email is required";
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

    setTimeout(() => {
      const mockToken = "mock-auth-token-123";
      login(mockToken); // Use the login function from auth context
      toast.success("Logged in successfully!");
      setIsSubmitting(false);
      navigate("/home");
    }, 1000);
  };

  const handleForgotPassword = () => {
    navigate("/forgot-password");
  };

  return (
    <div className="login-container">
      <div className="login-form-wrapper">
        <h1>Nexus</h1> 
        <h2>Log Into Nexus</h2> 
        <form onSubmit={handleSubmit} className="login-form">
          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              type="text"
              id="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              className={errors.email ? "error" : ""}
              disabled={isSubmitting}
              placeholder="Enter your email"
            />
            {errors.email && (
              <span className="error-message">{errors.email}</span>
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
  );
};

export default SignIn;
