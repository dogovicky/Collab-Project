import React from 'react';
import Post from './Post';

const Feeds = ({ posts }) => {
    return (
        <div style={{ height: '500px', overflowY: 'scroll' }}>
            {posts.map((post, index) => (
                <Post key={index} post={post} />
            ))}
        </div>
    );
};

export default Feeds;