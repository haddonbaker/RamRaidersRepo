import React from 'react';

function FilterModal({ closeModal }) {
  const modalStyle = {
    position: 'fixed',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    backgroundColor: '#FFFFFF',
    padding: '1.5rem',
    border: '2px solid #1976D2',
    borderRadius: '12px',
    zIndex: 1000,
    width: '95%',
    maxWidth: '450px',
    boxShadow: '0 10px 40px rgba(0, 0, 0, 0.2)',
  };

  const overlayStyle = {
    position: 'fixed',
    top: 0,
    left: 0,
    height: '100%',
    width: '100%',
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    zIndex: 999,
  };

  const headingStyle = {
    color: '#1976D2',
    fontSize: '1.25rem',
    marginTop: 0,
    marginBottom: '1.25rem',
  };

  const sectionStyle = {
    marginBottom: '1.25rem',
    textAlign: 'left',
  };

  const labelStyle = {
    display: 'block',
    color: '#1F2937',
    fontWeight: '500',
    marginBottom: '0.5rem',
    fontSize: '0.95rem',
  };

  const checkboxGroupStyle = {
    display: 'grid',
    gridTemplateColumns: 'repeat(2, 1fr)',
    gap: '0.5rem',
    marginTop: '0.5rem',
  };

  const checkboxLabelStyle = {
    display: 'flex',
    alignItems: 'center',
    gap: '0.5rem',
    color: '#1F2937',
    fontSize: '0.9rem',
  };

  const timeRangeStyle = {
    display: 'flex',
    gap: '0.75rem',
    alignItems: 'center',
  };

  const timeInputStyle = {
    flex: 1,
    padding: '0.6rem',
    border: '2px solid #E5E7EB',
    borderRadius: '8px',
    fontSize: '0.9rem',
    color: '#1F2937',
    background: '#FFFFFF',
    boxSizing: 'border-box',
    fontFamily: 'inherit',
    transition: 'border-color 0.2s',
  };

  const buttonContainerStyle = {
    display: 'flex',
    gap: '0.75rem',
    marginTop: '1.5rem',
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
    transition: 'background-color 0.2s',
  };

  const cancelButtonStyle = {
    ...buttonStyle,
    background: '#6B7280',
  };

  return (
    <>
      <div style={overlayStyle} onClick={closeModal}></div>
      <div style={modalStyle}>
        <h2 style={headingStyle}>Filter Courses</h2>

        <div style={sectionStyle}>
          <label style={labelStyle}>Days:</label>
          <div style={checkboxGroupStyle}>
            <label style={checkboxLabelStyle}>
              <input type="checkbox" style={{ cursor: 'pointer' }} /> Monday
            </label>
            <label style={checkboxLabelStyle}>
              <input type="checkbox" style={{ cursor: 'pointer' }} /> Tuesday
            </label>
            <label style={checkboxLabelStyle}>
              <input type="checkbox" style={{ cursor: 'pointer' }} /> Wednesday
            </label>
            <label style={checkboxLabelStyle}>
              <input type="checkbox" style={{ cursor: 'pointer' }} /> Thursday
            </label>
            <label style={checkboxLabelStyle}>
              <input type="checkbox" style={{ cursor: 'pointer' }} /> Friday
            </label>
          </div>
        </div>

        <div style={sectionStyle}>
          <label style={labelStyle}>Time Range:</label>
          <div style={timeRangeStyle}>
            <input type="time" style={timeInputStyle} />
            <span style={{ color: '#6B7280', fontSize: '0.9rem' }}>to</span>
            <input type="time" style={timeInputStyle} />
          </div>
        </div>

        <div style={buttonContainerStyle}>
          <button
            style={buttonStyle}
            onMouseEnter={(e) => e.target.style.background = '#1565C0'}
            onMouseLeave={(e) => e.target.style.background = '#1976D2'}
          >
            Apply Filters
          </button>
          <button
            style={cancelButtonStyle}
            onClick={closeModal}
            onMouseEnter={(e) => e.target.style.background = '#4B5563'}
            onMouseLeave={(e) => e.target.style.background = '#6B7280'}
          >
            Cancel
          </button>
        </div>
      </div>
    </>
  );
}

export default FilterModal;
