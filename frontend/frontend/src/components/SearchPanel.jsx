import React from 'react';

function SearchPanel({ openFilters }) {
  const panelStyle = {
    maxWidth: '500px',
    margin: '0 auto',
    padding: '1.25rem',
    background: '#FFFFFF',
    border: '1px solid #E5E7EB',
    borderRadius: '12px',
    boxShadow: '0 2px 12px rgba(0, 0, 0, 0.08)',
  };

  const searchContainerStyle = {
    marginBottom: '0.75rem',
  };

  const searchInputStyle = {
    width: '100%',
    padding: '0.75rem',
    border: '2px solid #E5E7EB',
    borderRadius: '8px',
    fontSize: '1rem',
    color: '#1F2937',
    background: '#FFFFFF',
    boxSizing: 'border-box',
    fontFamily: 'inherit',
    transition: 'border-color 0.2s, box-shadow 0.2s',
  };

  const headingStyle = {
    color: '#1976D2',
    fontSize: '1.25rem',
    marginTop: 0,
    marginBottom: '0.75rem',
  };

  const helpTextStyle = {
    color: '#6B7280',
    fontSize: '0.85rem',
    marginBottom: '1rem',
    marginTop: '-0.5rem',
    fontStyle: 'italic',
  };

  const buttonContainerStyle = {
    display: 'flex',
    gap: '0.75rem',
  };

  const buttonStyle = {
    flex: 1,
    padding: '0.75rem 1.2rem',
    background: '#1976D2',
    color: 'white',
    border: 'none',
    borderRadius: '8px',
    fontSize: '0.95rem',
    fontWeight: '500',
    cursor: 'pointer',
    transition: 'background-color 0.2s, box-shadow 0.2s',
  };

  const filterButtonStyle = {
    ...buttonStyle,
    background: '#F59E0B',
  };

  return (
    <div style={panelStyle}>
      <h2 style={headingStyle}>Search Courses</h2>
      
      <div style={searchContainerStyle}>
        <input
          placeholder="Search by course code, name, keyword, professor, or department"
          style={searchInputStyle}
          onFocus={(e) => e.target.style.borderColor = '#1976D2'}
          onBlur={(e) => e.target.style.borderColor = '#E5E7EB'}
        />
      </div>

      <p style={helpTextStyle}>
        Search across course codes, keywords, course names, professor names, departments, and credit counts.
      </p>

      <div style={buttonContainerStyle}>
        <button 
          style={buttonStyle}
          onMouseEnter={(e) => e.target.style.background = '#1565C0'}
          onMouseLeave={(e) => e.target.style.background = '#1976D2'}
        >
          Search
        </button>
        <button
          style={filterButtonStyle}
          onClick={openFilters}
          onMouseEnter={(e) => e.target.style.background = '#F97316'}
          onMouseLeave={(e) => e.target.style.background = '#F59E0B'}
        >
          Filters
        </button>
      </div>
    </div>
  );
}

export default SearchPanel;