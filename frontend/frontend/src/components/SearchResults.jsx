import React from 'react';

function SearchResults() {
  const panelStyle = {
    maxWidth: '800px',
    margin: '0 auto',
    padding: '1.25rem',
    background: '#FFFFFF',
    border: '1px solid #E5E7EB',
    borderRadius: '12px',
    width: '100%',
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

  return (
    <div style={panelStyle}>
      <h2 style={headingStyle}>Search Results</h2>
      <p style={textStyle}>No results yet. Results from backend will appear here.</p>
    </div>
  );
}

export default SearchResults;