import React from "react";
import { Link } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "./CssSheets/SignUp.css";
import { useFormValidation } from "../hooks/useFormValidation";
import { useSignUp } from "../hooks/useSignUp";

const SignUp = () => {
  const initialState = {
    firstName: "",
    lastName: "",
    email: "",
    username: "",
    gender: "",
    password: "",
    confirmPassword: "",
    dateOfBirth: "",
    institution: "",
  };

  const { formData, errors, setErrors, handleChange, validateForm } = useFormValidation(initialState);
  const { isSubmitting, submitSignUp } = useSignUp();

  const handleSubmit = async (e) => {
    e.preventDefault();
    const formErrors = validateForm();
    if (Object.keys(formErrors).length > 0) {
      setErrors(formErrors);
      toast.error("Please correct the errors in the form");
      return;
    }
    await submitSignUp(formData, setErrors);
  };

  return (
    <div className="signup-container">
      <div className="signup-form-wrapper">
        <h1>Nexus</h1>
        <h2>Create an Account on Nexus</h2>
        <form onSubmit={handleSubmit} className="signup-form">
          <div className="form-group">
            <label htmlFor="firstName">First Name</label>
            <input
              type="text"
              id="firstName"
              name="firstName"
              value={formData.firstName}
              onChange={handleChange}
              className={errors.firstName ? "error" : ""}
              disabled={isSubmitting}
              placeholder="Enter your first name"
            />
            {errors.firstName && (
              <span className="error-message">{errors.firstName}</span>
            )}
          </div>
          <div className="form-group">
            <label htmlFor="lastName">Last Name</label>
            <input
              type="text"
              id="lastName"
              name="lastName"
              value={formData.lastName}
              onChange={handleChange}
              className={errors.lastName ? "error" : ""}
              disabled={isSubmitting}
              placeholder="Enter your last name"
            />
            {errors.lastName && (
              <span className="error-message">{errors.lastName}</span>
            )}
          </div>
          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              type="email"
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
            <label htmlFor="username">Username</label>
            <input
              type="text"
              id="username"
              name="username"
              value={formData.username}
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
            <label htmlFor="gender">Gender</label>
            <select
              id="gender"
              name="gender"
              value={formData.gender}
              onChange={handleChange}
              className={errors.gender ? "error" : ""}
              disabled={isSubmitting}
            >
              <option value="">Select gender</option>
              <option value="male">Male</option>
              <option value="female">Female</option>
            </select>
            {errors.gender && (
              <span className="error-message">{errors.gender}</span>
            )}
          </div>
          <div className="form-group">
            <label htmlFor="dateOfBirth">Date of Birth</label>
            <input
              type="date"
              id="dateOfBirth" 
              name="dateOfBirth" 
              value={formData.dateOfBirth}
              onChange={handleChange}
              className={errors.dateOfBirth ? "error" : ""}
              disabled={isSubmitting}
              placeholder="Enter your date of birth"
            />
            {errors.dateOfBirth && (
              <span className="error-message">{errors.dateOfBirth}</span>
            )}
          </div>
          <div className="form-group">
            <label htmlFor="institution">Institution</label>
            <input
              type="text"
              id="institution"
              name="institution"
              value={formData.institution}
              onChange={handleChange}
              className={errors.institution ? "error" : ""}
              disabled={isSubmitting}
              placeholder="Enter your institution"
            />
            {errors.institution && (
              <span className="error-message">{errors.institution}</span>
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
              placeholder="Enter your 8 digit password"
            />
            {errors.password && (
              <span className="error-message">{errors.password}</span>
            )}
          </div>
          <div className="form-group">
            <label htmlFor="confirmPassword">Confirm Password</label>
            <input
              type="password"
              id="confirmPassword"
              name="confirmPassword"
              value={formData.confirmPassword}
              onChange={handleChange}
              className={errors.confirmPassword ? "error" : ""}
              disabled={isSubmitting}
              placeholder="Confirm your password"
            />
            {errors.confirmPassword && (
              <span className="error-message">{errors.confirmPassword}</span>
            )}
          </div>
          <button
            type="submit"
            className={`submit-button ${isSubmitting ? 'submitting' : ''}`}
            disabled={isSubmitting}
          >
            {isSubmitting ? "Creating Account..." : "Sign Up"}
          </button>
          <div className="login-link">
            Already have an account? <Link to="/Signin">Log In</Link>
          </div>
        </form>
      </div>
      <ToastContainer position="top-right" autoClose={5000} />
    </div>
  );
};

export default SignUp;
