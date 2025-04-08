import { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import debounce from 'lodash/debounce';

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

  // const checkEmailExists = async (email) => {
  //   try {
  //     const response = await axios.post('/api/check-email', { email });
  //     return response.data.exists;
  //   } catch (error) {
  //     throw new Error(error.response?.data?.message || 'Error checking email');
  //   }
  // };

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

  const validateForm = useCallback(() => {
    const newErrors = {};
    
    // Check required fields
    const requiredFields = ['email', 'password', 'firstName', 'lastName', 'username', 'dateOfBirth', 'gender', 'institution', 'fieldOfInterest'];
    requiredFields.forEach(field => {
      if (!formData[field] || formData[field].trim() === '') {
        newErrors[field] = `${formatFieldName(field)} is required`;
      }
    });

    // Validate email
    if (formData.email && !validateEmail(formData.email)) {
      newErrors.email = 'Invalid email format';
    }

    // Validate password
    if (formData.password) {
      const passwordValidation = validatePassword(formData.password);
      if (!passwordValidation.isValid) {
        newErrors.password = passwordValidation.message;
      }
    }

    // Username validation
    if (formData.username && formData.username.length < 3) {
      newErrors.username = 'Username must be at least 3 characters long';
    }

    // Date of birth validation
    if (formData.dateOfBirth) {
      const date = new Date(formData.dateOfBirth);
      const now = new Date();
      const age = now.getFullYear() - date.getFullYear();
      if (age < 13) {
        newErrors.dateOfBirth = 'You must be at least 13 years old';
      }
    }

    // Gender validation
    if (formData.gender === '--Choose Gender--') {
      newErrors.gender = 'Please select a gender';
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
      const currentErrors = validateForm();
      if (JSON.stringify(currentErrors) !== JSON.stringify(errors)) {
        setErrors(currentErrors);
      }
    }
  }, [formData]); // Only depend on formData changes

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
