import React, { useState } from 'react';

const SearchBar = ({ onSearch }) => {
    const [query, setQuery] = useState('');
    const [searchType, setSearchType] = useState('community');

    const handleInputChange = (e) => {
        setQuery(e.target.value);
    };

    const handleSearchTypeChange = (e) => {
        setSearchType(e.target.value);
    };

    const handleSearch = () => {
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
            <select value={searchType} onChange={handleSearchTypeChange}>
                <option value="community">Community</option>
                <option value="post">Post</option>
                <option value="username">Username</option>
            </select>
            <button onClick={handleSearch}>Search</button>
        </div>
    );
};

export default SearchBar;