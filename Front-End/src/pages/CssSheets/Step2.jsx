import { FaArrowLeft, FaArrowRight } from 'react-icons/fa';
import './Step2.css';

const Step2 = ({ formData, handleChange, nextStep, prevStep }) => {
  return (
    <div>
      <input
        type="text"
        name="firstName"
        placeholder="First Name"
        value={formData.firstName}
        onChange={handleChange}
        required
      />
      <input
        type="text"
        name="lastName"
        placeholder="Last Name"
        value={formData.lastName}
        onChange={handleChange}
        required
      />
      <button onClick={prevStep}>
        <FaArrowLeft /> Back
      </button>
      <button onClick={nextStep}>
        Next <FaArrowRight />
      </button>
    </div>
  );
};

export default Step2;
