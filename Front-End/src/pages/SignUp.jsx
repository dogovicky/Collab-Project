import { useState, useEffect } from "react";
import Step1 from "./CssSheets/Step1";
import Step2 from "./CssSheets/Step2";
import Step3 from "./CssSheets/Step3";
import Step4 from "./CssSheets/Step4";
import { useSignUp } from "../hooks/useSignUp";
import { useFormValidation } from "../hooks/useFormValidation";
import { signUp } from "../api/auth";
import "./CssSheets/SignUp.css";
import axios from "axios";

const SignUp = () => {
  const [step, setStep] = useState(1);
  const [formData, setFormData] = useState({
    email: "",
    password: "",
    firstName: "",
    lastName: "",
    profilePicture: null,
    bio: "",
    institution: "",
    fieldsOfInterest: [],
  });
  const [errors, setErrors] = useState({});
  const { isSubmitting, submitSignUp } = useSignUp();
  const { validateForm } = useFormValidation(formData);
  const [apiError, setApiError] = useState(null);
  const [apiSuccess, setApiSuccess] = useState(null);

  const handleChange = (e) => {
    const { name, value, files } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: files ? files[0] : value,
    }));

    // Dynamically clear errors for the field being updated
    if (errors[name]) {
      setErrors((prevErrors) => {
        const { [name]: removedError, ...rest } = prevErrors;
        return rest;
      });
    }
  };

  const nextStep = async (e) => {
    e.preventDefault();
    setApiError(null);
    let currentStepValid = true;
    let newErrors = {};

    if (step === 1) {
      try {
        // Call API to check if email exists
        const response = await axios.post('/api/check-email', { email: formData.email });
        if (response.data.exists) {
          newErrors.email = 'Email already exists';
          currentStepValid = false;
        }
      } catch (error) {
        setApiError(error.response?.data?.message || 'An error occurred');
        currentStepValid = false;
      }
    }

    // Validate current step
    if (step === 1) {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
      
      if (!emailRegex.test(formData.email)) {
        newErrors.email = 'Please enter a valid email';
        currentStepValid = false;
      }
      if (!passwordRegex.test(formData.password)) {
        newErrors.password = 'Please enter a valid password';
        currentStepValid = false;
      }
    }

    if (!currentStepValid) {
      setErrors(newErrors);
      return;
    }

    // If validation passes, clear errors and move to next step
    setErrors({});
    setStep(prevStep => prevStep + 1);
  };

  const prevStep = () => setStep((prev) => prev - 1);

  const handleSubmit = async (e) => {
    e.preventDefault(); // Prevent default form behavior
    setApiError(null);
    if (step === 4) {
      const validationErrors = validateForm();
      if (Object.keys(validationErrors).length > 0) {
        setErrors(validationErrors);
        return;
      }
      try {
        const response = await signUp(formData); // Use the signUp API here
        setApiSuccess(response.data.message || 'Signup successful! Please check your email for verification.');
      } catch (error) {
        setApiError(error.response?.data?.message || 'Signup failed. Please try again.');
      }
    }
  };

  return (
    <div className="sign-up-page">
      <div className="sign-up-container">
        {apiError && <div className="api-error">{apiError}</div>}
        {apiSuccess && <div className="api-success">{apiSuccess}</div>}
        <h2>Step {step} of 4</h2>
        <form onSubmit={handleSubmit}>
          {step === 1 && (
            <Step1
              formData={formData}
              handleChange={handleChange}
              nextStep={nextStep}
              errors={errors}
            />
          )}
          {step === 2 && (
            <Step2
              formData={formData}
              handleChange={handleChange}
              nextStep={nextStep}
              prevStep={prevStep}
              errors={errors}
            />
          )}
          {step === 3 && (
            <Step3
              formData={formData}
              handleChange={handleChange}
              nextStep={nextStep}
              prevStep={prevStep}
              errors={errors}
            />
          )}
          {step === 4 && (
            <Step4
              formData={formData}
              handleSubmit={handleSubmit}
              prevStep={prevStep}
              isSubmitting={isSubmitting}
              errors={errors}
            />
          )}
        </form>
      </div>
    </div>
  );
};

export default SignUp;


