import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import Input from "../components/Input";
import Button from "../components/Button";
import FormCard from "../components/FormCard";

import "../styles/Login.css";

const Login = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    email: "",
    password: "",
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

    if (!formData.email.includes("@")) {
      newErrors.email = "Enter a valid email";
    }

    if (formData.password.length < 6) {
      newErrors.password = "Password must be at least 6 characters";
    }

    setErrors(newErrors);

    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    if (validateForm()) {
      // Dummy Login
      localStorage.setItem("isLoggedIn", "true");

      navigate("/dashboard");
    }
  };

  return (
    <div className="auth-container login-page">
      <FormCard
        title="Welcome Back"
        subtitle="Login to manage your contacts"
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

          <div className="forgot-password-link">
            <Link to="/forgot-password">Forgot Password?</Link>
          </div>

          <Button type="submit" text="Login" />
        </form>

        <div className="auth-footer">
          Don't have an account?{" "}
          <Link to="/" className="auth-link">
            Register
          </Link>
        </div>
      </FormCard>
    </div>
  );
};

export default Login;