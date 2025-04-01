import React from 'react';

const Header = () => {
  return (
    <header style={styles.header}>
      <div style={styles.logo}>Nexus</div>
    </header>
  );
};

const styles = {
  header: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    backgroundColor: '#000',
    color: '#E7E9EA',
    padding: '12px 24px',
    borderBottom: '1px solid #333639',
    position: 'sticky',
    top: 0,
    zIndex: 1000,
  },
  logo: {
    fontSize: '24px',
    fontWeight: '700',
    background: 'linear-gradient(90deg, #1a8cd8, #2dc428)',
    WebkitBackgroundClip: 'text',
    color: 'transparent',
    cursor: 'pointer',
    textShadow: '0 0 8px rgba(26, 140, 216, 0.5)',
  },
};

export default Header;

