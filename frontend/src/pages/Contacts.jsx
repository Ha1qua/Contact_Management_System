import { useState } from "react";
import SearchBar from "../components/SearchBar";
import ContactCard from "../components/ContactCard";
import Pagination from "../components/Pagination";
import ContactModal from "../components/ContactModal";
import DeleteModal from "../components/DeleteModal";
import ImportContacts from "../components/ImportContacts";

import "../styles/Contacts.css";

const Contacts = () => {

  const [contacts, setContacts] = useState([
    {
      id: 1,
      firstName: "Amelia",
      lastName: "Hart",
      email: "amelia@gmail.com",
      phone: "03123456789",
    },
    {
      id: 2,
      firstName: "Marcus",
      lastName: "Okafor",
      email: "marcus@gmail.com",
      phone: "03211234567",
    },
  ]);

  const [search, setSearch] = useState("");
  const [openModal, setOpenModal] = useState(false);
  const [selectedContact, setSelectedContact] = useState(null);
  const [deleteModal, setDeleteModal] = useState(false);

  const filteredContacts = contacts.filter((contact) =>
    `${contact.firstName} ${contact.lastName}`
      .toLowerCase()
      .includes(search.toLowerCase())
  );

  return (
    <div className="contacts-container">

      {/* HEADER */}
     {/* HEADER */}
<div className="contacts-header">

  <SearchBar
    search={search}
    setSearch={setSearch}
  />

  <div className="header-actions">

    <button
      className="new-contact-btn"
      onClick={() => {
        // Export logic here later
        console.log("Export Contacts");
      }}
    >
      Export
    </button>

    <button
      className="new-contact-btn"
      onClick={() => {
        // Import logic here later
        console.log("Import Contacts");
      }}
    >
      Import
    </button>

    <button
      className="new-contact-btn"
      onClick={() => {
        setSelectedContact(null);
        setOpenModal(true);
      }}
    >
       New Contact
    </button>

  </div>

</div>


      {/* CONTACT LIST */}
      <div className="contacts-list">
        {filteredContacts.map((contact) => (
          <ContactCard
            key={contact.id}
            contact={contact}
            onEdit={() => {
              setSelectedContact(contact);
              setOpenModal(true);
            }}
            onDelete={() => {
              setSelectedContact(contact);
              setDeleteModal(true);
            }}
          />
        ))}
      </div>

      {/* PAGINATION */}
      <Pagination />

      {/* MODALS */}
      {openModal && (
        <ContactModal
          closeModal={() => setOpenModal(false)}
          selectedContact={selectedContact}
        />
      )}

      {deleteModal && (
        <DeleteModal
          closeModal={() => setDeleteModal(false)}
        />
      )}

    </div>
  );
};

export default Contacts;