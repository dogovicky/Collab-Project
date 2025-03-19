import React, { useState } from 'react';
import './Post.css';

const Post = ({ avatarUrl, username, content, initialComments = [], postTime }) => {
    const [likes, setLikes] = useState(0);
    const [liked, setLiked] = useState(false);
    const [comments, setComments] = useState(initialComments);
    const [reposts, setReposts] = useState(0);
    const [commentText, setCommentText] = useState('');
    const [showComments, setShowComments] = useState(false);

    const handleLike = () => {
        setLikes(prev => prev + (liked ? -1 : 1));
        setLiked(prev => !prev);
    };

    const handleCommentSubmit = (e) => {
        e.preventDefault();
        if (commentText.trim()) {
            const newComment = {
                text: commentText,
                username: 'Current User',
                timestamp: new Date().toLocaleString(),
                avatarUrl: avatarUrl,
                likes: 0,
                liked: false
            };
            setComments(prev => [...prev, newComment]);
            setCommentText('');
        }
    };

    const handleRepost = () => setReposts(prev => prev + 1);

    return (
        <div className="post">
            <div className="post__header">
                <img className="post__avatar" src={avatarUrl} alt={`${username}'s avatar`} />
                <div>
                    <h3>{username}</h3>
                    <span>{postTime}</span>
                </div>
            </div>
            <div className="post__content">
                <p>{content}</p>
            </div>
            <div className="post__actions">
                <button onClick={handleLike}>{liked ? '❤️' : '👍'} Like {likes > 0 && `(${likes})`}</button>
                <button onClick={() => setShowComments(prev => !prev)}>💬 {showComments ? 'Hide' : 'Comment'} ({comments.length})</button>
                <button onClick={handleRepost}>🔄 Repost {reposts > 0 && `(${reposts})`}</button>
            </div>

            {showComments && (
                <div className="post__comments">
                    {comments.map((comment, index) => (
                        <div key={index} className="post__comment">
                            <img src={comment.avatarUrl} alt={`${comment.username}'s avatar`} />
                            <div>
                                <strong>{comment.username}</strong>
                                <span>{comment.timestamp}</span>
                                <p>{comment.text}</p>
                            </div>
                        </div>
                    ))}
                    <form onSubmit={handleCommentSubmit} className="post__comment-form">
                        <textarea
                            value={commentText}
                            onChange={(e) => setCommentText(e.target.value)}
                            placeholder="Write a comment..."
                        />
                        <button type="submit">Post</button>
                    </form>
                </div>
            )}
        </div>
    );
};

export default Post;