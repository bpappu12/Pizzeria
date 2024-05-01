
SELECT
    BasePrices.Size AS Size,
    BasePrices.Crust AS Crust,
    SUM(Pizzas.Price - Pizzas.Cost) AS Profit,
    DATE_FORMAT(Orders.OrderDate, '%m/%Y') AS OrderMonth
FROM
    Orders
JOIN
    OrderPizzas ON Orders.OrderID = OrderPizzas.OrderID
JOIN
    Pizzas ON OrderPizzas.PizzaID = Pizzas.PizzaID
JOIN
    BasePrices ON Pizzas.BasePriceID = BasePrices.BasePriceID
GROUP BY
    BasePrices.Size, BasePrices.Crust, OrderMonth
ORDER BY
    Profit DESC;
