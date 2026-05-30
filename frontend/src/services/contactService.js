import api from "./api";

export const getContacts = (page, size) => {
  return api.get(`/contacts?page=${page}&size=${size}`);
};

// CREATE CONTACT
export const createContact = (data) => {
  return api.post("/contacts", data);
};

// UPDATE CONTACT
export const updateContact = (id, data) => {
  return api.put(`/contacts/${id}`, data);
};

// DELETE CONTACT
export const deleteContact = (id) => {
  return api.delete(`/contacts/${id}`);
};

// EXPORT CONTACTS
export const exportContacts = () => {
  return api.get("/contacts/export", {
    responseType: "blob",
  });
};

// IMPORT CONTACTS
export const importContacts = (file) => {
  const formData = new FormData();
  formData.append("file", file);

  return api.post("/contacts/import", formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
};