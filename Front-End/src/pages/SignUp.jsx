import { useState, useEffect } from "react";
import Step1 from "./CssSheets/Step1";
import Step2 from "./CssSheets/Step2";
import Step3 from "./CssSheets/Step3";
import Step4 from "./CssSheets/Step4";
import { useSignUp } from "../hooks/useSignUp";
import "./CssSheets/SignUp.css";

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

  const handleChange = (e) => {
    const { name, value, files } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: files ? files[0] : value,
    }));
  };

  const nextStep = () => setStep((prev) => prev + 1);
  const prevStep = () => setStep((prev) => prev - 1);

  const handleSubmit = async (e) => {
    e.preventDefault();
    await submitSignUp(formData, setErrors);
  };

  return (
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
          />
        )}
        {step === 3 && (
          <Step3
            formData={formData}
            handleChange={handleChange}
            nextStep={nextStep}
            prevStep={prevStep}
          />
        )}
        {step === 4 && (
          <Step4
            formData={formData}
            handleSubmit={handleSubmit}
            prevStep={prevStep}
            isSubmitting={isSubmitting}
          />
        )}
      </form>
      {Object.keys(errors).length > 0 && (
        <div className="error-message">
          {Object.values(errors).map((error, index) => (
            <p key={index}>{error}</p>
          ))}
        </div>
      )}
    </div>
  );
};

export default SignUp;


