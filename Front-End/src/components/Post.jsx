import React from 'react';
import { Avatar, Button } from '@material-ui/core';
import { ThumbUp, Comment, Share, Repeat } from '@material-ui/icons';

const Post = ({ avatarUrl, username, content }) => {
    return (
        <div className="post">
            <div className="post__header">
                <Avatar src={avatarUrl} alt={username} />
                <h3>{username}</h3>
            </div>
            <div className="post__content">
                <p>{content}</p>
            </div>
            <div className="post__actions">
                <Button startIcon={<ThumbUp />}>Like</Button>
                <Button startIcon={<Comment />}>Comment</Button>
                <Button startIcon={<Share />}>Share</Button>
                <Button startIcon={<Repeat />}>Repost</Button>
            </div>
        </div>
    );
};

export default Post;