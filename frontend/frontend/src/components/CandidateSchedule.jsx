import React from 'react';

function CandidateSchedule({ openModal }) {
  const panelStyle = {
    maxWidth: '800px',
    width: '100%',
    margin: '0 auto',
    padding: '1.25rem',
    background: '#FFFFFF',
    border: '1px solid #E5E7EB',
    borderRadius: '12px',
    boxShadow: '0 2px 12px rgba(0, 0, 0, 0.08)',
  };

  const headingStyle = {
    color: '#1976D2',
    fontSize: '1.25rem',
    marginTop: 0,
    marginBottom: '0.75rem',
  };

  const textStyle = {
    color: '#6B7280',
    fontSize: '0.95rem',
    margin: 0,
  };

  const buttonStyle = {
    padding: '0.6rem 1.2rem',
    marginTop: '0.75rem',
    background: '#1976D2',
    color: 'white',
    border: 'none',
    borderRadius: '8px',
    fontSize: '0.95rem',
    fontWeight: '500',
    cursor: 'pointer',
    transition: 'background-color 0.2s, box-shadow 0.2s',
  };

  return (
    <div style={panelStyle}>
      <h2 style={headingStyle}>Candidate Schedule</h2>
      <p style={textStyle}>Courses added by the user will appear here.</p>
      <button 
        onClick={openModal}
        style={buttonStyle}
        onMouseEnter={(e) => e.target.style.background = '#1565C0'}
        onMouseLeave={(e) => e.target.style.background = '#1976D2'}
      >
        View Weekly Schedule
      </button>
    </div>
  );
}

export default CandidateSchedule;