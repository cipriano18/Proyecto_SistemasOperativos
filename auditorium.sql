CREATE SCHEMA IF NOT EXISTS auditorium;
USE auditorium;

CREATE TABLE AUD_Statuses (
    id_status INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(1) NOT NULL UNIQUE,
    description VARCHAR(25) NOT NULL
);
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
    FOREIGN KEY (id_user) REFERENCES AUD_Users(id_user)
);

CREATE TABLE AUD_ClientTypes (
    id_type INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);

CREATE TABLE AUD_Clients (
    id_client INT AUTO_INCREMENT PRIMARY KEY,
    id_user INT NOT NULL UNIQUE,
    id_type INT NOT NULL,
    f_name VARCHAR(50) NOT NULL,
    m_name VARCHAR(50),
    f_surname VARCHAR(50) NOT NULL,
    m_surname VARCHAR(50),
    identity_card VARCHAR(30) NOT NULL UNIQUE,
    FOREIGN KEY (id_user) REFERENCES AUD_Users(id_user),
    FOREIGN KEY (id_type) REFERENCES AUD_ClientTypes(id_type)
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
    FOREIGN KEY (id_admin) REFERENCES AUD_Administrators(id_admin),
    FOREIGN KEY (id_contact) REFERENCES AUD_Contacts(id_contact)
);

CREATE TABLE AUD_CXC (
    id_cxc INT AUTO_INCREMENT PRIMARY KEY,
    id_client INT NOT NULL,
    id_contact INT NOT NULL,
    FOREIGN KEY (id_client) REFERENCES AUD_Clients(id_client),
    FOREIGN KEY (id_contact) REFERENCES AUD_Contacts(id_contact)
);


INSERT INTO AUD_Roles (name) VALUES 
('SuperAdministrador'), 
('Administrador'), 
('Cliente');

INSERT INTO AUD_ClientTypes (name) VALUES 
('Estudiante'), 
('Profesor'), 
('Administrativo'), 
('Externo');


