import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { registerUser } from "../services/authService";

import Input from "../components/Input";
import Button from "../components/Button";
import FormCard from "../components/FormCard";

import "../styles/Register.css";

const Register = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    email: "",
    password: "",
    confirmPassword: "",
  });

  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const validateForm = () => {
    let newErrors = {};

    // Email validation
    if (!formData.email.includes("@")) {
      newErrors.email = "Enter a valid email";
    }

    // Password validation
    if (formData.password.length < 6) {
      newErrors.password = "Password must be at least 6 characters";
    }

    // Confirm password
    if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = "Passwords do not match";
    }

    setErrors(newErrors);

    return Object.keys(newErrors).length === 0;
  };
 const [loading, setLoading] = useState(false);
const handleSubmit = async (e) => {
    e.preventDefault();

    if (formData.password !== formData.confirmPassword) {
      alert("wring pin")
      return;
    }

    setLoading(true);

    try {
      const response = await registerUser({
        email: formData.email,
        password: formData.password,
      });

      console.log("REGISTER RESPONSE:", response);
console.log("REGISTER RESPONSE DATA:", response.data);
      if (response.data.success) {
        

        navigate("/verify-otp", {
          state: {
            email: formData.email,
            flow: "register",
          },
        });
      } else {
        
      }
    } catch (err) {
      
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container register-page">
      <FormCard
        title="Create Account"
        subtitle="Manage your contacts securely and efficiently"
      >
        <form onSubmit={handleSubmit}>
          <Input
            type="email"
            placeholder="Enter your email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            error={errors.email}
          />

          <Input
            type="password"
            placeholder="Enter your password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            error={errors.password}
          />

          <Input
            type="password"
            placeholder="Confirm your password"
            name="confirmPassword"
            value={formData.confirmPassword}
            onChange={handleChange}
            error={errors.confirmPassword}
          />

          <Button type="submit" text="Register" />
        </form>

        <div className="auth-footer">
          Already have an account?{" "}
          <Link to="/login" className="auth-link">
            Login
          </Link>
        </div>
      </FormCard>
    </div>
  );
};

export default Register;