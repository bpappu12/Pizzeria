-- Bhargav Pappu
-- CPSC 4620
-- DropTables.sql

SET FOREIGN_KEY_CHECKS = 0;
USE Pizzeria;

DROP TABLE IF EXISTS Pizzeria.PizzaToppings;
DROP TABLE IF EXISTS Pizzeria.Pizzas;
DROP TABLE IF EXISTS Pizzeria.Toppings;
DROP TABLE IF EXISTS Pizzeria.Orders;
DROP TABLE IF EXISTS Pizzeria.Customers;
DROP TABLE IF EXISTS Pizzeria.Discounts;
DROP TABLE IF EXISTS Pizzeria.BasePrices;
DROP TABLE IF EXISTS Pizzeria.OrderPizzas;
DROP TABLE IF EXISTS Pizzeria.OrderDiscounts;
DROP VIEW IF EXISTS Pizzeria.ProfitByPizza;
DROP VIEW IF EXISTS Pizzeria.ToppingPopularity;
DROP VIEW IF EXISTS Pizzeria.ProfitByOrderType;

DROP SCHEMA IF EXISTS Pizzeria;

SET FOREIGN_KEY_CHECKS = 1;
