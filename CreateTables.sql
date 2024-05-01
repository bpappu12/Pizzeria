-- Bhargav Pappu
-- CPSC 4620
-- CreateTables.sql

-- Create the Pizzeria database
CREATE DATABASE IF NOT EXISTS Pizzeria;
USE Pizzeria;


CREATE TABLE Customers (
    CustomerID INT AUTO_INCREMENT PRIMARY KEY,
    FirstName VARCHAR(50),
    LastName VARCHAR(50),
    Phone VARCHAR(15)
);

CREATE TABLE BasePrices (
    BasePriceID INT AUTO_INCREMENT PRIMARY KEY,
    Size VARCHAR(20),
    Crust VARCHAR(20),
    Price DECIMAL(10, 2),
    Cost DECIMAL(10, 2)
);

CREATE TABLE Discounts (
    DiscName VARCHAR(50) PRIMARY KEY,
    PercentOff INT,
    DollarsOff DECIMAL(10, 2)
);

CREATE TABLE Orders (
    OrderID INT AUTO_INCREMENT PRIMARY KEY,
    CustomerID INT,
    OrderDate DATE,
    TotalAmount DECIMAL(10, 2),
    OrderType ENUM('Pickup', 'Dine-In', 'Delivery') NOT NULL,
    OrderComplete BOOLEAN NOT NULL DEFAULT FALSE,
    OrderTime DATETIME NOT NULL,
    FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID)
);

CREATE TABLE Pizzas (
    PizzaID INT AUTO_INCREMENT PRIMARY KEY,
    Price DECIMAL(10, 2),
    Cost DECIMAL(10, 2),
    BasePriceID INT, 
    PizzaState VARCHAR(20) NOT NULL DEFAULT "complete",
    FOREIGN KEY (BasePriceID) REFERENCES BasePrices(BasePriceID)
);

CREATE TABLE OrderPizzas (
    OrderPizzaID INT AUTO_INCREMENT PRIMARY KEY,
    OrderID INT,
    PizzaID INT,
    FOREIGN KEY (OrderID) REFERENCES Orders(OrderID),
    FOREIGN KEY (PizzaID) REFERENCES Pizzas(PizzaID)
);

CREATE TABLE OrderDiscounts (
    OrderID INT,
    DiscountName VARCHAR(50),
    PizzaID INT, 
    PRIMARY KEY (OrderID, DiscountName, PizzaID),
    FOREIGN KEY (OrderID) REFERENCES Orders(OrderID),
    FOREIGN KEY (DiscountName) REFERENCES Discounts(DiscName),
    FOREIGN KEY (PizzaID) REFERENCES Pizzas(PizzaID) 
);

CREATE TABLE Toppings (
    ToppingID INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(50),
    PricePerUnit DECIMAL(10, 2),
    CostPerUnit DECIMAL(10, 2),
    Inventory INT,
    InventoryMinimum INT,
    Small DECIMAL(10, 2),
    Medium DECIMAL(10, 2),
    Large DECIMAL(10, 2),
    XLarge DECIMAL(10, 2)
);

CREATE TABLE PizzaToppings (
    PizzaToppingID INT AUTO_INCREMENT PRIMARY KEY,
    PizzaID INT,
    ToppingID INT,
    FOREIGN KEY (PizzaID) REFERENCES Pizzas(PizzaID),
    FOREIGN KEY (ToppingID) REFERENCES Toppings(ToppingID)
);



CREATE TABLE DeliveryOrders (
    OrderID INT PRIMARY KEY, -- Use the same ID as OrderID
    DeliveryAddress VARCHAR(255),
    DeliveryTime DATETIME,
    FOREIGN KEY (OrderID) REFERENCES Orders(OrderID)
);






