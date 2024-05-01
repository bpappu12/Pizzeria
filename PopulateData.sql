-- Bhargav Pappu
-- CPSC 4620
-- PopulateData.sql

USE Pizzeria;

DROP PROCEDURE IF EXISTS `ADD_CUSTOMER`;
DROP PROCEDURE IF EXISTS `UPDATE_CUSTOMER_ADDRESS`;
DROP PROCEDURE IF EXISTS `ADD_PIZZA`;

DELIMITER //

CREATE PROCEDURE `ADD_CUSTOMER`(
	IN FIRST_NAME VARCHAR(50),
	IN LAST_NAME VARCHAR(50),
	IN PHONE_NUMBER VARCHAR(15),
	IN ADDRESS VARCHAR(255)
)
BEGIN
	INSERT INTO Customers
	(FirstName, LastName, Phone)
	VALUES
		(FIRST_NAME, LAST_NAME, PHONE_NUMBER, ADDRESS);
END
//

CREATE PROCEDURE `UPDATE_CUSTOMER_ADDRESS`(
	IN PHONE_NUMBER VARCHAR(15),
	IN ADDRESS VARCHAR(255)
)
BEGIN
	UPDATE Customers
	SET Address = ADDRESS
	WHERE Phone = PHONE_NUMBER;
END
//

CREATE PROCEDURE `ADD_PIZZA`(
	IN ORDER_ID INT,
	IN CRUST VARCHAR(20),
	IN SIZE VARCHAR(20)
)
BEGIN
	INSERT INTO Pizzas
		(Price, Cost, BasePriceID, PizzaState)
		VALUES
			(0, 0, (SELECT BasePriceID FROM BasePrices WHERE Size = SIZE AND Crust = CRUST), FALSE);
END
//

DELIMITER ;



-- Insert Toppings Data into Toppings Table
INSERT INTO Toppings (Name, PricePerUnit, CostPerUnit, Inventory, InventoryMinimum, Small, Medium, Large, XLarge)
VALUES
    ('Pepperoni', 1.25, 0.2, 100, 50, 2, 2.75, 3.5, 4.5),
    ('Sausage', 1.25, 0.15, 100, 50, 2.5, 3, 3.5, 4.25),
    ('Ham', 1.5, 0.15, 78, 25, 2, 2.5, 3.25, 4),
    ('Chicken', 1.75, 0.25, 56, 25, 1.5, 2, 2.25, 3),
    ('Green Pepper', 0.5, 0.02, 79, 25, 1, 1.5, 2, 2.5),
    ('Onion', 0.5, 0.02, 85, 25, 1, 1.5, 2, 2.75),
    ('Roma Tomato', 0.75, 0.03, 86, 10, 2, 3, 3.5, 4.5),
    ('Mushrooms', 0.75, 0.1, 52, 50, 1.5, 2, 2.5, 3),
    ('Black Olives', 0.6, 0.1, 39, 25, 0.75, 1, 1.5, 2),
    ('Pineapple', 1, 0.25, 15, 0, 1, 1.25, 1.75, 2),
    ('Jalapenos', 0.5, 0.05, 64, 0, 0.5, 0.75, 1.25, 1.75),
    ('Banana Peppers', 0.5, 0.05, 36, 0, 0.6, 1, 1.3, 1.75),
    ('Regular Cheese', 0.5, 0.12, 250, 50, 2, 3.5, 5, 7),
    ('Four Cheese Blend', 1, 0.15, 150, 25, 2, 3.5, 5, 7),
    ('Feta Cheese', 1.5, 0.18, 75, 0, 1.75, 3, 4, 5.5),
    ('Goat Cheese', 1.5, 0.2, 54, 0, 1.6, 2.75, 4, 5.5),
    ('Bacon', 1.5, 0.25, 89, 0, 1, 1.5, 2, 3);
    
-- Insert Discount Data into Discoutns Table
INSERT INTO Discounts (DiscName, PercentOff, DollarsOff)
VALUES
	('Employee', 15, NULL),
    ('Lunch Special Medium', NULL, 1.00),
    ('Lunch Special Large', NULL, 2.00),
    ('Specialty Pizza', NULL, 1.50),
    ('Happy Hour', 10, NULL),
    ('Gameday Special', 20, NULL);
    
