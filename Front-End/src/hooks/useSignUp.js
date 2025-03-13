import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { toast } from 'react-toastify';

export const useSignUp = () => {
  const navigate = useNavigate();
  const [isSubmitting, setIsSubmitting] = useState(false);

  const submitSignUp = async (formData, setErrors) => {
    setIsSubmitting(true);
    try {
      const response = await axios.post("https://api.example.com/signup", formData, {
        headers: { "Content-Type": "application/json" },
      });

      if (response.status === 201) {
        toast.success("Account created successfully!");
        if (response.data.token) {
          localStorage.setItem("authToken", response.data.token);
        }
        navigate("/emailValidation");
      }
    } catch (error) {
      console.error("Signup error:", error);
      handleSignUpError(error, setErrors);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleSignUpError = (error, setErrors) => {
    if (error.response) {
      const { status, data } = error.response;
      if (status === 409) {
        toast.error(data.message || "Email or username already in use");
      } else if (status === 400) {
        toast.error(data.message || "Invalid form data");
        if (data.errors) setErrors(data.errors);
      } else {
        toast.error("Server error. Please try again later.");
      }
    } else if (error.request) {
      toast.error("No response from server. Please check your internet connection.");
    } else {
      toast.error("An unexpected error occurred.");
    }
  };

  return { isSubmitting, submitSignUp };
};
