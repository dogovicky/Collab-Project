import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { toast } from "react-toastify";
import { useAuth } from "../../context/AuthContext";
import { loginUser } from "../../api/authA";
import "./CssSheets/SignIn.css";
import Header from "../../components/header";

const SignIn = () => {
  const navigate = useNavigate(); // Hook to programmatically navigate between routes
  const { login } = useAuth(); // Context function to handle login state
  const [formData, setFormData] = useState({
    username: "",
    password: "",
  });

  const [errors, setErrors] = useState({}); // Tracks validation errors for the form
  const [isSubmitting, setIsSubmitting] = useState(false); // Tracks the submission state of the form

  // Function to validate the form inputs
  const validateForm = () => {
    const newErrors = {};
    if (!formData.username.trim()) newErrors.username = "Username is required"; // Ensure username is not empty
    if (!formData.password.trim()) newErrors.password = "Password is required"; // Ensure password is not empty
    return newErrors;
  };

  // Handles changes in form inputs and updates the state
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value }); // Update the corresponding field in formData

    if (errors[name]) {
      setErrors({ ...errors, [name]: "" }); // Clear the error for the field being updated
    }
  };

  // Handles form submission
  const handleSubmit = async (e) => {
    e.preventDefault(); // stops page from reloading
    const formErrors = validateForm(); // Validate the form inputs

    if (Object.keys(formErrors).length > 0) {
      setErrors(formErrors); // Set validation errors if any
      toast.error("Please fill in all the required fields");
      return;
    }

    setIsSubmitting(true);

    try {
      const response = await loginUser(formData); // Call the API to log in the user

      if (!response.token) {
        throw new Error("Authentication failed. Invalid credentials."); // Handle invalid credentials
      }

      login(response.token); // Save the token using the login function from context
      toast.success("Logged in successfully!");
      navigate("/home"); // Redirect to the home page
    } catch (error) {
      if (error.response?.data?.errors) {
        setErrors(error.response.data.errors); // Display specific errors from the API
      }
      toast.error(
        error.response?.data?.message ||
          error.message ||
          "Login failed. Please try again."
      ); // Show error notification
    } finally {
      setIsSubmitting(false); // Reset the submission state
    }
  };

  // Redirects the user to the forgot password page
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
              {/* Username input field */}
              <div className="form-group">
                <label htmlFor="username">Username</label>
                <input
                  type="text"
                  id="username"
                  name="username"
                  value={formData.username} // Bind input value to state
                  onChange={handleChange} // Update state on input change
                  className={errors.username ? "error" : ""} // Add error class if validation fails
                  disabled={isSubmitting} // Disable input while submitting
                  placeholder="Enter your username"
                />
                {errors.username && (
                  <span className="error-message">{errors.username}</span> // Display validation error
                )}
              </div>
              {/* Password input field */}
              <div className="form-group">
                <label htmlFor="password">Password</label>
                <input
                  type="password"
                  id="password"
                  name="password"
                  value={formData.password} // Bind input value to state
                  onChange={handleChange} // Update state on input change
                  className={errors.password ? "error" : ""} // Add error class if validation fails
                  disabled={isSubmitting}
                  placeholder="Enter 8 digit password"
                />
                {errors.password && (
                  <span className="error-message">{errors.password}</span> // Display validation error
                )}
              </div>
              {/* Submit button */}
              <button
                type="submit"
                className="login-button"
                disabled={isSubmitting}
              >
                {isSubmitting ? "Logging in..." : "Log In"}
              </button>
              {/* Forgot password link */}
              <div className="forgot-password">
                <Link to="/forgot-password" className="forgot-password-link">
                  Forgot Password?
                </Link>
              </div>
              {/* Sign-up link */}
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