-- Insert BasePrices data into BasePrice Table
INSERT INTO BasePrices (Size, Crust, Price, Cost)
VALUES
	('Small', 'Thin', 3, 0.5),
    ('Small', 'Original', 3, 0.75),
    ('Small', 'Pan', 3.5, 1),
    ('Small', 'Gluten-Free', 4, 2),
    ('Medium', 'Thin', 5, 1),
    ('Medium', 'Original', 5, 1.5),
    ('Medium', 'Pan', 6, 2.25),
    ('Medium', 'Gluten-Free', 6.25, 3),
    ('Large', 'Thin', 8, 1.25),
    ('Large', 'Original', 8, 2),
    ('Large', 'Pan', 9, 3),
    ('Large', 'Gluten-Free', 9.5, 4),
    ('XLarge', 'Thin', 10, 2),
    ('XLarge', 'Original', 10, 3),
    ('XLarge', 'Pan', 11.5, 4.5),
    ('XLarge', 'Gluten-Free', 12.5, 6);
-- First Order
INSERT INTO Customers(FirstName, lastName, Phone)
VALUES
	(NULL, NULL, NULL);
INSERT INTO Orders (CustomerID, OrderDate, TotalAmount, OrderType, OrderComplete, OrderTime)
VALUES
    (1, '2023-03-05', 20.75, 'Dine-In', True, '2023-03-05 12:03:00');
INSERT INTO Pizzas (Price, Cost, BasePriceID)
VALUES
    ( 20.75, 3.68 ,9);
INSERT INTO OrderPizzas (OrderID, PizzaID)
VALUES
    (1, 1);
INSERT INTO PizzaToppings (PizzaID, ToppingID)
VALUES
	(1, 13),
    (1, 13),
    (1, 1),
    (1, 2);
INSERT INTO OrderDiscounts (OrderID, DiscountName, PizzaID)
VALUES
	(1, 'Lunch Special Large' , 1);
-- Second Order
INSERT INTO Customers(FirstName, lastName, Phone)
VALUES
	(NULL, NULL, NULL);
INSERT INTO Orders (CustomerID, OrderDate, TotalAmount, OrderType, OrderComplete, OrderTime)
VALUES
    (2, '2023-04-03', 19.78, 'Dine-In', True, '2023-04-03 12:05:00');
INSERT INTO Pizzas (Price, Cost, BasePriceID)
VALUES
    (12.85, 3.23 ,7),
    (6.93, 1.40, 2);
INSERT INTO OrderPizzas (OrderID, PizzaID)
VALUES
    (2, 2),
    (2, 3);
INSERT INTO PizzaToppings (PizzaID, ToppingID)
VALUES
    (2, 15),
    (2, 9),
    (2, 7),
    (2, 8),
    (2, 12),
    (3, 13),
    (3, 4),
    (3, 12);
INSERT INTO OrderDiscounts (OrderID, DiscountName, PizzaID)
VALUES
	(2, 'Lunch Special Medium' , 2),
    (2, 'Specialty Pizza', 2);
--  Third Order
INSERT INTO Customers(FirstName, lastName, Phone)
VALUES
	('Andrew', 'Wilkes-Krier', 8642545861);
INSERT INTO Orders (CustomerID, OrderDate, TotalAmount, OrderType, OrderComplete, OrderTime)
VALUES
    (3, '2023-03-03', 89.28, 'Pickup', True, '2022-03-03 21:30:00');
INSERT INTO Pizzas (Price, Cost, BasePriceID)
VALUES
    (14.88, 3.30 , 10),
    (14.88, 3.30 , 10),
    (14.88, 3.30 , 10),
    (14.88, 3.30 , 10),
    (14.88, 3.30 , 10),
    (14.88, 3.30 , 10);
INSERT INTO OrderPizzas (OrderID, PizzaID)
VALUES
    (3, 4),
    (3, 5),
    (3, 6),
    (3, 7),
    (3, 8),
    (3, 9);
