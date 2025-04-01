import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { toast } from "react-toastify";
import { signUp } from "../api/auth";

export const useSignUp = () => {
  const navigate = useNavigate();
  const [isSubmitting, setIsSubmitting] = useState(false);

  const submitSignUp = async (formData, setErrors) => {
    setIsSubmitting(true);
    try {
      const response = await signUp(formData);
      toast.success("Signup successful!");
      navigate("/dashboard");
    } catch (error) {
      console.error(error);
      setErrors(error.response?.data?.errors || {});
      toast.error("Signup failed. Please check the highlighted fields.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return { isSubmitting, submitSignUp };
};
