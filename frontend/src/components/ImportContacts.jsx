
import { importContacts } from "../services/contactService";

const ImportContacts = ({ refreshContacts }) => {

  const handleImport = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    try {
      await importContacts(file);

      refreshContacts(); // reload from DB
      e.target.value = "";

    } catch (error) {
      console.log("Import error:", error.response?.data);
    }
  };

  return (
    <label className="new-contact-btn">
      Import

      <input
        type="file"
        accept=".csv"
        hidden
        onChange={handleImport}
      />
    </label>
  );
};

export default ImportContacts;