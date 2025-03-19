import React, { useState } from 'react';

const SearchBar = ({ onSearch }) => {
    const [query, setQuery] = useState('');
    const [searchType, setSearchType] = useState('community');
    const [error, setError] = useState('');

    const handleInputChange = (e) => {
        setQuery(e.target.value);
        setError(''); // Clear error when user starts typing
    };

    const handleSearchTypeChange = (e) => {
        setSearchType(e.target.value);
    };

    const handleSearch = () => {
        if (!query.trim()) {
            setError('Search query cannot be empty.');
            return;
        }
        onSearch(query, searchType);
    };

    return (
        <div className="search-bar">
            <input
                type="text"
                value={query}
                onChange={handleInputChange}
                placeholder="Search..."
            />
            
            <button onClick={handleSearch}>Search</button>
            {error && <p className="error">{error}</p>}
        </div>
    );
};

export default SearchBar;