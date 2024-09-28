USE master;
GO
ALTER DATABASE UETIMS SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
GO
DROP DATABASE UETIMS;

-- Create DataBase UETIMS
CREATE DATABASE UETIMS;
USE UETIMS;

-- Create Admin Table
CREATE TABLE Admin (
    AdminId INT PRIMARY KEY IDENTITY(1,1),
    VerificationCode INT,
    Status bit,
    Email VARCHAR(50) UNIQUE, -- Making Email column unique
    Password VARCHAR(8),
    Name VARCHAR(50)
);

-- Create Hostel Table
CREATE TABLE Hostel (
    HostelId INT PRIMARY KEY IDENTITY(1,1),
    Name VARCHAR(50) UNIQUE, -- Making Name column unique
    Location VARCHAR(255),
    NumberOfFloors INT,
    Warden VARCHAR(50)
);
delete from Hostel
-- Create HostelRooms Table
CREATE TABLE HostelRooms (
    HostelId INT,
    TotalRooms INT,
    FOREIGN KEY (HostelId) REFERENCES Hostel(HostelId)
);

-- Create Rooms Table
CREATE TABLE Rooms ( 
    RoomId INT PRIMARY KEY IDENTITY(1,1),
    RoomNo VARCHAR(50) ,
    HostelId INT ,
    FloorNo VARCHAR(255),  
    RoomCategory VARCHAR(50), 
);


-- Create Suppliers Table
CREATE TABLE Suppliers (
    SupplierId INT PRIMARY KEY IDENTITY(1,1),
    Name VARCHAR(50),
    BillNo VARCHAR(50) ,
    DeliveredDate DATE,
);

-- Create SupplierItems Table
CREATE TABLE SupplierItems (
    ItemId INT PRIMARY KEY IDENTITY(1,1),
    SupplierId INT,
    ItemName VARCHAR(50),
    ItemCategory VARCHAR(50),
    ItemPrice DECIMAL(10, 2),  
    FOREIGN KEY (SupplierId) REFERENCES Suppliers(SupplierId)
);

-- Create UnallocatedItems Table
CREATE TABLE InventoryItems (
    ItemId INT PRIMARY KEY IDENTITY(1,1),
    SuppItemId INT,
    SuppId INT,
    ItemName VARCHAR(50),
    ItemCategory VARCHAR(50),
    FOREIGN KEY (SuppId) REFERENCES Suppliers(SupplierId),
    FOREIGN KEY (SuppItemId) REFERENCES SupplierItems(ItemId)
);

-- Create ItemsCategory Table
CREATE TABLE ItemsCategory (
    CategoryId INT PRIMARY KEY IDENTITY(1,1),
    CategoryName VARCHAR(50) UNIQUE -- Making CategoryName column unique
);

-- Create RequestItem Table
CREATE TABLE RequestItem (
    ReqNo INT PRIMARY KEY IDENTITY(1,1),
	 
    RoomNo VARCHAR(50),
    HostelName VARCHAR(50),
    ItemName VARCHAR(50),
    ItemCategory VARCHAR(50),
	Description VARCHAR(255),
    Quantity INT,
    RequestDate DATE,
    Status VARCHAR(50),  
);

-- Create AllocatedItems Table
CREATE TABLE AllocatedItems (
    ItemId INT PRIMARY KEY IDENTITY(1,1),
    HostelId INT,
    RoomId INT,
    ReqNo INT,  -- Add this line to create a column for the foreign key
    ItemName VARCHAR(50),
    ItemCategory VARCHAR(50),
	SuppItemId INT,
    SuppId INT,
    FOREIGN KEY (RoomId) REFERENCES Rooms(RoomId),
    FOREIGN KEY (HostelId) REFERENCES Hostel(HostelId),
    FOREIGN KEY (ReqNo) REFERENCES RequestItem(ReqNo),  -- Add this line to create the foreign key relationship
	 FOREIGN KEY (SuppId) REFERENCES Suppliers(SupplierId),
    FOREIGN KEY (SuppItemId) REFERENCES SupplierItems(ItemId)	
);

-- Insert sample data into ItemsCategory table

INSERT INTO ItemsCategory (CategoryName)
VALUES ('Electronics');
INSERT INTO ItemsCategory (CategoryName)
VALUES ('Furniture');
INSERT INTO ItemsCategory (CategoryName)
VALUES ('Hardware');
INSERT INTO ItemsCategory (CategoryName)
VALUES ('Sanitory');
