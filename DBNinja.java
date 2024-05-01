package cpsc4620;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



/*
 * This file is where most of your code changes will occur You will write the code to retrieve
 * information from the database, or save information to the database
 * 
 * The class has several hard coded static variables used for the connection, you will need to
 * change those to your connection information
 * 
 * This class also has static string variables for pickup, delivery and dine-in. If your database
 * stores the strings differently (i.e "pick-up" vs "pickup") changing these static variables will
 * ensure that the comparison is checking for the right string in other places in the program. You
 * will also need to use these strings if you store this as boolean fields or an integer.
 * 
 * 
 */

/**
 * A utility class to help add and retrieve information from the database
 */

public final class DBNinja {
	private static Connection conn;

	// Change these variables to however you record dine-in, pick-up and delivery, and sizes and crusts
	public final static String pickup = "pickup";
	public final static String delivery = "delivery";
	public final static String dine_in = "dinein";

	public final static String size_s = "Small";
	public final static String size_m = "Medium";
	public final static String size_l = "Large";
	public final static String size_xl = "XLarge";

	public final static String crust_thin = "Thin";
	public final static String crust_orig = "Original";
	public final static String crust_pan = "Pan";
	public final static String crust_gf = "Gluten-Free";



	
	private static boolean connect_to_db() throws SQLException, IOException {

		try {
			conn = DBConnector.make_connection();
			return true;
		} catch (SQLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

	}

	public static void updatePizzaCostAndPrice(int pizzaID) throws SQLException, IOException {
		connect_to_db();

		String updatePizzaCostAndPriceQuery = "UPDATE Pizzas P " +
				"JOIN BasePrices BP ON P.BasePriceID = BP.BasePriceID " +
				"SET P.Cost = BP.Cost + " +
				"(SELECT SUM(T.CostPerUnit * " +
				"CASE BP.Size " +
				"    WHEN 'Small' THEN T.Small " +
				"    WHEN 'Medium' THEN T.Medium " +
				"    WHEN 'Large' THEN T.Large " +
				"    WHEN 'XLarge' THEN T.XLarge " +
				"END) " +
				"FROM PizzaToppings PT " +
				"JOIN Toppings T ON PT.ToppingID = T.ToppingID " +
				"WHERE PT.PizzaID = ?), " +
				"P.Price = BP.Price + " +
				"(SELECT SUM(T.PricePerUnit * " +
				"CASE BP.Size " +
				"    WHEN 'Small' THEN T.Small " +
				"    WHEN 'Medium' THEN T.Medium " +
				"    WHEN 'Large' THEN T.Large " +
				"    WHEN 'XLarge' THEN T.XLarge " +
				"END) " +
				"FROM PizzaToppings PT " +
				"JOIN Toppings T ON PT.ToppingID = T.ToppingID " +
				"WHERE PT.PizzaID = ?) " +
				"WHERE P.PizzaID = ?";

		try (PreparedStatement ps = conn.prepareStatement(updatePizzaCostAndPriceQuery)) {
			ps.setInt(1, pizzaID);
			ps.setInt(2, pizzaID);
			ps.setInt(3, pizzaID);
			ps.executeUpdate();
		} finally {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		}
	}




	public static void markOrderAsComplete() throws SQLException, IOException {
		try {
			connect_to_db(); // Ensure you have a valid connection
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Which Order would you like to complete? Enter the OrderID");
			displayOpenOrders();
			int orderIdToComplete = Integer.parseInt(reader.readLine());
			updateOrderStatus(orderIdToComplete, "complete");

		} finally {
			if (conn != null && !conn.isClosed()) {
				conn.close(); // Close the connection in a finally block to ensure it's closed even if an exception occurs
			}
		}
	}

	private static void displayOpenOrders() throws SQLException {
		String query = "SELECT OrderID, OrderComplete FROM Orders WHERE OrderComplete = 0";
		try (Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {

			System.out.println("Open Orders:");
			while (rs.next()) {
				int orderId = rs.getInt("OrderID");
				String orderStatus = rs.getString("OrderComplete");
				System.out.println("OrderID: " + orderId + ", Status: " + orderStatus);
			}
		}
	}

	private static void updateOrderStatus(int orderId, String newStatus) throws SQLException {
		String updateQuery = "UPDATE Orders SET OrderComplete = ? WHERE OrderID = ?";
		try (PreparedStatement ps = conn.prepareStatement(updateQuery)) {
			ps.setInt(1, 1);
			ps.setInt(2, orderId);
			int rowsUpdated = ps.executeUpdate();

			if (rowsUpdated > 0) {
				System.out.println("Order marked as complete successfully.");
			} else {
				System.out.println("Failed to mark the order as complete. OrderID may be invalid.");
			}
		}
	}

	public static void addToppingInventory(int toppingID, int quantity) throws SQLException, IOException {
		connect_to_db();

		String updateInventoryQuery = "UPDATE Toppings SET Inventory = Inventory + ? WHERE ToppingID = ?";

		try (PreparedStatement ps = conn.prepareStatement(updateInventoryQuery)) {
			ps.setInt(1, quantity);
			ps.setInt(2, toppingID);
			ps.executeUpdate();
		} finally {
			conn.close();
		}
	}

	public static int getNextOrderID() throws SQLException, IOException {
		connect_to_db();

		String getMaxOrderIDQuery = "SELECT MAX(OrderID) AS MaxOrderID FROM Orders";

		try (Statement statement = conn.createStatement()) {
			ResultSet resultSet = statement.executeQuery(getMaxOrderIDQuery);

			int maxOrderID = 0;
			if (resultSet.next()) {
				maxOrderID = resultSet.getInt("MaxOrderID");
			}

			return maxOrderID + 1;
		} finally {
			conn.close();
		}
	}

	public static void addOrder(Order o) throws SQLException, IOException 
	{
		connect_to_db();
		/*
		 * add code to add the order to the DB. Remember that we're not just
		 * adding the order to the order DB table, but we're also recording
		 * the necessary data for the delivery, dinein, and pickup tables
		 * 
		 */
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String dateTimeString = now.format(formatter);
		String dateString = now.toLocalDate().toString();
		String timeString = now.toLocalTime().toString();

		int cID = o.getCustID();
		System.out.println("Entered " + cID);
		String addedOrder = "INSERT INTO Orders(OrderID, CustomerID, OrderDate, TotalAmount, OrderType, OrderComplete, OrderTime)"
				+ " VALUES" + "(?,?,?,?,?,?,?)";
		try (PreparedStatement ps = conn.prepareStatement(addedOrder)) {
			ps.setInt(1, 0);
			ps.setInt(2, cID);
			ps.setString(3, dateString);
			ps.setDouble(4, 0);
			ps.setString(5, o.getOrderType());
			ps.setInt(6, 0);
			ps.setString(7, dateTimeString);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			System.out.println(e);
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
	}
	public static void linkOrderToPizza(int orderID, int pizzaID) throws SQLException, IOException {
		connect_to_db();

		String linkOrderToPizzaQuery = "INSERT INTO OrderPizzas (OrderID, PizzaID) VALUES (?, ?)";

		try (PreparedStatement ps = conn.prepareStatement(linkOrderToPizzaQuery)) {
			ps.setInt(1, orderID);
			ps.setInt(2, pizzaID);
			ps.executeUpdate();
		} finally {
			conn.close();
		}
		updateOrderTotalAmount(getNextOrderID()-1);
	}

	public static void insertDeliveryInformation(int orderId, String deliveryAddress) throws SQLException, IOException {
		connect_to_db();

		LocalDateTime deliveryTime = LocalDateTime.now();
		String insertDeliveryQuery = "INSERT INTO DeliveryOrders (OrderID, DeliveryAddress, DeliveryTime) VALUES (?, ?, ?)";

		try (PreparedStatement ps = conn.prepareStatement(insertDeliveryQuery)) {
			ps.setInt(1, orderId);
			ps.setString(2, deliveryAddress);
			ps.setObject(3, Timestamp.valueOf(deliveryTime));

			ps.executeUpdate();
		} finally {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		}
	}




	public static void addPizza(Pizza p) throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Add the code needed to insert the pizza into into the database.
		 * Keep in mind adding pizza discounts and toppings associated with the pizza,
		 * there are other methods below that may help with that process.
		 * 
		 */
		String addPizzaQuery = "INSERT INTO Pizzas(Price, Cost, BasePriceID, PizzaState) VALUES (?, ?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(addPizzaQuery, Statement.RETURN_GENERATED_KEYS)) {
			ps.setDouble(1, p.getCustPrice());
			ps.setDouble(2, p.getBusPrice());
			ps.setInt(3, p.getBasePriceID());
			// Set PizzaState
			ps.setString(4, p.getPizzaState());

			ps.executeUpdate();

			// Retrieve the generated PizzaID
			try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					int pizzaID = generatedKeys.getInt(1);
					p.setPizzaID(pizzaID);
				} else {
					throw new SQLException("Failed to retrieve generated PizzaID");
				}
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
		conn.close();
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static int getBasePriceID(String size, String crustType) throws SQLException, IOException {
		connect_to_db();

		String query = "SELECT BasePriceID FROM BasePrices WHERE Size = ? AND Crust = ?";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, size);
			ps.setString(2, crustType);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("BasePriceID");
				} else {
					// Handle the case where no matching record is found
					return -1; // or throw an exception, depending on your error handling strategy
				}
			}
		} finally {
			conn.close();
		}
	}

	public static int getMaxPizzaID() throws SQLException, IOException {
		connect_to_db();

		String getMaxPizzaIDQuery = "SELECT MAX(PizzaID) AS MaxPizzaID FROM Pizzas";

		try (PreparedStatement ps = conn.prepareStatement(getMaxPizzaIDQuery);
			 ResultSet rs = ps.executeQuery()) {
			if (rs.next()) {
				return rs.getInt("MaxPizzaID");
			} else {
				// If no pizzas in the table, return a default value (e.g., 0 or -1)
				return -1;
			}
		} finally {
			conn.close();
		}
	}

	public static void addToppings(Pizza p, int pizzaID) throws SQLException, IOException {
		connect_to_db();

		String insertPizzaToppingQuery = "INSERT INTO PizzaToppings (PizzaID, ToppingID) VALUES (?, ?)";

		try (PreparedStatement ps = conn.prepareStatement(insertPizzaToppingQuery)) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

			int toppingID;
			while (true) {
				System.out.println("Enter the ToppingID to add to the pizza (or -1 to finish):");
				try {
					toppingID = Integer.parseInt(reader.readLine());
					if (toppingID == -1) {
						break;
					}

					ps.setInt(1, pizzaID);
					ps.setInt(2, toppingID);
					ps.executeUpdate();
					Topping t = getToppingById(toppingID);
					useTopping(p, t, false);
				} catch (NumberFormatException e) {
					System.out.println("Invalid input. Please enter a valid ToppingID or -1 to finish.");
				}
			}
		} finally {
			conn.close();
		}
	}



	public static void useTopping(Pizza p, Topping t, boolean isDoubled) throws SQLException, IOException {
		try {
			connect_to_db(); // Ensure you have a valid connection

			int newInventory = t.getCurINVT() - (isDoubled ? 2 : 1);
			System.out.println("Inventory currently:" + t.getCurINVT());

			String updateInventoryQuery = "UPDATE Toppings SET Inventory = ? WHERE ToppingID = ?";
			try (PreparedStatement ps = conn.prepareStatement(updateInventoryQuery)) {
				ps.setInt(1, newInventory);
				ps.setInt(2, t.getTopID());
				ps.executeUpdate();
			}

		} finally {
			if (conn != null && !conn.isClosed()) {
				conn.close(); // Close the connection in a finally block to ensure it's closed even if an exception occurs
			}
		}
	}

	public static Topping getToppingById(int toppingID) throws SQLException, IOException {
		connect_to_db();

		Topping topping = null;
		String selectToppingQuery = "SELECT * FROM Toppings WHERE ToppingID = ?";

		try (PreparedStatement ps = conn.prepareStatement(selectToppingQuery)) {
			ps.setInt(1, toppingID);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					// Extract data from the result set
					int topID = rs.getInt("ToppingID");
					String topName = rs.getString("Name");
					int inventory = rs.getInt("Inventory");
					int inventoryMinimum = rs.getInt("InventoryMinimum");
					double small = rs.getDouble("Small");
					double costPerUnit = rs.getDouble("CostPerUnit");
					double pricePerUnit = rs.getDouble("PricePerUnit");
					double medium = rs.getDouble("Medium");
					double large = rs.getDouble("Large");
					double xLarge = rs.getDouble("XLarge");

// Create a Topping object
					topping = new Topping(topID, topName, small, medium, large, xLarge, pricePerUnit, costPerUnit, inventoryMinimum, inventory);
				}
			}
		} finally {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		}

		return topping;
	}





	public static void usePizzaDiscount(Pizza p, Discount d) throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * This method connects a discount with a Pizza in the database.
		 * 
		 * What that means will be specific to your implementatinon.
		 */
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}


	public static void printProfitByPizzaReport() throws SQLException, IOException {
		connect_to_db();
		String query = "SELECT Size, Crust, Profit, OrderMonth FROM ProfitByPizza";

		try (Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {

			System.out.println("Profit By Pizza Report:");
			while (rs.next()) {
				String size = rs.getString("Size");
				String crust = rs.getString("Crust");
				double profit = rs.getDouble("Profit");
				String orderMonth = rs.getString("OrderMonth");
				System.out.println("Size: " + size + ", Crust: " + crust + ", Profit: " + profit + ", Order Month: " + orderMonth);
			}
		} finally {
			conn.close();
		}
	}

	public static void printProfitByOrderTypeReport() throws SQLException, IOException {
		connect_to_db();
		String query = "SELECT OrderType, MonthYear, TotalOrderPrice, TotalOrderCost, Profit FROM ProfitByOrderType";

		try (Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {

			System.out.println("Profit By Order Type Report:");
			while (rs.next()) {
				String orderType = rs.getString("OrderType");
				String monthYear = rs.getString("MonthYear");
				double totalOrderPrice = rs.getDouble("TotalOrderPrice");
				double totalOrderCost = rs.getDouble("TotalOrderCost");
				double profit = rs.getDouble("Profit");

				System.out.println("Order Type: " + orderType + ", Month Year: " + monthYear +
						", Total Order Price: " + totalOrderPrice + ", Total Order Cost: " + totalOrderCost +
						", Profit: " + profit);
			}
		} finally {
			conn.close();
		}
	}


	public static void useOrderDiscount(Order o, Discount d) throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * This method connects a discount with an order in the database
		 * 
		 * You might use this, you might not depending on where / how to want to update
		 * this information in the dabast
		 */
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	
	public static void addCustomer(Customer c) throws SQLException, IOException {
		connect_to_db();
		/*
		 * This method adds a new customer to the database.
		 * 
		 */
		String addcust = "INSERT INTO Customers(CustomerID, FirstName, LastName, Phone)" + "VALUES" + "(?, ?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(addcust)) {
			ps.setInt(1, c.getCustID());
			ps.setString(2, c.getLName());
			ps.setString(3, c.getFName());
			ps.setString(4, c.getPhone());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			System.out.println(e);
		}
		conn.close();
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static void completeOrder(Order o) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Find the specifed order in the database and mark that order as complete in the database.
		 * 
		 */
		


		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}


	public static ArrayList<Order> getOrders(boolean openOnly) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Return an arraylist of all of the orders.
		 * 	openOnly == true => only return a list of open (ie orders that have not been marked as completed)
		 *           == false => return a list of all the orders in the database
		 * Remember that in Java, we account for supertypes and subtypes
		 * which means that when we create an arrayList of orders, that really
		 * means we have an arrayList of dineinOrders, deliveryOrders, and pickupOrders.
		 * 
		 * Don't forget to order the data coming from the database appropriately.
		 * 
		 */



		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return null;
	}
	
	public static Order getLastOrder(){
		/*
		 * Query the database for the LAST order added
		 * then return an Order object for that order.
		 * NOTE...there should ALWAYS be a "last order"!
		 */
		




		 return null;
	}

	public static ArrayList<Order> getOrdersByDate(String date){
		/*
		 * Query the database for ALL the orders placed on a specific date
		 * and return a list of those orders.
		 *  
		 */
		




		 return null;
	}
		
	public static ArrayList<Discount> getDiscountList() throws SQLException, IOException {
		connect_to_db();
		/* 
		 * Query the database for all the available discounts and 
		 * return them in an arrayList of discounts.
		 * 
		*/
		
		
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return null;
	}

	public static Discount findDiscountByName(String name){
		/*
		 * Query the database for a discount using it's name.
		 * If found, then return an OrderDiscount object for the discount.
		 * If it's not found....then return null
		 *  
		 */




		 return null;
	}


	public static ArrayList<Customer> getCustomerList() throws SQLException, IOException {
		/*
		 * Query the data for all the customers and return an arrayList of all the customers. 
		 * Don't forget to order the data coming from the database appropriately.
		 * 
		*/
		ArrayList<Customer> customers = new ArrayList<Customer>();
		connect_to_db();
		ResultSet rs = null;
		String viewCustomers = "SELECT * FROM Customers";
		try (PreparedStatement ps = conn.prepareStatement(viewCustomers)) {
			rs = ps.executeQuery();

			while (rs.next()) {
				int customer_id = rs.getInt("CustomerID");
				String Fname = rs.getString("FirstName");
				String Lname = rs.getString("LastName");
				long phone = rs.getLong("Phone");
				String ph = String.valueOf(phone);
				Customer c = new Customer(customer_id, Fname, Lname, ph);
				customers.add(c);
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
		conn.close();

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return customers;
	}

	public static Customer findCustomerByPhone(String phoneNumber){
		/*
		 * Query the database for a customer using a phone number.
		 * If found, then return a Customer object for the customer.
		 * If it's not found....then return null
		 *  
		 */
		




		 return null;
	}


	public static ArrayList<Topping> getToppingList() throws SQLException, IOException {
		connect_to_db();
		/*
		 * Query the database for the aviable toppings and 
		 * return an arrayList of all the available toppings. 
		 * Don't forget to order the data coming from the database appropriately.
		 * 
		 */

		

		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return null;
	}

	public static Topping findToppingByName(String name){
		/*
		 * Query the database for the topping using it's name.
		 * If found, then return a Topping object for the topping.
		 * If it's not found....then return null
		 *  
		 */
		




		 return null;
	}


	public static void addToInventory(Topping t, double quantity) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Updates the quantity of the topping in the database by the amount specified.
		 * 
		 * */


		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	
	public static double getBaseCustPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		/* 
		 * Query the database fro the base customer price for that size and crust pizza.
		 * 
		*/
		
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return 0.0;
	}

	public static double getBaseBusPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		/* 
		 * Query the database fro the base business price for that size and crust pizza.
		 * 
		*/
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return 0.0;
	}

	public static void printInventory() throws SQLException, IOException {
		connect_to_db();
		/*
		 * Queries the database and prints the current topping list with quantities.
		 *  
		 * The result should be readable and sorted as indicated in the prompt.
		 * 
		 */
		ResultSet rs = null;
		String getInventory = "Select ToppingID, Name, Inventory from Toppings";
		System.out.println("ToppingID\tTopping\tCurrInv");
		try (PreparedStatement ps = conn.prepareStatement(getInventory)) {
			rs = ps.executeQuery();

			while (rs.next()) {
				int toppingID = rs.getInt("ToppingID");
				String toppingName = rs.getString("Name");
				int currInv = rs.getInt("Inventory");
				System.out.println(toppingID + "\t" + toppingName + "\t" + currInv);
			}
		}
		conn.close();
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static ArrayList<Topping> getInventory() throws SQLException, IOException {
		connect_to_db();
		ArrayList<Topping> toppingList = new ArrayList<Topping>();
		ResultSet rs = null;
		String getInventory = "SELECT ToppingID, Name, PricePerUnit, CostPerUnit, InventoryMinimum," + "Inventory,"
				+ "Small, Medium, Large, XLarge FROM Toppings";
		try (PreparedStatement ps = conn.prepareStatement(getInventory)) {
			rs = ps.executeQuery();

			while (rs.next()) {
				int toppingID = rs.getInt("ToppingID");
				String toppingName = rs.getString("Name");
				int Inv = rs.getInt("Inventory");
				int minInv = rs.getInt("InventoryMinimum");
				double price = rs.getDouble("PricePerUnit");
				double cost = rs.getDouble("CostPerUnit");
				int numPerS = rs.getInt("Small");
				int numPerM = rs.getInt("Medium");
				int numPerL = rs.getInt("Large");
				int numPerXL = rs.getInt("XLarge");
				Topping t = new Topping(toppingID, toppingName, numPerS, numPerM, numPerL, numPerXL, price, cost,
						minInv, Inv);
				toppingList.add(t);
			}
		}
		conn.close();
		return toppingList;
	}


	public static void updateOrderTotalAmount(int orderId) throws SQLException, IOException {
		connect_to_db();

		double totalAmount = calculateOrderTotalAmount(orderId);

		String updateTotalAmountQuery = "UPDATE Orders SET TotalAmount = ? WHERE OrderID = ?";

		try (PreparedStatement updatePs = conn.prepareStatement(updateTotalAmountQuery)) {
			updatePs.setDouble(1, totalAmount);
			updatePs.setInt(2, orderId);

			int rowsAffected = updatePs.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("TotalAmount updated successfully.");
			} else {
				System.out.println("No rows updated. OrderID may not exist or there are no associated pizzas.");
			}
		} catch (SQLException e) {
			System.out.println("Error updating TotalAmount: " + e.getMessage());
		} finally {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		}
	}
	private static double calculateOrderTotalAmount(int orderId) throws SQLException {
		String selectTotalAmountQuery = "SELECT COALESCE(SUM(P.Price), 0) " +
				"FROM Pizzas P JOIN OrderPizzas OP ON P.PizzaID = OP.PizzaID " +
				"WHERE OP.OrderID = ?";

		try (PreparedStatement selectPs = conn.prepareStatement(selectTotalAmountQuery)) {
			selectPs.setInt(1, orderId);

			try (ResultSet resultSet = selectPs.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getDouble(1);
				} else {
					return 0.0; // No pizzas associated with the order
				}
			}
		}
	}
	public static int getMaxCustomerID() throws SQLException, IOException {
		connect_to_db();

		String selectMaxCustomerIDQuery = "SELECT MAX(CustomerID) FROM Customers";

		try (PreparedStatement selectPs = conn.prepareStatement(selectMaxCustomerIDQuery);
			 ResultSet resultSet = selectPs.executeQuery()) {

			if (resultSet.next()) {
				return resultSet.getInt(1);
			} else {
				return 0; // No customers in the table
			}
		} catch (SQLException e) {
			System.out.println("Error getting max CustomerID: " + e.getMessage());
			return 0;
		} finally {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		}
	}

	public static void printToppingPopReport() throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Prints the ToppingPopularity view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 *
		 * The result should be readable and sorted as indicated in the prompt.
		 *
		 */
		String query = "SELECT Topping, ToppingCount FROM ToppingPopularity";

		try (Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {

			System.out.println("Topping Popularity Report:");
			while (rs.next()) {
				String topping = rs.getString("Topping");
				int toppingCount = rs.getInt("ToppingCount");
				System.out.println(topping + ": " + toppingCount);
			}
		} finally {
			conn.close();
		}
	}
	

	
	public static void printProfitByOrderType() throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Prints the ProfitByOrderType view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 *
		 * The result should be readable and sorted as indicated in the prompt.
		 *
		 */
		String query = "SELECT OrderType, MonthYear, TotalOrderPrice, TotalOrderCost, Profit FROM ProfitByOrderType";

		try (Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {

			System.out.println("Profit By Order Type Report:");
			while (rs.next()) {
				String orderType = rs.getString("OrderType");
				String monthYear = rs.getString("MonthYear");
				double totalOrderPrice = rs.getDouble("TotalOrderPrice");
				double totalOrderCost = rs.getDouble("TotalOrderCost");
				double profit = rs.getDouble("Profit");

				System.out.println("Order Type: " + orderType + ", Month Year: " + monthYear +
						", Total Order Price: " + totalOrderPrice + ", Total Order Cost: " + totalOrderCost +
						", Profit: " + profit);
			}
		} finally {
			conn.close();
		}
	}
	
	
	
	public static String getCustomerName(int CustID) throws SQLException, IOException
	{
	/*
		 * This is a helper method to fetch and format the name of a customer
		 * based on a customer ID. This is an example of how to interact with 
		 * your database from Java.  It's used in the model solution for this project...so the code works!
		 * 
		 * OF COURSE....this code would only work in your application if the table & field names match!
		 *
		 */

		 connect_to_db();

		/* 
		 * an example query using a constructed string...
		 * remember, this style of query construction could be subject to sql injection attacks!
		 * 
		 */
		String cname1 = "";
		String query = "Select FName, LName From customer WHERE CustID=" + CustID + ";";
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery(query);
		
		while(rset.next())
		{
			cname1 = rset.getString(1) + " " + rset.getString(2); 
		}

		/* 
		* an example of the same query using a prepared statement...
		* 
		*/
		String cname2 = "";
		PreparedStatement os;
		ResultSet rset2;
		String query2;
		query2 = "Select FName, LName From customer WHERE CustID=?;";
		os = conn.prepareStatement(query2);
		os.setInt(1, CustID);
		rset2 = os.executeQuery();
		while(rset2.next())
		{
			cname2 = rset2.getString("FName") + " " + rset2.getString("LName"); // note the use of field names in the getSting methods
		}

		conn.close();
		return cname1; // OR cname2
	}

	/*
	 * The next 3 private methods help get the individual components of a SQL datetime object. 
	 * You're welcome to keep them or remove them.
	 */
	private static int getYear(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(0,4));
	}
	private static int getMonth(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(5, 7));
	}
	private static int getDay(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(8, 10));
	}

	public static boolean checkDate(int year, int month, int day, String dateOfOrder)
	{
		if(getYear(dateOfOrder) > year)
			return true;
		else if(getYear(dateOfOrder) < year)
			return false;
		else
		{
			if(getMonth(dateOfOrder) > month)
				return true;
			else if(getMonth(dateOfOrder) < month)
				return false;
			else
			{
				if(getDay(dateOfOrder) >= day)
					return true;
				else
					return false;
			}
		}
	}


}