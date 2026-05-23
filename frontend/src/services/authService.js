import axios from "axios";

const BASE_URL = "http://localhost:8080/api/auth";

// REGISTER
export const registerUser = async (data) => {
  return axios.post(`${BASE_URL}/register`, data);
};

// LOGIN
export const loginUser = async (data) => {
  return axios.post(`${BASE_URL}/login`, data);
};

// VERIFY OTP
export const verifyOtp = async (data) => {
  return axios.post(`${BASE_URL}/verify-otp`, data);
};

// RESEND OTP
export const resendOtp = async (data) => {
  return axios.post(`${BASE_URL}/resend-otp`, data);
};

// 🔥 FORGOT PASSWORD (SEND OTP)
export const sendForgotPasswordOTP = async (data) => {
  return axios.post(`${BASE_URL}/forgot-password`, data);
};

// 🔥 RESET PASSWORD (FINAL STEP)
export const resetPassword = async (data) => {
  return axios.post(`${BASE_URL}/reset-password`, data);
};