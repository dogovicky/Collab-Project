import { useState } from "react";
import { useFormValidation } from "../hooks/useFormValidation";
import { signUp } from "../api/auth";
import "./CssSheets/SignUp.css";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const SignUp = () => {
  const API_URL = "http://localhost:8080/auth/signup";
  const navigate = useNavigate();
  const [isSubmitting, setIsSubmitting] = useState(false);

  const {
    formData: signUpRequest,
    errors,
    handleChange: handleInputChange,
    validateForm,
  } = useFormValidation({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    username: "",
    dateOfBirth: "",
    gender: "",
    institution: "",
    fieldOfInterest: "",
  });

  const handleSubmit = async (e) => {
    e.preventDefault();

    const formErrors = await validateForm();
    if (Object.keys(formErrors).length > 0) {
      const firstErrorField = document.querySelector(".error-message");
      firstErrorField?.scrollIntoView({ behavior: "smooth", block: "center" });
      return;
    }

    setIsSubmitting(true);

    const interestArray = signUpRequest.fieldOfInterest
      .split(",")
      .map((interest) => interest.trim())
      .filter((interest) => interest.length > 0);

    const payload = {
      ...signUpRequest,
      fieldOfInterest: interestArray,
    };

    try {
      const response = await signUp(payload);
      if (response.data.status === 200) {
        localStorage.setItem("username", response.data.data);
        console.log(response.data);
      }
      navigate("/emailValidation");
    } catch (error) {
      console.error("Sign up error: ", error);
      setErrors((prev) => ({
        ...prev,
        submit:
          error.response?.data?.message ||
          "Failed to sign up. Please try again.",
      }));
    } finally {
      setIsSubmitting(false);
    }
  };

  const renderError = (fieldName) => {
    return (
      errors[fieldName] && (
        <div className="error-message" role="alert">
          {errors[fieldName]}
        </div>
      )
    );
  };

  return (
    <div className="signup-container">
      <div className="form-wrapper">
        <div className="header">
          <h2>
            Welcome to Nexus, Please fill in your correct details to sign up.
          </h2>
        </div>
        <div className="form-container">
          <form action="" className="row g-3" onSubmit={handleSubmit}>
            {errors.submit && (
              <div className="alert alert-danger" role="alert">
                {errors.submit}
              </div>
            )}
            <div className="row g-3 input-container">
              <div className="col">
                <input
                  type="text"
                  className={`form-control ${
                    errors.firstName ? "is-invalid" : ""
                  }`}
                  placeholder="First name"
                  aria-label="First name"
                  name="firstName"
                  value={signUpRequest.firstName}
                  onChange={handleInputChange}
                />
                {renderError("firstName")}
              </div>
              <div className="col">
                <input
                  type="text"
                  className={`form-control ${
                    errors.lastName ? "is-invalid" : ""
                  }`}
                  placeholder="Last name"
                  aria-label="Last name"
                  name="lastName"
                  value={signUpRequest.lastName}
                  onChange={handleInputChange}
                />
                {renderError("lastName")}
              </div>
            </div>
            <div className="input-container">
              <div className="col-md-6">
                <input
                  type="email"
                  className={`form-control ${errors.email ? "is-invalid" : ""}`}
                  id="inputEmail4"
                  placeholder="Email e.g name@example.com"
                  name="email"
                  value={signUpRequest.email}
                  onChange={handleInputChange}
                />
                {renderError("email")}
              </div>
              <div className="col-md-6">
                <input
                  type="password"
                  className={`form-control ${
                    errors.password ? "is-invalid" : ""
                  }`}
                  id="inputPassword4"
                  placeholder="Strong password"
                  name="password"
                  value={signUpRequest.password}
                  onChange={handleInputChange}
                />
                {renderError("password")}
              </div>
            </div>
            <div className="col-12">
              <input
                type="text"
                className={`form-control ${
                  errors.username ? "is-invalid" : ""
                }`}
                id="inputAddress"
                placeholder="@username"
                name="username"
                value={signUpRequest.username}
                onChange={handleInputChange}
              />
              {renderError("username")}
            </div>
            <div className="details">
              <div className="col-md-6">
                <label for="inputCity" className="form-label">
                  Date of Birth
                </label>
                <input
                  type="date"
                  className={`form-control ${
                    errors.dateOfBirth ? "is-invalid" : ""
                  }`}
                  id="inputCity"
                  name="dateOfBirth"
                  value={signUpRequest.dateOfBirth}
                  onChange={handleInputChange}
                />
                {renderError("dateOfBirth")}
              </div>
              <div className="col-md-4">
                <label for="inputState" className="form-label">
                  Gender
                </label>
                <select
                  id="inputState"
                  className={`form-select ${errors.gender ? "is-invalid" : ""}`}
                  name="gender"
                  value={signUpRequest.gender}
                  onChange={handleInputChange}
                >
                  <option selected>--Choose Gender--</option>
                  <option>Male</option>
                  <option>Female</option>
                </select>
                {renderError("gender")}
              </div>
              <div className="col-md-2">
                <label for="inputZip" className="form-label">
                  Institution
                </label>
                <input
                  type="text"
                  className={`form-control ${
                    errors.institution ? "is-invalid" : ""
                  }`}
                  id="inputZip"
                  name="institution"
                  value={signUpRequest.institution}
                  onChange={handleInputChange}
                />
                {renderError("institution")}
              </div>
            </div>
            <div className="col-12">
              <label for="inputAddress2" className="form-label">
                Fields of Interest
              </label>
              <input
                type="text"
                className={`form-control ${
                  errors.fieldOfInterest ? "is-invalid" : ""
                }`}
                id="inputAddress2"
                placeholder="Example; Cyber Security, Medicine, Aviation etc.."
                name="fieldOfInterest"
                value={signUpRequest.fieldOfInterest}
                onChange={handleInputChange}
              />
              {renderError("fieldOfInterest")}
            </div>
            <div className="col-12">
              <button
                type="submit"
                className="btn btn-primary"
                disabled={isSubmitting}
              >
                {isSubmitting ? "Signing Up..." : "Sign Up"}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default SignUp;
