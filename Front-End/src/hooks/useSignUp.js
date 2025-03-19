import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { toast } from "react-toastify";

export const useSignUp = () => {
  const navigate = useNavigate();
  const [isSubmitting, setIsSubmitting] = useState(false);

  const submitSignUp = async (formData, setErrors) => {
    setIsSubmitting(true);
    try {
      const formDataToSend = new FormData();

      Object.entries(formData).forEach(([key, value]) => {
        if (Array.isArray(value)) {
          value.forEach((v) => formDataToSend.append(key, v));
        } else {
          formDataToSend.append(key, value);
        }
      });

      const response = await axios.post("/api/signup", formDataToSend);
      toast.success("Signup successful!");
      navigate("/dashboard");
    } catch (error) {
      console.error(error);
      setErrors(error.response?.data?.errors || {});
      toast.error("Signup failed");
    } finally {
      setIsSubmitting(false);
    }
  };

  return { isSubmitting, submitSignUp };
};
