CREATE SCHEMA IF NOT EXISTS auditorium;
USE auditorium;

CREATE TABLE AUD_Roles (
    id_role INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE AUD_Users (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    id_role INT NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    FOREIGN KEY (id_role) REFERENCES AUD_Roles(id_role)
);

CREATE TABLE AUD_Administrators (
    id_admin INT AUTO_INCREMENT PRIMARY KEY,
    id_user INT NOT NULL UNIQUE,
    f_name VARCHAR(50) NOT NULL,
    m_name VARCHAR(50),
    f_surname VARCHAR(50) NOT NULL,
    m_surname VARCHAR(50),
    identity_card VARCHAR(30) NOT NULL UNIQUE,
    FOREIGN KEY (id_user) REFERENCES AUD_Users(id_user) ON DELETE CASCADE
);

CREATE TABLE AUD_Clients (
    id_client INT AUTO_INCREMENT PRIMARY KEY,
    id_user INT NOT NULL UNIQUE,
    f_name VARCHAR(50) NOT NULL,
    m_name VARCHAR(50),
    f_surname VARCHAR(50) NOT NULL,
    m_surname VARCHAR(50),
    identity_card VARCHAR(30) NOT NULL UNIQUE,
    FOREIGN KEY (id_user) REFERENCES AUD_Users(id_user) ON DELETE CASCADE
);

CREATE TABLE AUD_Contacts (
    id_contact INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(150) NOT NULL,
    contact_value VARCHAR(255) NOT NULL
);

CREATE TABLE AUD_CXA (
    id_cxa INT AUTO_INCREMENT PRIMARY KEY,
    id_admin INT NOT NULL,
    id_contact INT NOT NULL,
    FOREIGN KEY (id_admin) REFERENCES AUD_Administrators(id_admin) ON DELETE CASCADE,
    FOREIGN KEY (id_contact) REFERENCES AUD_Contacts(id_contact) ON DELETE CASCADE
);

CREATE TABLE AUD_CXC (
    id_cxc INT AUTO_INCREMENT PRIMARY KEY,
    id_client INT NOT NULL,
    id_contact INT NOT NULL,
    FOREIGN KEY (id_client) REFERENCES AUD_Clients(id_client) ON DELETE CASCADE,
    FOREIGN KEY (id_contact) REFERENCES AUD_Contacts(id_contact) ON DELETE CASCADE
);

CREATE TABLE AUD_Equipment (
    id_equipment INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    available_quantity INT NOT NULL DEFAULT 0
);

CREATE TABLE AUD_Sections (
    id_section INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);

CREATE TABLE AUD_Reservations (
    id_reservation INT AUTO_INCREMENT PRIMARY KEY,
    id_section INT NOT NULL,
    reservation_date DATE NOT NULL,
    FOREIGN KEY (id_section) REFERENCES AUD_Sections(id_section)
);

CREATE TABLE AUD_RXC (
    id_rxc INT AUTO_INCREMENT PRIMARY KEY,
    id_reservation INT NOT NULL,
    id_client INT NOT NULL,
    FOREIGN KEY (id_reservation) REFERENCES AUD_Reservations(id_reservation) ON DELETE CASCADE,
    FOREIGN KEY (id_client) REFERENCES AUD_Clients(id_client) ON DELETE CASCADE,
    UNIQUE (id_reservation)
);

CREATE TABLE AUD_RXE (
    id_rxe INT AUTO_INCREMENT PRIMARY KEY,
    id_reservation INT NOT NULL,
    id_equipment INT NOT NULL,
    quantity INT NOT NULL,

    FOREIGN KEY (id_reservation) 
        REFERENCES AUD_Reservations(id_reservation) 
        ON DELETE CASCADE,

    FOREIGN KEY (id_equipment) 
        REFERENCES AUD_Equipment(id_equipment) 
        ON DELETE CASCADE,

    UNIQUE (id_reservation, id_equipment)
);

-- Tablas nuevas para reservas temporales
CREATE TABLE AUD_ReservationDrafts (
    id_draft INT AUTO_INCREMENT PRIMARY KEY,
    id_client INT NOT NULL,
    id_section INT NOT NULL,
    reservation_date DATE NOT NULL,
    created_at DATETIME NOT NULL,
    expires_at DATETIME NOT NULL,

    FOREIGN KEY (id_client) REFERENCES AUD_Clients(id_client) ON DELETE CASCADE,
    FOREIGN KEY (id_section) REFERENCES AUD_Sections(id_section) ON DELETE CASCADE
);

CREATE TABLE AUD_RDXE (
    id_rdxe INT AUTO_INCREMENT PRIMARY KEY,
    id_draft INT NOT NULL,
    id_equipment INT NOT NULL,
    quantity INT NOT NULL,

    FOREIGN KEY (id_draft) 
        REFERENCES AUD_ReservationDrafts(id_draft) 
        ON DELETE CASCADE,

    FOREIGN KEY (id_equipment) 
        REFERENCES AUD_Equipment(id_equipment) 
        ON DELETE CASCADE,

    UNIQUE (id_draft, id_equipment)
);

INSERT INTO AUD_Roles (name) VALUES 
('SuperAdministrador'),
('Administrador'),
('Cliente');
