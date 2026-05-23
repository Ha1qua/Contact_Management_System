import { useState } from "react";
import "../styles/Pagination.css";

const Pagination = () => {
  const [currentPage, setCurrentPage] = useState(1);

  const totalPages = 3;

  const handlePrevious = () => {
    if (currentPage > 1) {
      setCurrentPage(currentPage - 1);
    }
  };

  const handleNext = () => {
    if (currentPage < totalPages) {
      setCurrentPage(currentPage + 1);
    }
  };

  return (
    <div className="pagination">

      {/* Previous Button */}
      <button
        onClick={handlePrevious}
        disabled={currentPage === 1}
      >
        ← 
      </button>

      {/* Page Numbers */}
      {[1, 2, 3].map((page) => (
        <button
          key={page}
          onClick={() => setCurrentPage(page)}
          className={currentPage === page ? "active-page" : ""}
        >
          {page}
        </button>
      ))}

      {/* Next Button */}
      <button
        onClick={handleNext}
        disabled={currentPage === totalPages}
      >
        →
      </button>

    </div>
  );
};

export default Pagination;