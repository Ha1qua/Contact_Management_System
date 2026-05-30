import api from "./api";

// REGISTER
export const registerUser = (data) => {
  return api.post("/auth/register", data);
};

// LOGIN
export const loginUser = (data) => {
  return api.post("/auth/login", data);
};

// VERIFY OTP
export const verifyOtp = (data) => {
  return api.post("/auth/verify-otp", data);
};

// RESEND OTP
export const resendOtp = (data) => {
  return api.post("/auth/resend-otp", data);
};

// FORGOT PASSWORD
export const sendForgotPasswordOTP = (data) => {
  return api.post("/auth/forgot-password", data);
};

// RESET PASSWORD
export const resetPassword = (data) => {
  return api.post("/auth/reset-password", data);
};

// CHANGE PASSWORD
export const changePassword = (data) => {
  return api.post("/auth/change-password", data);
};

// GET PROFILE INFO
export const getMyProfile = () => {
  return api.get("/auth/me");
};