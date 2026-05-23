const SearchBar = ({ search, setSearch }) => {
  return (
    <input
      type="text"
      placeholder="Search by first or last name..."
      value={search}
      onChange={(e) => setSearch(e.target.value)}
      className="search-input"
    />
  );
};

export default SearchBar;