import { useState, useEffect } from "react";
import Step1 from "./CssSheets/Step1";
import Step2 from "./CssSheets/Step2";
import Step3 from "./CssSheets/Step3";
import Step4 from "./CssSheets/Step4";
import { useSignUp } from "../hooks/useSignUp";
import { useFormValidation } from "../hooks/useFormValidation";
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

  const nextStep = (e) => {
    e.preventDefault(); // Prevent default form behavior
    const fieldsToValidate = Object.keys(formData).filter((key) => {
      if (step === 1) return ["email", "password"].includes(key);
      if (step === 2) return ["firstName", "lastName"].includes(key);
      if (step === 3) return ["profilePicture", "bio", "institution", "fieldsOfInterest"].includes(key);
      return false;
    });

    const validationErrors = validateForm(fieldsToValidate);

    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      alert("Please fix the errors before proceeding to the next step."); // Feedback for validation failure
      return;
    }

    setErrors({});
    setStep((prev) => prev + 1); // Move to the next step
  };

  const prevStep = () => setStep((prev) => prev - 1);

  const handleSubmit = async (e) => {
    e.preventDefault(); // Prevent default form behavior
    if (step === 4) {
      const validationErrors = validateForm();
      if (Object.keys(validationErrors).length > 0) {
        setErrors(validationErrors);
        return;
      }
      const signupSuccess = await submitSignUp(formData, setErrors);
      if (signupSuccess) {
        try {
          // Trigger email verification API
          await axios.post('/api/send-verification', { email: formData.email });
          alert('Signup successful! A verification code has been sent to your email.');
        } catch (err) {
          alert('Signup successful, but failed to send verification email.');
        }
      }
    }
  };

  return (
    <div className="sign-up-page">
      <div className="sign-up-container">
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


