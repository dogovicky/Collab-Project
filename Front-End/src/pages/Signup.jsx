import React, {useState, useEffect} from 'react';
import { useNavigate } from 'react-router-dom';
//import { ToastContainer, toast } from 'react-toastify';
//import 'react-toastify/dist/ReactToastify.css';

const SignupForm = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
      firstName: '',
      lastName: '',
      email: '',
      username: '',
      gender: '',
      password: '',
      confirmPassword: '',
      dateofbirth: '',
      institiution: '',
    });
    const [errors, setErrors] = useState({});
    const [isSubmitting, setIsSubmitting] = useState(false);

    const validateEmail = (email) => {
      const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      return re.test(String(email).toLowerCase());
    };
    const validatePassword = (password) => {
        // At least 8 characters, 1 uppercase, 1 lowercase, 1 number, 1 special character
        const re = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
        return re.test(password);
    };

    const validateForm = () => {
        const newErrors = {};
        
        // Required fields
        if (!formData.firstName.trim()) newErrors.firstName = 'First name is required';
        if (!formData.lastName.trim()) newErrors.lastName = 'Last name is required';
        if (!formData.email.trim()) newErrors.email = 'Email is required';
        if (!formData.username.trim()) newErrors.username = 'Username is required';
        if (!formData.gender.trim()) newErrors.gender = 'Gender is required';
        if (!formData.password) newErrors.password = 'Password is required';
        if (!formData.confirmPassword) newErrors.confirmPassword = 'Confirm password is required';
// Email validation
if (formData.email && !validateEmail(formData.email)) {
    newErrors.email = 'Please enter a valid email address';
  }
  
  // Password strength validation
  if (formData.password && !validatePassword(formData.password)) {
    newErrors.password = 'Password must be at least 8 characters and include uppercase, lowercase, number and special character';
  }
  
  // Password match validation
  if (formData.password && formData.confirmPassword && formData.password !== formData.confirmPassword) {
    newErrors.confirmPassword = 'Passwords do not match';
  }
  
  return newErrors;
};
const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
    
    // Clear the error for this field when the user starts typing
    if (errors[name]) {
      setErrors({ ...errors, [name]: '' });
    }
  };
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Validate form
    const formErrors = validateForm();
    
    if (Object.keys(formErrors).length > 0) {
      setErrors(formErrors);
      
      // Show an error toast for validation errors
      toast.error('Please correct the errors in the form');
      return;
    }
    setIsSubmitting(true);
    try {
        // Send data to the backend API
        const response = await axios.post('https://api.example.com/signup', {
          firstName: formData.firstName,
          lastName: formData.lastName,
          email: formData.email,
          username: formData.username,
          gender: formData.gender,
          password: formData.password // Note: Password will be hashed on the backend
        }, {
          headers: {
            'Content-Type': 'application/json',
            // Include CSRF token if your backend requires it
            // 'X-CSRF-Token': csrfToken,
          }
        });
        
        if (response.status === 201) {
          // Show success notification
          toast.success('Account created successfully!');
          
          // Optional: automatically log the user in
          // Store JWT token if provided by the backend
          if (response.data.token) {
            localStorage.setItem('authToken', response.data.token);
          }
          
          // Redirect to login page or dashboard after a short delay
          setTimeout(() => {
            navigate('/login');
          }, 2000);
        }
      } catch (error) {
        console.error('Signup error:', error);
        
        // Handle different types of errors
        if (error.response) {
          // The server responded with an error status
          const { status, data } = error.response;
          
          if (status === 409) {
            // Conflict - user already exists
            toast.error(data.message || 'Email or username already in use');
          } else if (status === 400) {
            // Bad request - validation errors
            toast.error(data.message || 'Invalid form data');
            
            // Update form errors if the backend provided field-specific errors
            if (data.errors) {
              setErrors(data.errors);
            }
          } else {
            // General server error
            toast.error('Server error. Please try again later.');
          }
        } else if (error.request) {
          // No response received
          toast.error('No response from server. Please check your internet connection.');
        } else {
          // Something else went wrong
          toast.error('An unexpected error occurred.');
        }
      } finally {
        setIsSubmitting(false);
      }
    };
    return (
        <div className="signup-container">
          <div className="signup-form-wrapper">
            <h1>Create an Account</h1>
            <form onSubmit={handleSubmit} className="signup-form">
              <div className="form-group">
                <label htmlFor="firstName">First Name</label>
                <input
                  type="text"
                  id="firstName"
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleChange}
                  className={errors.firstName ? 'error' : ''}
                  disabled={isSubmitting}
                />
                {errors.firstName && <span className="error-message">{errors.firstName}</span>}
              </div>
              
              <div className="form-group">
                <label htmlFor="lastName">Last Name</label>
                <input
                  type="text"
                  id="lastName"
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleChange}
                  className={errors.lastName ? 'error' : ''}
                  disabled={isSubmitting}
                />
                {errors.lastName && <span className="error-message">{errors.lastName}</span>}
              </div>
              
              <div className="form-group">
                <label htmlFor="email">Email</label>
                <input
                  type="email"
                  id="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  className={errors.email ? 'error' : ''}
                  disabled={isSubmitting}
                />
                {errors.email && <span className="error-message">{errors.email}</span>}
              </div>
              
              <div className="form-group">
                <label htmlFor="username">Username</label>
                <input
                  type="text"
                  id="username"
                  name="username"
                  value={formData.username}
                  onChange={handleChange}
                  className={errors.username ? 'error' : ''}
                  disabled={isSubmitting}
                />
                {errors.username && <span className="error-message">{errors.username}</span>}
              </div>
              
              <div className="form-group">
                <label htmlFor="gender">Gender</label>
                <select
                  id="gender"
                  name="gender"
                  value={formData.gender}
                  onChange={handleChange}
                  className={errors.gender ? 'error' : ''}
                  disabled={isSubmitting}
                >
                  <option value="">Select gender</option>
                  <option value="male">Male</option>
                  <option value="female">Female</option>
                 
                </select>
                {errors.gender && <span className="error-message">{errors.gender}</span>}
              </div>
              
              <div className="form-group">
                <label htmlFor="password">Password</label>
                <input
                  type="password"
                  id="password"
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  className={errors.password ? 'error' : ''}
                  disabled={isSubmitting}
                />
                {errors.password && <span className="error-message">{errors.password}</span>}
              </div>
              
              <div className="form-group">
                <label htmlFor="confirmPassword">Confirm Password</label>
                <input
                  type="password"
                  id="confirmPassword"
                  name="confirmPassword"
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  className={errors.confirmPassword ? 'error' : ''}
                  disabled={isSubmitting}
                />
                {errors.confirmPassword && <span className="error-message">{errors.confirmPassword}</span>}
              </div>
              
              <button type="submit" className="submit-button" disabled={isSubmitting}>
                {isSubmitting ? 'Creating Account...' : 'Sign Up'}
              </button>
              
              <div className="login-link">
                Already have an account? <a href="/Signin">Log In</a>
              </div>
            </form>
          </div>
          
        </div>
      );
    };
    
    export default SignupForm;
    

