import { useState } from 'react';

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
          
        case 'confirmPassword':
          if (value !== formData.password) {
            newErrors.confirmPassword = "Passwords do not match";
          }
          break;
          
        case 'dateOfBirth': {
          const dob = new Date(value);
          const age = new Date().getFullYear() - dob.getFullYear();
          if (age < 13) {
            newErrors.dateOfBirth = "You must be at least 13 years old";
          }
          break;
        }
      }
    });

    return newErrors;
  };

  const handleChange = (e) => {
    const { name, value, type } = e.target;
    const processedValue = type === 'date' ? new Date(value).toISOString().split('T')[0] : value;
    
    setFormData(prev => ({ ...prev, [name]: processedValue }));
    
    // Clear error if user starts correcting
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: "" }));
    }
  };

  return { 
    formData, 
    errors, 
    setErrors, 
    handleChange, 
    validateForm,
    setFormData // Optional: Add if you need direct access
  };
};
