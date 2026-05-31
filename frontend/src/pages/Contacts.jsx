import { useEffect, useState } from "react";
import { importContacts } from "../services/contactService";
import ImportContacts from "../components/ImportContacts";
import { toast } from "react-toastify";

import SearchBar from "../components/SearchBar";
import ContactCard from "../components/ContactCard";
import Pagination from "../components/Pagination";
import ContactModal from "../components/ContactModal";
import DeleteModal from "../components/DeleteModal";

import {
  getContacts,
  deleteContact,
  exportContacts,
} from "../services/contactService";

import "../styles/Contacts.css";

const Contacts = () => {

  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [contacts, setContacts] = useState([]);
  const [search, setSearch] = useState("");
  const [openModal, setOpenModal] = useState(false);
  const [selectedContact, setSelectedContact] = useState(null);
  const [deleteModal, setDeleteModal] = useState(false);

  // LOAD CONTACTS FROM BACKEND
 useEffect(() => {
  fetchContacts(currentPage);
}, [currentPage]);

  const fetchContacts = async (page = 0) => {
  try {
    const res = await getContacts(page, 5);

    setContacts(res.data.data.content);   // Page content
    setTotalPages(res.data.data.totalPages);

  } catch (error) {
    toast.error("Failed to load contacts");
  }
};

  // FILTER CONTACTS
  const filteredContacts = contacts.filter((contact) =>
    `${contact.firstName} ${contact.lastName}`
      .toLowerCase()
      .includes(search.toLowerCase())
  );

  // DELETE CONTACT
  const handleDelete = async (id) => {
    try {
      await deleteContact(id);
       toast.success("Contact deleted successfully");
      fetchContacts(); // refresh list
    } catch (error) {
      toast.error(
      error?.response?.data?.message || "Failed to delete contact"
    );
    }
  };

  // EXPORT CONTACTS
  const handleExport = async () => {
    try {
      const res = await exportContacts();

      const url = window.URL.createObjectURL(new Blob([res.data]));
      const link = document.createElement("a");

      link.href = url;
      link.setAttribute("download", "contacts.csv");

      document.body.appendChild(link);
      link.click();
      toast.success("Contacts exported successfully");
    } catch (error) {
      toast.error("Failed to export contacts");
    }
  };

 

  return (
    <div className="contacts-container">

      {/* HEADER */}
      <div className="contacts-header">

        <SearchBar
          search={search}
          setSearch={setSearch}
        />

        <div className="header-actions">

          <button
            className="new-contact-btn"
            onClick={handleExport}
          >
            Export
          </button>

          {/* <button
            className="new-contact-btn"
            onClick={() => console.log("Import will be added later")}
          >
            Import
          </button> */}
<ImportContacts refreshContacts={fetchContacts} />
          

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
              handleDelete(contact.id);
            }}
          />
        ))}
      </div>

      {/* PAGINATION (STATIC FOR NOW) */}
      <Pagination
      currentPage={currentPage}
      setCurrentPage={setCurrentPage}
      totalPages={totalPages}
    />

      {/* MODALS */}
      {openModal && (
        <ContactModal
          closeModal={() => setOpenModal(false)}
          selectedContact={selectedContact}
          refreshContacts={fetchContacts}
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