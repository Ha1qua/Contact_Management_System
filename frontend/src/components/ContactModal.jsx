import { useState, useEffect } from "react";
import { createContact, updateContact } from "../services/contactService";
import { toast } from "react-toastify";

const ContactModal = ({ closeModal, selectedContact, refreshContacts }) => {

  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phone: "",
  });

  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (selectedContact) {
      setFormData({
        firstName: selectedContact.firstName || "",
        lastName: selectedContact.lastName || "",
        email: selectedContact.email || "",
        phone: selectedContact.phone || "",
      });
    }
  }, [selectedContact]);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };


  const validate = () => {
    let newErrors = {};

    const nameRegex = /^[A-Za-z]+$/;

    // First Name
    if (!formData.firstName.trim()) {
      newErrors.firstName = "First name is required";
    } else if (!nameRegex.test(formData.firstName)) {
      newErrors.firstName = "Only alphabets allowed (A-Z)";
    }

    // Last Name
    if (!formData.lastName.trim()) {
      newErrors.lastName = "Last name is required";
    } else if (!nameRegex.test(formData.lastName)) {
      newErrors.lastName = "Only alphabets allowed (A-Z)";
    }

    if (!formData.email.includes("@")) {
      newErrors.email = "Enter a valid email";
    }

    if (formData.phone.length < 11) {
      newErrors.phone = "Phone must be at least 11 digits";
    }

    setErrors(newErrors);

    if (Object.keys(newErrors).length > 0) {
      toast.error("Please fix form errors");
      return false;
    }

    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validate()) return;

    try {
      if (selectedContact) {
        await updateContact(selectedContact.id, formData);
        toast.success("Contact updated successfully");
      } else {
        await createContact(formData);
        toast.success("Contact created successfully");
      }

      refreshContacts();
      closeModal();

    } catch (error) {
      toast.error(
        error?.response?.data?.message || "Something went wrong"
      );
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-box">

        <button className="modal-close-btn" onClick={closeModal}>×</button>

        <div className="modal-header">
          <h2>{selectedContact ? "Update contact" : "Create contact"}</h2>
          <p className="modal-subtitle">
            {selectedContact
              ? `Edit the details for ${formData.firstName} ${formData.lastName}.`
              : "Enter the details to add a new contact to your network."}
          </p>
        </div>

        <form className="modal-form" onSubmit={handleSubmit}>

          <div className="form-row">
            <div className="input-group">
              <label>First name</label>
              <input
                type="text"
                name="firstName"
                value={formData.firstName}
                onChange={handleChange}
              />
              {errors.firstName && (
                <small className="error">{errors.firstName}</small>
              )}
            </div>

            <div className="input-group">
              <label>Last name</label>
              <input
                type="text"
                name="lastName"
                value={formData.lastName}
                onChange={handleChange}
              />
              {errors.lastName && (
                <small className="error">{errors.lastName}</small>
              )}
            </div>
          </div>

          <div className="input-group">
            <label>Email</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
            />
            {errors.email && (
              <small className="error">{errors.email}</small>
            )}
          </div>

          <div className="input-group">
            <label>Phone</label>
            <input
              type="text"
              name="phone"
              value={formData.phone}
              onChange={handleChange}
            />
            {errors.phone && (
              <small className="error">{errors.phone}</small>
            )}
          </div>

          <div className="modal-buttons">
            <button type="button" className="cancel-btn" onClick={closeModal}>
              ✕ Cancel
            </button>

            <button type="submit" className="save-btn">
              Save changes
            </button>
          </div>

        </form>
      </div>
    </div>
  );
};

export default ContactModal;