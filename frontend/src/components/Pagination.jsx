const Pagination = ({ currentPage, setCurrentPage, totalPages }) => {

  return (
    <div className="pagination">

      <button
        onClick={() => setCurrentPage(currentPage - 1)}
        disabled={currentPage === 0}
      >
        ←
      </button>

      {[...Array(totalPages)].map((_, i) => (
        <button
          key={i}
          onClick={() => setCurrentPage(i)}
          className={currentPage === i ? "active-page" : ""}
        >
          {i + 1}
        </button>
      ))}

      <button
        onClick={() => setCurrentPage(currentPage + 1)}
        disabled={currentPage === totalPages - 1}
      >
        →
      </button>

    </div>
  );
};

export default Pagination;