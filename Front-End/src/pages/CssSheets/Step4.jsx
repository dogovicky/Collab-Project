import './Step4.css';

const Step4 = ({ formData, handleSubmit, prevStep, isSubmitting, errors }) => {
  return (
    <div className="step-container">
      <p>Confirm your details before submitting:</p>
      <pre>{JSON.stringify(formData, null, 2)}</pre>

      {errors.profilePicture && (
        <p className="error">
          {errors.profilePicture} (Upload a valid image file, such as .jpg or .png.)
        </p>
      )}
      {errors.fieldsOfInterest && (
        <p className="error">
          {errors.fieldsOfInterest} (Select at least one field of interest from the list.)
        </p>
      )}

      <div className="button-group">
        <button onClick={prevStep}>Back</button>
        <button
          type="submit"
          onClick={(e) => {
            if (!isSubmitting) handleSubmit(e);
          }}
          disabled={isSubmitting}
        >
          {isSubmitting ? "Submitting..." : "Submit"}
        </button>
      </div>
    </div>
  );
};

export default Step4;

