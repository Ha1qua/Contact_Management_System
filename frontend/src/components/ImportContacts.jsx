import * as XLSX from "xlsx";

const ImportContacts = ({ setContacts, contacts }) => {

  const handleImport = (e) => {
    const file = e.target.files[0];
    if (!file) return;

    const reader = new FileReader();

    reader.onload = (event) => {

      const data = new Uint8Array(event.target.result);

      const workbook = XLSX.read(data, {
        type: "array",
      });

      const sheetName = workbook.SheetNames[0];

      const worksheet = workbook.Sheets[sheetName];

      const jsonData =
        XLSX.utils.sheet_to_json(worksheet);

      const newContacts = jsonData.map((item, index) => ({
        id: Date.now() + index,
        firstName: item.firstName || "",
        lastName: item.lastName || "",
        email: item.email || "",
        phone: item.phone || "",
      }));

      setContacts((prev) => [...prev, ...newContacts]);
    };

    reader.readAsArrayBuffer(file);
  };

  return (
    <label className="new-contact-btn">
      Import

      <input
        type="file"
        accept=".xlsx,.xls,.csv"
        hidden
        onChange={handleImport}
      />
    </label>
  );
};

export default ImportContacts;