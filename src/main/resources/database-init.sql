-- ================================================
--  PostgreSQL Chat App Database Initialization
--  Safely drops existing tables, then recreates all
-- ================================================

-- Optional: Create the database first (run once as postgres)
-- CREATE DATABASE message;

-- Connect to your database
-- \c message;

-- ========== DROP TABLES (in reverse dependency order) ==========
DROP TABLE IF EXISTS unread_messages CASCADE;
DROP TABLE IF EXISTS typing_indicators CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS files CASCADE;
DROP TABLE IF EXISTS user_sessions CASCADE;

-- ========== RECREATE TABLES ==========

-- 1️⃣ User sessions table
CREATE TABLE IF NOT EXISTS user_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255),
    is_online BOOLEAN DEFAULT FALSE,
    last_activity TIMESTAMP DEFAULT NOW()
);

-- 2️⃣ Files table (must exist before messages)
CREATE TABLE IF NOT EXISTS files (
    id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(255),
    file_size BIGINT,
    file_path VARCHAR(500) NOT NULL,
    uploaded_by VARCHAR(255) NOT NULL,
    chat_type VARCHAR(255) NOT NULL,
    recipient_id VARCHAR(255),
    uploaded_at TIMESTAMP DEFAULT NOW()
);

-- 3️⃣ Messages table (depends on files)
CREATE TABLE IF NOT EXISTS messages (
    id BIGSERIAL PRIMARY KEY,
    sender_id VARCHAR(255),
    recipient_id VARCHAR(255),
    content TEXT,
    file_id BIGINT,
    message_type VARCHAR(255) DEFAULT 'TEXT',
    sent_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_file FOREIGN KEY (file_id) REFERENCES files(id) ON DELETE SET NULL
);

-- 4️⃣ Typing indicators table
CREATE TABLE IF NOT EXISTS typing_indicators (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255),
    started_at TIMESTAMP DEFAULT NOW()
);

-- 5️⃣ Unread messages table
CREATE TABLE IF NOT EXISTS unread_messages (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    message_id BIGINT NOT NULL,
    chat_type VARCHAR(255) NOT NULL,
    sender_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_unread_msg FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE
);