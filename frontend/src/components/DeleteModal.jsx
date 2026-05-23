const DeleteModal = ({ closeModal }) => {
  return (
    <div className="modal-overlay">
      <div className="delete-modal">
        <h2>Delete Contact</h2>

        <p>Are you sure you want to delete this contact?</p>

        <div className="modal-buttons">
          <button onClick={closeModal}>Cancel</button>
          <button className="delete-btn">Confirm Delete</button>
        </div>
      </div>
    </div>
  );
};

export default DeleteModal;