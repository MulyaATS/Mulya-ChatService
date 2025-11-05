-- PostgreSQL Database Initialization Script
-- Run this script to create the database and tables

-- Create database (run as postgres user)
CREATE DATABASE message;

-- Connect to message database and create tables
\c message;

-- User sessions table
CREATE TABLE IF NOT EXISTS user_sessions (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(50),
    is_online BOOLEAN DEFAULT FALSE,
    last_activity TIMESTAMP
);

-- Messages table
CREATE TABLE IF NOT EXISTS messages (
    id SERIAL PRIMARY KEY,
    sender_id VARCHAR(50),
    recipient_id VARCHAR(50),
    content TEXT,
    file_name VARCHAR(255),
    file_type VARCHAR(100),
    file_size BIGINT,
    file_data BYTEA,
    message_type VARCHAR(20) DEFAULT 'TEXT',
    sent_at TIMESTAMP
);

-- Typing indicators table
CREATE TABLE IF NOT EXISTS typing_indicators (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(50),
    started_at TIMESTAMP
);