import React, {useState} from "react";
import { useNavigate } from "react-router-dom";
import { Link } from "react-router-dom";

const LoginForm = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        email: "",
        password: ""
    });

    const [errors, setErrors] = useState({});
    const [isSubmitting, setIsSubmitting] = useState(false);

    const validateForm = () => {
        const newErrors = {};
        if (!formData.email.trim()) newErrors.email= 'Email is required';
        if (!formData.password.trim()) newErrors.password= 'Password is required';
        return newErrors;
    };

    const handleChange = (e) => {
        const {name, value} = e.target;
        setFormData({...errors,[name]:''});

        // clear errors
        if (errors[name]) {
            setErrors({...errors, [name]:''});
        }
    };
    const handleSubmit = async(e) => {
        e.preventDefault();
        setErrors(validateForm());
//validate the form
const formErrors = validateForm();

if (Object.keys(formErrors).length>0) {
    setErrors(formErrors);
    toast.error('please fill in all the required fileds');
    return;
    }

    setIsSubmitting(true);

    try {
        // Send login request to the backend API
        const response = await axios.post('https://api.example.com/login', {
          emailOrUsername: formData.emailOrUsername,
          password: formData.password
        }, {
          headers: {
            'Content-Type': 'application/json',
            // Include CSRF token if your backend requires it
            // 'X-CSRF-Token': csrfToken,
          }
        });
        
        if (response.status === 200 && response.data.token) {
          // Store JWT token securely
          localStorage.setItem('authToken', response.data.token);
          
          // Show success notification
          toast.success('Logged in successfully!');
          
          // Redirect to dashboard or home page after a short delay
          setTimeout(() => {
            navigate('/dashboard');
          }, 1500);
        }
      } catch (error) {
        console.error('Login error:', error);
        
        // Handle different types of errors
        if (error.response) {
          // The server responded with an error status
          const { status, data } = error.response;
          
          if (status === 401) {
            // Unauthorized - invalid credentials
            toast.error('Invalid email/username or password');
          } else if (status === 400) {
            // Bad request - validation errors
            toast.error(data.message || 'Invalid login data');
            
            // Update form errors if the backend provided field-specific errors
            if (data.errors) {
              setErrors(data.errors);
            }
          } else if (status === 403) {
            // Forbidden - account locked or requires verification
            toast.error(data.message || 'Account locked or requires verification');
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
    
    const handleForgotPassword = () => {
      navigate('/forgot-password');
    };

    return (
        <div className="login-container">
            <div className="login-form-wrapper">
                <h1>Log In</h1>
                <form onSubmit={handleSubmit} className="login-form">
                    <div className="form-group">
                        <label htmlFor="email">Email</label>
                        <input
                         type="text"
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
                        <label htmlFor="password"></label>
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
                    <div className="forgot-password">
                        <button
                        type="button"
                        className="forgot-password-link"
                        onClick={handleForgotPassword}
                        disabled={isSubmitting}
                        >
                        Forgot Password</button>
                    </div>
                    <button type="submit" lassName="login-button" disabled={isSubmitting}>
                    {isSubmitting ? 'Logging in...' : 'Log In'}
                    </button>
                    <div className="signup-link">
            Don't have an account? <a href="/signup">Sign Up</a>
          </div>
                </form>
                
            </div>
            {/* <ToastContainer position="top-right" autoClose={5000} /> */}
        </div>
    );
};
export default LoginForm;