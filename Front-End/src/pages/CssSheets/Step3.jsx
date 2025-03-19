import { FaArrowLeft, FaArrowRight } from 'react-icons/fa';
import './Step3.css';

const Step3 = ({ formData, handleChange, nextStep, prevStep }) => {
    const fieldsOfInterest = [
      "Information Technology",
      "Medicine",
      "Engineering",
      "Education",
      "Finance",
      "Statistics",
      "Journalism",
      "Agriculture",
      "Music and Production",
      "Law"
    ];
  
    const handleFieldChange = (e) => {
      const selectedOptions = Array.from(e.target.selectedOptions).map(
        (option) => option.value
      );
      handleChange({
        target: { name: "fieldsOfInterest", value: selectedOptions },
      });
    };
  
    return (
      <div>
        <input type="file" name="profilePicture" accept="image/*" onChange={handleChange} />
  
        <textarea
          name="bio"
          placeholder="Bio"
          value={formData.bio}
          onChange={handleChange}
        />
  
        <input
          type="text"
          name="institution"
          placeholder="Institution"
          value={formData.institution}
          onChange={handleChange}
        />
  
        <select
          name="fieldsOfInterest"
          value={formData.fieldsOfInterest || []}
          onChange={handleFieldChange}
          multiple
        >
          {fieldsOfInterest.map((field) => (
            <option key={field} value={field}>
              {field}
            </option>
          ))}
        </select>
  
        <button onClick={prevStep}>
          <FaArrowLeft /> Back
        </button>
        <button onClick={nextStep}>
          Next <FaArrowRight />
        </button>
      </div>
    );
  };
  
  export default Step3;

