/**
 * Validate email address format.
 * @param {string} email
 * @returns {string|null} Error message or null if valid.
 */
export const validateEmail = (email) => {
  if (!email) return 'Email is required.';
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!re.test(email)) return 'Please enter a valid email address.';
  return null;
};

/**
 * Validate password strength.
 * @param {string} password
 * @returns {string|null} Error message or null if valid.
 */
export const validatePassword = (password) => {
  if (!password) return 'Password is required.';
  if (password.length < 8) return 'Password must be at least 8 characters.';
  return null;
};

/**
 * Validate that two passwords match.
 * @param {string} password
 * @param {string} confirmPassword
 * @returns {string|null} Error message or null if valid.
 */
export const validateConfirmPassword = (password, confirmPassword) => {
  if (!confirmPassword) return 'Please confirm your password.';
  if (password !== confirmPassword) return 'Passwords do not match.';
  return null;
};

/**
 * Get password strength score (0-3).
 * @param {string} password
 * @returns {number} 0 = empty, 1 = weak, 2 = medium, 3 = strong
 */
export const getPasswordStrength = (password) => {
  if (!password) return 0;
  let score = 0;
  if (password.length >= 8) score++;
  if (/[A-Z]/.test(password) && /[a-z]/.test(password)) score++;
  if (/\d/.test(password) && /[^A-Za-z0-9]/.test(password)) score++;
  return score;
};