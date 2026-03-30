-- Create sequence for user ID
CREATE SEQUENCE user_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Create users table
CREATE TABLE users (
    id NUMBER(19) PRIMARY KEY,
    name VARCHAR2(100) NOT NULL,
    email VARCHAR2(100) NOT NULL UNIQUE,
    phone VARCHAR2(20) NOT NULL,
    address VARCHAR2(500),
    created_at TIMESTAMP DEFAULT SYSDATE NOT NULL,
    updated_at TIMESTAMP DEFAULT SYSDATE NOT NULL
);

-- Create index for email search
CREATE INDEX idx_users_email ON users(email);

-- Create index for created_at for sorting
CREATE INDEX idx_users_created_at ON users(created_at);

-- Sample data (optional)
-- INSERT INTO users (id, name, email, phone, address, created_at, updated_at)
-- VALUES (user_seq.NEXTVAL, 'John Doe', 'john@example.com', '1234567890', '123 Main St', SYSDATE, SYSDATE);
