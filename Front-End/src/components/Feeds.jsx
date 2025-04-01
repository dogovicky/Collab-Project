import React, { useState } from 'react';
import Post from './Post';
import CreatePost from './CreatePost';

const Feeds = ({ posts }) => {
    const [feedPosts, setFeedPosts] = useState(posts);

    return (
        <div style={{ height: '500px', overflowY: 'scroll', backgroundColor:'#29465B' }}>
            <CreatePost updateFeed={setFeedPosts} />
            {feedPosts.map((post, index) => (
                <Post
                    key={index}
                    avatarUrl={post.avatarUrl || ''}
                    username={post.username || 'Anonymous'}
                    content={post.text || ''}
                    media={post.file || null} // Added support for media files
                    initialComments={post.comments || []}
                    postTime={post.createdAt || ''}
                />
            ))}
        </div>
    );
};

export default Feeds;