import "../styles/Input.css";

const Input = ({
  type = "text",
  placeholder,
  value,
  onChange,
  name,
  error,
}) => {
  return (
    <div className="input-group">
      <input
        type={type}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        name={name}
        className={`custom-input ${error ? "input-error" : ""}`}
      />

      {error && <p className="error-text">{error}</p>}
    </div>
  );
};

export default Input;