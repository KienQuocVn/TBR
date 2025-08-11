Create Database TBR
Go


Use TBR
GO

-- B?ng Users 
CREATE TABLE Users (
    user_id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) NOT NULL UNIQUE,
    password_hash NVARCHAR(255) NOT NULL,
    full_name NVARCHAR(100) NOT NULL,
    role NVARCHAR(50) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NULL
);


-- B?ng Products
CREATE TABLE Products (
    id INT IDENTITY(1,1) PRIMARY KEY,             -- Mã s?n ph?m
    product_code NVARCHAR(50) NOT NULL,              -- Mã l?p l?p
    specification NVARCHAR(50),                      -- Quy cách
    ply_rating INT,                                  -- Ch? s? PR (Ply Rating)
    tread_code NVARCHAR(50),                         -- Mã gai
    type NVARCHAR(10),                               -- Lo?i l?p (TT/TL)
    load_index NVARCHAR(30),                         -- Ch? s? t?i
    layer_count INT,                                 -- S? l?p
    brand NVARCHAR(100)                              -- Th??ng hi?u
);

-- B?ng Machines
CREATE TABLE Machines (
    id INT IDENTITY(1,1) PRIMARY KEY,             -- Mã máy cân
    model NVARCHAR(100),                             -- Model máy cân
    location NVARCHAR(100),                          -- V? trí ??t máy
    installation_date DATE                           -- Ngày l?p ??t
);

-- B?ng WeighRecords
CREATE TABLE WeighRecords (
    id INT IDENTITY(1,1) PRIMARY KEY,             -- Mã b?n ghi cân
    product_id INT NOT NULL,                      -- FK t?i Products
    barcode NVARCHAR(100),                           -- Mã barcode
    max_weight DECIMAL(10,3),                        -- Kh?i l??ng t?i ?a cho phép
    min_weight DECIMAL(10,3),                        -- Kh?i l??ng t?i thi?u cho phép
    actual_weight DECIMAL(10,3),                     -- Kh?i l??ng th?c t?
    deviation DECIMAL(10,3),                         -- Sai l?ch
    result NVARCHAR(10),                             -- K?t qu? cân (OK/NG)
    weigh_date DATETIME,                             -- Ngày gi? cân
    machine_id INT,                               -- FK t?i Machines
    CONSTRAINT FK_WeighRecords_Products FOREIGN KEY (product_id) REFERENCES Products(id),
    CONSTRAINT FK_WeighRecords_Machines FOREIGN KEY (machine_id) REFERENCES Machines(id)
);