INSERT INTO PizzaToppings (PizzaID, ToppingID)
VALUES
    (4, 13),
    (4, 1),
    (5, 13),
    (5, 1),
    (6, 13),
    (6, 1),
    (7, 13),
    (7, 1),
    (8, 13),
    (8, 1),
    (9, 13),
    (9, 1);
-- Fourth Order
INSERT INTO Orders (CustomerID, OrderDate, TotalAmount, OrderType, OrderComplete, OrderTime)
VALUES
    (3, '2023-04-20', 86.19, 'Delivery', True, '2023-04-20 19:11:00');
INSERT INTO Pizzas (Price, Cost, BasePriceID)
VALUES
    (27.94, 9.19, 14),
    (31.50, 6.25, 14),
    (26.75, 8.18, 14);
INSERT INTO OrderPizzas (OrderID, PizzaID)
VALUES
    (4, 10),
    (4, 11),
    (4, 12);
INSERT INTO PizzaToppings (PizzaID, ToppingID)
VALUES
    (10, 14),
    (11, 14),
    (12, 14),
    (10, 1),
    (10, 2),
    (11, 3),
    (11, 3),
    (11, 10),
    (11, 10),
    (12, 4),
    (12, 17);
INSERT INTO OrderDiscounts (OrderID, DiscountName, PizzaID)
VALUES
	(4, 'Gameday Special', 10),
    (4, 'Gameday Special', 11),
	(4, 'Gameday Special', 12),
    (4, 'Specialty Pizza', 11);
-- Fifth Order 
INSERT INTO Customers(FirstName, lastName, Phone)
VALUES
	('Matt', 'Engers', 8644749953);
INSERT INTO Orders (CustomerID, OrderDate, TotalAmount, OrderType, OrderComplete, OrderTime)
VALUES
    (4, '2023-03-02', 27.45, 'Pickup', True, '2023-03-02 17:30:00');
INSERT INTO Pizzas (Price, Cost, BasePriceID)
VALUES
    (27.45, 7.88, 16);
INSERT INTO OrderPizzas (OrderID, PizzaID)
VALUES
    (5, 13);
INSERT INTO PizzaToppings (PizzaID, ToppingID)
VALUES
    (13, 5),
    (13, 6),
    (13, 7),
    (13, 8),
    (13, 9),
    (13, 16);
INSERT INTO OrderDiscounts (OrderID, DiscountName, PizzaID)
VALUES
    (5, 'Specialty Pizza', 13);
-- Sixth Order
INSERT INTO Customers(FirstName, lastName, Phone)
VALUES
	('Frank', 'Turner', 8642328944);
INSERT INTO Orders (CustomerID, OrderDate, TotalAmount, OrderType, OrderComplete, OrderTime)
VALUES
    (5, '2023-03-02', 25.81, 'Delivery', True, '2023-03-02 18:17:00');
INSERT INTO Pizzas (Price, Cost, BasePriceID)
VALUES
    (25.81, 4.24, 9);
INSERT INTO OrderPizzas (OrderID, PizzaID)
VALUES
    (6, 14);
INSERT INTO PizzaToppings (PizzaID, ToppingID)
VALUES
    (14, 14),
    (14, 14),
    (14, 4),
    (14, 5),
    (14, 6),
    (14, 8);
-- Seventh Order
INSERT INTO Customers(FirstName, lastName, Phone)
VALUES
	('Milo', 'Auckerman', 8648785679);
INSERT INTO Orders (CustomerID, OrderDate, TotalAmount, OrderType, OrderComplete, OrderTime)
VALUES
    (6, '2023-04-13', 37.25, 'Delivery', True, '2023-04-13 20:32:00');
INSERT INTO Pizzas (Price, Cost, BasePriceID)
VALUES
    (18.00, 2.75, 9),
    (19.25, 3.25, 9);
INSERT INTO OrderPizzas (OrderID, PizzaID)
VALUES
    (7, 15),
    (7, 16);
INSERT INTO PizzaToppings (PizzaID, ToppingID)
VALUES
    (15, 14),
    (16, 13),
    (15, 14),
    (16, 1),
    (16, 1);
INSERT INTO OrderDiscounts (OrderID, DiscountName, PizzaID)
VALUES
    (7, 'Employee', 15),
    (7, 'Employee', 16);