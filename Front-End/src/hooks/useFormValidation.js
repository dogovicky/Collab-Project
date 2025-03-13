import { useState } from 'react';

export const useFormValidation = (initialState) => {
  const [formData, setFormData] = useState(initialState);
  const [errors, setErrors] = useState({});

  const validateEmail = (email) => {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(String(email).toLowerCase());
  };

  const validatePassword = (password) => {
    const re = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
    return re.test(password);
  };

  const validateForm = () => {
    const newErrors = {};
    
    Object.entries(formData).forEach(([key, value]) => {
      if (!value?.trim()) {
        newErrors[key] = `${key.charAt(0).toUpperCase() + key.slice(1)} is required`;
      }
    });

    if (formData.email && !validateEmail(formData.email)) {
      newErrors.email = "Please enter a valid email address";
    }

    if (formData.password && !validatePassword(formData.password)) {
      newErrors.password = "Password must be at least 8 characters and include uppercase, lowercase, number and special character";
    }

    if (formData.password && formData.confirmPassword && formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = "Passwords do not match";
    }

    return newErrors;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: "" }));
    }
  };

  return { formData, errors, setErrors, handleChange, validateForm };
};
