import { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import debounce from 'lodash/debounce';
import { validateFileSize, validateFileType } from '../utils/fileValidation';

export const useFormValidation = (initialState) => {
  const [formData, setFormData] = useState(initialState);
  const [errors, setErrors] = useState({});
  const [isCheckingEmail, setIsCheckingEmail] = useState(false);

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
    const requirements = {
      minLength: password.length >= 8,
      hasUpperCase: /[A-Z]/.test(password),
      hasLowerCase: /[a-z]/.test(password),
      hasNumber: /\d/.test(password),
      hasSpecial: /[!@#$%^&*(),.?":{}|<>]/.test(password)
    };

    const isValid = Object.values(requirements).every(Boolean);

    return {
      isValid,
      requirements,
      message: !isValid
        ? `Password must contain at least:
          - 8 characters
          - One uppercase letter
          - One lowercase letter
          - One number
          - One special character`
        : ''
    };
  };

  const checkEmailExists = async (email) => {
    try {
      const response = await axios.post('/api/check-email', { email });
      return response.data.exists;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Error checking email');
    }
  };

  const debouncedEmailCheck = useCallback(
    debounce(async (email) => {
      if (!email || !validateEmail(email)) return;
      
      setIsCheckingEmail(true);
      try {
        const exists = await checkEmailExists(email);
        if (exists) {
          setErrors(prev => ({
            ...prev,
            email: "This email is already registered"
          }));
        }
      } catch (error) {
        console.error('Email check failed:', error);
      } finally {
        setIsCheckingEmail(false);
      }
    }, 500),
    []
  );

  const validateForm = useCallback(async (fieldsToValidate = Object.keys(formData)) => {
    const newErrors = {};
    
    for (const field of fieldsToValidate) {
      const value = formData[field];
      const fieldName = formatFieldName(field);

      // Skip validation for optional fields if they're empty
      if (!value && !['email', 'password'].includes(field)) {
        continue;
      }

      // Required field validation
      if (['email', 'password'].includes(field) && !value?.toString().trim()) {
        newErrors[field] = `${fieldName} is required`;
        continue;
      }

      switch(field) {
        case 'email':
          if (!validateEmail(value)) {
            newErrors.email = `Please enter a valid email address`;
          }
          break;
          
        case 'password':
          const passwordValidation = validatePassword(value);
          if (!passwordValidation.isValid) {
            newErrors.password = passwordValidation.message;
          }
          break;

        case 'firstName':
        case 'lastName':
          if (!/^[a-zA-Z]+$/.test(value)) {
            newErrors[field] = `${fieldName} should only contain letters`;
          }
          break;

        case 'profilePicture':
          if (value instanceof File) {
            const sizeError = validateFileSize(value);
            if (sizeError) {
              newErrors.profilePicture = sizeError;
              break;
            }
            
            const typeError = validateFileType(value);
            if (typeError) {
              newErrors.profilePicture = typeError;
              break;
            }
          } else if (value !== null) {
            newErrors.profilePicture = `Please upload a valid image file`;
          }
          break;

        case 'fieldsOfInterest':
          if (!Array.isArray(value) || value.length === 0) {
            newErrors.fieldsOfInterest = `Please select at least one field of interest.`;
          }
          break;

        default:
          break;
      }
    }

    setErrors(newErrors);
    return newErrors;
  }, [formData]);

  const handleChange = useCallback((e) => {
    const { name, value, type, files } = e.target;
    const processedValue = type === 'file' ? files[0] : value;

    setFormData((prev) => ({ ...prev, [name]: processedValue }));

    // Clear error for the changed field
    setErrors(prev => {
      const { [name]: removed, ...rest } = prev;
      return rest;
    });

    // Trigger email check only when email field changes and has a value
    if (name === 'email' && value) {
      debouncedEmailCheck(value);
    }
  }, [debouncedEmailCheck]);

  // Add useEffect to revalidate when formData changes
  useEffect(() => {
    const fieldsWithErrors = Object.keys(errors);
    if (fieldsWithErrors.length > 0) {
      validateForm(fieldsWithErrors);
    }
  }, [formData, validateForm, errors]);

  return { 
    formData, 
    errors, 
    setErrors, 
    handleChange, 
    validateForm,
    isCheckingEmail,
    setFormData 
  };
};
