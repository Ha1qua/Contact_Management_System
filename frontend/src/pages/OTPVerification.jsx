
import { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";

import FormCard from "../components/FormCard";
import Button from "../components/Button";
import OTPInput from "../components/OTPInput";

import "../styles/OTPVerification.css";

const OTPVerification = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const flow = location.state?.flow;

  const [otp, setOtp] = useState(["", "", "", ""]);
  const [error, setError] = useState("");

  // Dummy OTP
  const correctOTP = "1234";

  const handleVerify = (e) => {
    e.preventDefault();

    const enteredOtp = otp.join("");

    if (enteredOtp === correctOTP) {
      navigate("/login");
    } else {
      setError("Invalid OTP");
    }
  };

  const handleResendOTP = () => {
    alert("OTP Resent Successfully");
  };

  return (
    <div className="auth-container otp-page">
      <FormCard
        title="OTP Verification"
        subtitle="Enter the 4-digit verification code"
      >
        <form onSubmit={handleVerify}>
          <OTPInput otp={otp} setOtp={setOtp} />

          {error && <p className="otp-error">{error}</p>}

          <Button type="submit" text="Verify OTP" />

          <div className="resend-container">
            <span>Didn't receive the code?</span>

            <button
              type="button"
              className="resend-btn"
              onClick={handleResendOTP}
            >
              Resend OTP
            </button>
          </div>
        </form>
      </FormCard>
    </div>
  );
};

export default OTPVerification;