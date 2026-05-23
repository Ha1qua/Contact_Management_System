const ContactCard = ({ contact, onEdit, onDelete }) => {
  return (
    <div className="contact-card">
      <div className="contact-left">
        <div className="contact-avatar">
          {contact.firstName[0]}
          {contact.lastName[0]}
        </div>

        <div>
          <h3>
            {contact.firstName} {contact.lastName}
          </h3>

          <p>{contact.email}</p>
          <p>{contact.phone}</p>
        </div>
      </div>

      <div className="contact-actions">
        <button onClick={onEdit}>Edit</button>
        <button className="delete-btn" onClick={onDelete}>
          Delete
        </button>
      </div>
    </div>
  );
};

export default ContactCard;