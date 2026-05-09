import "../styles/FormCard.css";

const FormCard = ({ title, subtitle, children }) => {
  return (
    <div className="form-card">
      <div className="form-header">
        <h2>{title}</h2>
        <p>{subtitle}</p>
      </div>

      <div className="form-content">{children}</div>
    </div>
  );
};

export default FormCard;