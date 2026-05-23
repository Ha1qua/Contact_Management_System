import { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { resendOtp } from "../services/authService";
import "../styles/ForgotPassword.css";

const ForgotPassword = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const [email, setEmail] = useState(location.state?.email || "");

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!email) {
      alert("Enter email");
      return;
    }

   try {
  await resendOtp({ email });

  navigate("/verify-otp", {
      state: {
          email,
          flow: "forgot-password"
  }

  });

} catch (err) {
  console.log(err);
  alert("Server error");
}
  };

  return (
    <div className="forgot-page">
    <form onSubmit={handleSubmit}>
      <h2>Forgot Password</h2>

      <input
        type="email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        placeholder="Enter email"
      />

      <button type="submit">Send OTP</button>
    </form>
    </div>
  );
};

export default ForgotPassword;