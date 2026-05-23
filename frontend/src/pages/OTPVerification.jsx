import { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";

import { verifyOtp } from "../services/authService"; 
import { resendOtp } from "../services/authService";

import FormCard from "../components/FormCard";
import Button from "../components/Button";
import OTPInput from "../components/OTPInput";

import "../styles/OTPVerification.css";

const OTPVerification = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const flow = location.state?.flow;


  const email = location.state?.email;

  const [otp, setOtp] = useState(["", "", "", "", "", ""]); 
  const [error, setError] = useState("");

  const handleVerify = async (e) => {
    e.preventDefault();

    const enteredOtp = otp.join("");

    try {
  console.log("VERIFY OTP REQUEST:", { email, otp: enteredOtp });

  const res = await verifyOtp({
    email: email,
    otp: enteredOtp,
  });

  console.log("VERIFY OTP RESPONSE:", res.data);

  // 🔥 FLOW BASED NAVIGATION
  if (flow === "forgot-password") {
    navigate("/reset-password", {
      state: {
        email,
        otp: enteredOtp,
      },
    });
  } else {
    navigate("/login");
  }

} catch (err) {
  console.log("OTP ERROR:", err);

  setError(
    err.response?.data?.message ||
    err.response?.data ||
    "OTP verification failed"
  );
}
  };

 const handleResendOTP = async () => {
  try {
    const res = await resendOtp({ email });

    alert(res.data?.message || "OTP Resent Successfully");
  } catch (error) {
    console.log("RESEND OTP ERROR:", error);

    alert(
      error.response?.data?.message ||
      error.response?.data ||
      "Failed to resend OTP"
    );
  }
};

  return (
    <div className="auth-container otp-page">
      <FormCard
        title="OTP Verification"
        subtitle="Enter the 6-digit verification code"
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