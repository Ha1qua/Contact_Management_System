/**
 * authService.js
 * Placeholder authentication service functions.
 * Replace the simulated delays and responses with real API calls.
 */

const delay = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

/**
 * Register a new user.
 * @param {{ email: string, password: string }} data
 */
export const registerUser = async (data) => {
  await delay(800);
  // TODO: replace with real API call
  // const response = await fetch('/api/auth/register', {
  //   method: 'POST',
  //   headers: { 'Content-Type': 'application/json' },
  //   body: JSON.stringify(data),
  // });
  // return response.json();
  return { success: true, message: 'OTP sent to your email.' };
};

/**
 * Log in an existing user.
 * @param {{ email: string, password: string }} data
 */
export const loginUser = async (data) => {
  await delay(800);
  // TODO: replace with real API call
  if (!data.email || !data.password) {
    return { success: false, message: 'Invalid credentials.' };
  }
  return { success: true, user: { email: data.email } };
};

/**
 * Verify a 4-digit OTP code.
 * @param {{ email: string, otp: string }} data
 */
export const verifyOTP = async (data) => {
  await delay(800);
  // TODO: replace with real API call
  // Simulate: accept any 4-digit code for demo purposes
  if (data.otp.length !== 4) {
    return { success: false, message: 'Invalid OTP. Please try again.' };
  }
  return { success: true, message: 'OTP verified successfully.' };
};

/**
 * Reset the user's password.
 * @param {{ email: string, otp: string, password: string }} data
 */
export const resetPassword = async (data) => {
  await delay(800);
  // TODO: replace with real API call
  if (!data.password) {
    return { success: false, message: 'Password is required.' };
  }
  return { success: true, message: 'Password reset successfully.' };
};

/**
 * Send a forgot-password OTP to the given email.
 * @param {{ email: string }} data
 */
export const sendForgotPasswordOTP = async (data) => {
  await delay(800);
  // TODO: replace with real API call
  return { success: true, message: 'Password reset OTP sent.' };
};