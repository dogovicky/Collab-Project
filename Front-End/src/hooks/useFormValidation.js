import { useState, useEffect } from 'react';

export const useFormValidation = (initialState) => {
  const [formData, setFormData] = useState(initialState);
  const [errors, setErrors] = useState({});

  const formatFieldName = (field) => {
    return field
      .replace(/([A-Z])/g, ' $1')
      .replace(/^./, str => str.toUpperCase())
      .replace(/ Of /g, ' of ');
  };

  const validateEmail = (email) => {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(String(email).toLowerCase());
  };

  const validatePassword = (password) => {
    const re = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
    return re.test(password);
  };

  const validateForm = (fieldsToValidate = Object.keys(formData)) => {
    const newErrors = {};
    
    fieldsToValidate.forEach((field) => {
      const value = formData[field];
      const fieldName = formatFieldName(field);

      // Required field validation
      if (!value?.toString().trim()) {
        newErrors[field] = `${fieldName} is required`;
        return;
      }

      // Field-specific validations
      switch(field) {
        case 'email':
          if (!validateEmail(value)) {
            newErrors.email = "Please enter a valid email address";
          }
          break;
          
        case 'password':
          if (!validatePassword(value)) {
            newErrors.password = "Password must be at least 8 characters and include uppercase, lowercase, number and special character";
          }
          break;

        case 'firstName':
        case 'lastName':
          if (!/^[a-zA-Z]+$/.test(value)) {
            newErrors[field] = `${fieldName} should only contain letters`;
          }
          break;

        case 'profilePicture':
          if (!(value instanceof File)) {
            newErrors.profilePicture = "Please upload a valid profile picture (e.g., .jpg, .png).";
          }
          break;

        case 'fieldsOfInterest':
          if (!Array.isArray(value) || value.length === 0) {
            newErrors.fieldsOfInterest = "Please select at least one field of interest.";
          }
          break;

        default:
          break;
      }
    });

    setErrors(newErrors); // Ensure errors are updated in state
    return newErrors;
  };

  const handleChange = (e) => {
    const { name, value, type, files } = e.target;
    const processedValue = type === 'file' ? files[0] : value;

    setFormData((prev) => ({ ...prev, [name]: processedValue }));

    // Dynamically clear errors for the field being updated
    if (errors[name]) {
      setErrors((prevErrors) => {
        const { [name]: removedError, ...rest } = prevErrors;
        return rest;
      });
    }
  };

  // Add useEffect to revalidate when formData changes
  useEffect(() => {
    const fieldsWithErrors = Object.keys(errors);
    if (fieldsWithErrors.length > 0) {
      const updatedErrors = validateForm(fieldsWithErrors);
      setErrors(updatedErrors);
    }
  }, [formData]);

  return { 
    formData, 
    errors, 
    setErrors, 
    handleChange, 
    validateForm,
    setFormData // Optional: Add if you need direct access
  };
};
