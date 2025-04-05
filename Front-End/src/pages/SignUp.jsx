import { useState } from "react";
import Step1 from "./CssSheets/Step1";
import Step2 from "./CssSheets/Step2";
import Step3 from "./CssSheets/Step3";
import Step4 from "./CssSheets/Step4";
import { useSignUp } from "../hooks/useSignUp";
import { useFormValidation } from "../hooks/useFormValidation";
import { signUp } from "../api/auth";
import "./CssSheets/SignUp.css";

const SignUp = () => {
  const [step, setStep] = useState(1);
  const [apiError, setApiError] = useState(null);
  const [apiSuccess, setApiSuccess] = useState(null);
  const { isSubmitting, submitSignUp } = useSignUp();
  
  const initialFormData = {
    email: "",
    password: "",
    firstName: "",
    lastName: "",
    profilePicture: null,
    bio: "",
    institution: "",
    fieldsOfInterest: [],
  };

  const { 
    formData, 
    errors, 
    setErrors, 
    handleChange, 
    validateForm,
    isCheckingEmail 
  } = useFormValidation(initialFormData);

  const nextStep = async (e) => {
    e.preventDefault();
    setApiError(null);

    if (isCheckingEmail) {
      setApiError('Please wait while we verify your email');
      return;
    }

    let fieldsToValidate = [];
    switch (step) {
      case 1:
        fieldsToValidate = ['email', 'password'];
        break;
      case 2:
        fieldsToValidate = ['firstName', 'lastName'];
        break;
      case 3:
        fieldsToValidate = ['profilePicture', 'bio', 'institution', 'fieldsOfInterest'];
        break;
      default:
        fieldsToValidate = Object.keys(formData);
    }

    try {
      const validationErrors = await validateForm(fieldsToValidate);
      if (Object.keys(validationErrors).length === 0) {
        setStep(prevStep => prevStep + 1);
      }
    } catch (error) {
      setApiError(error.message || 'An error occurred');
    }
  };

  const prevStep = () => setStep((prev) => prev - 1);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setApiError(null);
    
    if (step === 4) {
      try {
        const validationErrors = await validateForm();
        if (Object.keys(validationErrors).length === 0) {
          const response = await signUp(formData);
          setApiSuccess(response.data.message || 'Signup successful! Please check your email for verification.');
        }
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
              isCheckingEmail={isCheckingEmail}
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


