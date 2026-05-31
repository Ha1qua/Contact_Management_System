import { toast } from "react-toastify";
import { importContacts } from "../services/contactService";

const ImportContacts = ({ refreshContacts }) => {

  const handleImport = async (e) => {
    const file = e.target.files[0];

    if (!file) {
      toast.error("Please select a file");
      return;
    }

    try {
      await importContacts(file);

      toast.success("Contacts imported successfully");

      refreshContacts();

      e.target.value = "";
    } catch (error) {
      toast.error(
        error?.response?.data?.message || "Import failed"
      );
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