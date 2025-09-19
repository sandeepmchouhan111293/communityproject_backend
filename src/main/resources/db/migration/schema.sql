-- USERS
CREATE TABLE users (
    id CHAR(36) PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'MEMBER') NOT NULL DEFAULT 'MEMBER',
    full_name VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    district VARCHAR(255),
    community_name VARCHAR(255),
    phone VARCHAR(255),
    avatar_url VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    last_login DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

-- AUDIT LOGS
CREATE TABLE audit_logs (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36),
    action VARCHAR(255) NOT NULL,
    entity_type VARCHAR(255),
    entity_id CHAR(36),
    old_values TEXT,
    new_values TEXT,
    ip_address VARCHAR(255),
    user_agent TEXT,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- DIRECTORY
CREATE TABLE directory (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) UNIQUE NOT NULL,
    display_name VARCHAR(255),
    contact_info TEXT,
    bio TEXT,
    skills TEXT,
    interests TEXT,
    social_links TEXT,
    is_public BOOLEAN DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- DISCUSSIONS
CREATE TABLE discussions (
    id CHAR(36) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(255),
    created_by CHAR(36) NOT NULL,
    is_pinned BOOLEAN DEFAULT FALSE,
    is_locked BOOLEAN DEFAULT FALSE,
    view_count INT DEFAULT 0,
    reply_count INT DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

-- DISCUSSION REPLIES
CREATE TABLE discussion_replies (
    id CHAR(36) PRIMARY KEY,
    discussion_id CHAR(36) NOT NULL,
    content TEXT NOT NULL,
    created_by CHAR(36) NOT NULL,
    parent_reply_id CHAR(36),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (discussion_id) REFERENCES discussions(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_reply_id) REFERENCES discussion_replies(id) ON DELETE CASCADE
);

-- DOCUMENTS
CREATE TABLE documents (
    id CHAR(36) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    category ENUM('GOVERNANCE', 'RESOURCES', 'MEETINGS', 'GUIDELINES', 'FORMS') NOT NULL,
    access_level ENUM('PUBLIC', 'MEMBER', 'COMMITTEE', 'ADMIN') NOT NULL DEFAULT 'PUBLIC',
    file_type VARCHAR(255),
    file_size VARCHAR(255),
    file_url VARCHAR(255),
    uploaded_by CHAR(36) NOT NULL,
    download_count INT DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (uploaded_by) REFERENCES users(id) ON DELETE CASCADE
);

-- EVENTS
CREATE TABLE events (
    id CHAR(36) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    event_date DATETIME NOT NULL,
    end_date DATETIME,
    location VARCHAR(255),
    max_participants INT,
    current_participants INT DEFAULT 0,
    status ENUM('UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELLED') DEFAULT 'UPCOMING',
    created_by CHAR(36) NOT NULL,
    image_url VARCHAR(255),
    registration_required BOOLEAN DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

-- EVENT REGISTRATIONS
CREATE TABLE event_registrations (
    id CHAR(36) PRIMARY KEY,
    event_id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL,
    status ENUM('REGISTERED', 'CONFIRMED', 'CANCELLED') DEFAULT 'REGISTERED',
    registered_at DATETIME NOT NULL,
    UNIQUE (event_id, user_id),
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- FAMILY MEMBERS
CREATE TABLE family_members (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    relationship VARCHAR(255) NOT NULL,
    age INT,
    gender ENUM('MALE', 'FEMALE', 'OTHER'),
    profession VARCHAR(255),
    date_of_birth DATE,
    school VARCHAR(255),
    hobbies TEXT,
    achievements TEXT,
    marital_status VARCHAR(255),
    spouse_family VARCHAR(255),
    spouse_city VARCHAR(255),
    marriage_year INT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- NOTIFICATIONS
CREATE TABLE notifications (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(255),
    related_entity_id CHAR(36),
    is_read BOOLEAN DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- SETTINGS
CREATE TABLE settings (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36),
    setting_key VARCHAR(255) NOT NULL,
    setting_value TEXT,
    is_global BOOLEAN DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE (user_id, setting_key),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- VOLUNTEER OPPORTUNITIES
CREATE TABLE volunteer_opportunities (
    id CHAR(36) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    requirements TEXT,
    location VARCHAR(255),
    date_time DATETIME,
    duration_hours INT,
    max_volunteers INT,
    current_volunteers INT DEFAULT 0,
    status ENUM('ACTIVE', 'FILLED', 'CANCELLED') DEFAULT 'ACTIVE',
    created_by CHAR(36) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

-- VOLUNTEER REGISTRATIONS
CREATE TABLE volunteer_registrations (
    id CHAR(36) PRIMARY KEY,
    opportunity_id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL,
    status ENUM('REGISTERED', 'CONFIRMED', 'CANCELLED') DEFAULT 'REGISTERED',
    notes TEXT,
    registered_at DATETIME NOT NULL,
    UNIQUE (opportunity_id, user_id),
    FOREIGN KEY (opportunity_id) REFERENCES volunteer_opportunities(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);