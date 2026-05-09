import { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import FormCard from '../components/FormCard';
import Input from '../components/Input';
import Button from '../components/Button';
import { resetPassword } from '../services/authService';
import { validatePassword, validateConfirmPassword } from '../utils/validators';
import '../styles/ResetPassword.css';

const ResetPassword = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { email = '', otp = '' } = location.state || {};

  const [form, setForm] = useState({ password: '', confirmPassword: '' });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [serverError, setServerError] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    if (errors[name]) setErrors((prev) => ({ ...prev, [name]: '' }));
    setServerError('');
  };

  const validate = () => {
    const newErrors = {};
    const passErr = validatePassword(form.password);
    const confirmErr = validateConfirmPassword(form.password, form.confirmPassword);
    if (passErr) newErrors.password = passErr;
    if (confirmErr) newErrors.confirmPassword = confirmErr;
    return newErrors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const validation = validate();
    if (Object.keys(validation).length > 0) {
      setErrors(validation);
      return;
    }
    setLoading(true);
    try {
      const result = await resetPassword({ email, otp, password: form.password });
      if (result.success) {
        setSuccess(true);
      } else {
        setServerError(result.message || 'Failed to reset password.');
      }
    } catch {
      setServerError('Something went wrong. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <FormCard title="" subtitle="">
        <div className="reset-success">
          <div className="reset-success__icon">✓</div>
          <h2 className="reset-success__title">Password reset!</h2>
          <p className="reset-success__text">
            Your password has been updated successfully.<br />
            You can now sign in with your new password.
          </p>
          <Button onClick={() => navigate('/login')}>
            Continue to Sign In
          </Button>
        </div>
      </FormCard>
    );
  }

  return (
    <FormCard
      title="New password."
      subtitle="Choose a strong password to secure your account."
    >
      <form onSubmit={handleSubmit} noValidate>
        <Input
          label="New Password"
          type="password"
          name="password"
          value={form.password}
          onChange={handleChange}
          placeholder="Min. 8 characters"
          error={errors.password}
          autoComplete="new-password"
          autoFocus
        />

        <Input
          label="Confirm New Password"
          type="password"
          name="confirmPassword"
          value={form.confirmPassword}
          onChange={handleChange}
          placeholder="Repeat your password"
          error={errors.confirmPassword}
          autoComplete="new-password"
        />

        {serverError && (
          <p className="error-text" style={{ marginBottom: '16px' }}>
            {serverError}
          </p>
        )}

        <Button type="submit" loading={loading} disabled={loading}>
          {loading ? 'Updating…' : 'Reset Password'}
        </Button>
      </form>
    </FormCard>
  );
};

export default ResetPassword;