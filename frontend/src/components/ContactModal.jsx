import { useState, useEffect } from "react";
import { createContact, updateContact } from "../services/contactService";

const ContactModal = ({ closeModal, selectedContact, refreshContacts }) => {

  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phone: "",
  });

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

   const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      if (selectedContact) {
        // UPDATE
        await updateContact(selectedContact.id, formData);
      } else {
        // CREATE
        await createContact(formData);
      }

      refreshContacts(); // reload list
      closeModal();

    } catch (error) {
      console.log("Contact save error:", error.response?.data);
    }
  };
  return (
    <div className="modal-overlay">
      <div className="modal-box">
        {/* Close button in the top right corner */}
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
                placeholder="First Name"
                value={formData.firstName}
                onChange={handleChange}
              />
            </div>
            <div className="input-group">
              <label>Last name</label>
              <input
                type="text"
                name="lastName"
                placeholder="Last Name"
                value={formData.lastName}
                onChange={handleChange}
              />
            </div>
          </div>

          <div className="input-group">
            <label>Email</label>
            <input
              type="email"
              name="email"
              placeholder="Email"
              value={formData.email}
              onChange={handleChange}
            />
          </div>

          <div className="input-group">
            <label>Phone</label>
            <input
              type="text"
              name="phone"
              placeholder="Phone"
              value={formData.phone}
              onChange={handleChange}
            />
          </div>

          <div className="modal-buttons">
            <button type="button" className="cancel-btn" onClick={closeModal}>
              <span className="close-icon">✕</span> Cancel
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