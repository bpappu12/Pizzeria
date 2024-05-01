-- Bhargav Pappu
-- CPSC 4620
-- CreateViews.sql

USE Pizzeria;
-- Topping Popularity View
CREATE VIEW ToppingPopularity AS
SELECT
    Toppings.Name AS Topping,
    COUNT(PizzaToppings.ToppingID) AS ToppingCount
FROM
    Toppings
LEFT JOIN
    PizzaToppings ON Toppings.ToppingID = PizzaToppings.ToppingID
GROUP BY
    Toppings.ToppingID, Toppings.Name
ORDER BY
    ToppingCount DESC;

-- ProfitByPizza View
CREATE VIEW ProfitByPizza AS
SELECT
    BasePrices.Size AS Size,
    BasePrices.Crust AS Crust,
    SUM(Pizzas.Price - Pizzas.Cost) AS Profit,
    MIN(DATE_FORMAT(Orders.OrderDate, '%m/%Y')) AS OrderMonth
FROM
    Pizzas
JOIN
    BasePrices ON Pizzas.BasePriceID = BasePrices.BasePriceID
JOIN
    OrderPizzas ON Pizzas.PizzaID = OrderPizzas.PizzaID
JOIN
    Orders ON OrderPizzas.OrderID = Orders.OrderID
GROUP BY
    BasePrices.Size, BasePrices.Crust
ORDER BY
    Profit DESC;

-- ProfitByOrderType View    
CREATE VIEW ProfitByOrderType AS
SELECT 
    COALESCE(OrderType, 'Grand Total') AS OrderType,
    MonthYear,
    Revenue AS TotalOrderPrice,
    Cost AS TotalOrderCost,
    Profit
FROM (
    SELECT 
        O.OrderType,
        DATE_FORMAT(O.OrderDate, '%m/%Y') AS MonthYear,
        SUM(P.Price) AS Revenue,
        SUM(P.Cost) AS Cost,
        SUM(P.Price - P.Cost) AS Profit
    FROM
        Orders O
    JOIN
        OrderPizzas OP ON O.OrderID = OP.OrderID
    JOIN
        Pizzas P ON OP.PizzaID = P.PizzaID
    GROUP BY
        OrderType, MonthYear
) AS Subquery
UNION ALL
SELECT 
    'Grand Total' AS OrderType,
    NULL AS MonthYear,
    SUM(Revenue) AS Revenue,
    SUM(Cost) AS Cost,
    SUM(Profit) AS Profit
FROM (
    SELECT 
        O.OrderType,
        DATE_FORMAT(O.OrderDate, '%m/%Y') AS MonthYear,
        SUM(P.Price - P.Cost) AS Profit,
        SUM(P.Price) AS Revenue,
        SUM(P.Cost) AS Cost
    FROM
        Orders O
    JOIN
        OrderPizzas OP ON O.OrderID = OP.OrderID
    JOIN
        Pizzas P ON OP.PizzaID = P.PizzaID
    GROUP BY
        OrderType, MonthYear
) AS Subquery
ORDER BY
    MonthYear DESC;
