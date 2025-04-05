import { FaArrowLeft, FaArrowRight } from 'react-icons/fa';
import './Step2.css';

const Step2 = ({ formData, handleChange, nextStep, prevStep, errors }) => {
  return (
    <div className="form-step">
      <div className="input-container">
        <input
          type="text"
          name="firstName"
          placeholder="First Name"
          value={formData.firstName}
          onChange={handleChange}
          required
        />
        {errors.firstName && (
          <p className="error">
            {errors.firstName} (First name should only contain letters.)
          </p>
        )}
      </div>
      
      <div className="input-container">
        <input
          type="text"
          name="lastName"
          placeholder="Last Name"
          value={formData.lastName}
          onChange={handleChange}
          required
        />
        {errors.lastName && (
          <p className="error">
            {errors.lastName} (Last name should only contain letters.)
          </p>
        )}
      </div>

      <div className="button-container">
        <button onClick={prevStep}>
          <FaArrowLeft /> Back
        </button>
        <button onClick={nextStep}>
          Next <FaArrowRight />
        </button>
      </div>
    </div>
  );
};

export default Step2;
