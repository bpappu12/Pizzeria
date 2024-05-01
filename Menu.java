package cpsc4620;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/*
 * This file is where the front end magic happens.
 * 
 * You will have to write the methods for each of the menu options.
 * 
 * This file should not need to access your DB at all, it should make calls to the DBNinja that will do all the connections.
 * 
 * You can add and remove methods as you see necessary. But you MUST have all of the menu methods (including exit!)
 * 
 * Simply removing menu methods because you don't know how to implement it will result in a major error penalty (akin to your program crashing)
 * 
 * Speaking of crashing. Your program shouldn't do it. Use exceptions, or if statements, or whatever it is you need to do to keep your program from breaking.
 * 
 */

public class Menu {

	public static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args) throws SQLException, IOException {

		System.out.println("Welcome to Pizzas-R-Us!");
		
		int menu_option = 0;

		// present a menu of options and take their selection
		
		PrintMenu();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String option = reader.readLine();
		menu_option = Integer.parseInt(option);

		while (menu_option != 9) {
			switch (menu_option) {
			case 1:// enter order
				EnterOrder();
				break;
			case 2:// view customers
				viewCustomers();
				break;
			case 3:// enter customer
				EnterCustomer();
				break;
			case 4:// view order
				// open/closed/date
				ViewOrders();
				break;
			case 5:// mark order as complete
				MarkOrderAsComplete();
				break;
			case 6:// view inventory levels
				ViewInventoryLevels();
				break;
			case 7:// add to inventory
				AddInventory();
				break;
			case 8:// view reports
				PrintReports();
				break;
			}
			PrintMenu();
			option = reader.readLine();
			menu_option = Integer.parseInt(option);
		}

	}

	// allow for a new order to be placed
	public static void EnterOrder() throws SQLException, IOException 
	{

		/*
		 * EnterOrder should do the following:
		 * 
		 * Ask if the order is delivery, pickup, or dinein
		 *   if dine in....ask for table number
		 *   if pickup...
		 *   if delivery...
		 * 
		 * Then, build the pizza(s) for the order (there's a method for this)
		 *  until there are no more pizzas for the order
		 *  add the pizzas to the order
		 *
		 * Apply order discounts as needed (including to the DB)
		 * 
		 * return to menu
		 * 
		 * make sure you use the prompts below in the correct order!
		 */

		 // User Input Prompts...
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		Order order = new Order(0, 0, "Dine-In", "n ", 0, 0, 0);
		int orderId = 0;
		order.setOrderID(orderId);
		System.out.println("Is this order for: \n1.) Dine-in\n2.) Pick-up\n3.) Delivery\nEnter the number of your choice:");
		int orderType = Integer.parseInt(reader.readLine());
		if (orderType == 1){
			System.out.println("Enter the table number");
			int tableNum = Integer.parseInt(reader.readLine());
			while (true) {
				Pizza p = buildPizza(orderId);
				DBNinja.addPizza(p);
				int pizzaID = DBNinja.getMaxPizzaID();
				DBNinja.addToppings(p, pizzaID);
				DBNinja.updatePizzaCostAndPrice(DBNinja.getMaxPizzaID());

				System.out.println("Do you want to add another pizza? (y/n): ");
				String addAnotherPizza = reader.readLine().trim().toLowerCase();

				if (!addAnotherPizza.equals("y")) {
					break; // Exit the loop if the user doesn't want to add another pizza
				}
			}
			System.out.println("Is this order for an existing customer? Answer y/n: ");
			String existingCustomer = reader.readLine();
			System.out.println("This is what you entered " + existingCustomer);
			if(Objects.equals(existingCustomer, "y"))
			{
				System.out.println("Here's a list of the current customers: ");
				viewCustomers();
				System.out.println("Which customer is this order for? Enter ID Number ");
				int custID = Integer.parseInt(reader.readLine());
				order.setCustID(custID);
				DBNinja.addOrder(order);
				System.out.println(DBNinja.getNextOrderID()-1);
				DBNinja.linkOrderToPizza(DBNinja.getNextOrderID()-1, pizzaID);
			}
			else if(existingCustomer == "n") {
				order.setCustID(0);
				DBNinja.addOrder(order);
				DBNinja.linkOrderToPizza(DBNinja.getNextOrderID()-1, pizzaID);
			}
			else {
				System.out.println("Incorrect input, returning to menu...");
			}

		}
		if (orderType == 2){
			Pizza p = buildPizza(orderId);
			DBNinja.addPizza(p);
			int pizzaID = DBNinja.getMaxPizzaID();
			DBNinja.addToppings(p, pizzaID);
			DBNinja.updatePizzaCostAndPrice(DBNinja.getMaxPizzaID());
			System.out.println("Is this order for an existing customer? Answer y/n: ");
			String existingCustomer = reader.readLine();
			System.out.println("This is what you entered " + existingCustomer);
			if(Objects.equals(existingCustomer, "y"))
			{
				System.out.println("Here's a list of the current customers: ");
				viewCustomers();
				System.out.println("Which customer is this order for? Enter ID Number ");
				int custID = Integer.parseInt(reader.readLine());
				order.setCustID(custID);
				DBNinja.addOrder(order);
				System.out.println(DBNinja.getNextOrderID()-1);
				DBNinja.linkOrderToPizza(DBNinja.getNextOrderID()-1, pizzaID);
			}
			else if(existingCustomer == "n") {
				EnterCustomer();
				order.setCustID(DBNinja.getMaxCustomerID());
				DBNinja.addOrder(order);
				DBNinja.linkOrderToPizza(DBNinja.getNextOrderID()-1, pizzaID);
			}
			else {
				System.out.println("Incorrect input, returning to menu...");
			}
		}
		if (orderType == 3){
			System.out.println("Enter the customer address");
			String address = reader.readLine();
			Pizza p = buildPizza(orderId);
			DBNinja.addPizza(p);
			int pizzaID = DBNinja.getMaxPizzaID();
			DBNinja.addToppings(p, pizzaID);
			DBNinja.updatePizzaCostAndPrice(DBNinja.getMaxPizzaID());
			System.out.println("Is this order for an existing customer? Answer y/n: ");
			String existingCustomer = reader.readLine();
			System.out.println("This is what you entered " + existingCustomer);
			if(Objects.equals(existingCustomer, "y"))
			{
				System.out.println("Here's a list of the current customers: ");
				viewCustomers();
				System.out.println("Which customer is this order for? Enter ID Number ");
				int custID = Integer.parseInt(reader.readLine());
				order.setCustID(custID);
				DBNinja.addOrder(order);
				System.out.println(DBNinja.getNextOrderID()-1);
				DBNinja.linkOrderToPizza(DBNinja.getNextOrderID()-1, pizzaID);
			}
			else if(existingCustomer == "n") {
				EnterCustomer();
				order.setCustID(DBNinja.getMaxCustomerID());
				DBNinja.addOrder(order);
				DBNinja.linkOrderToPizza(DBNinja.getNextOrderID()-1, pizzaID);
			}
			else {
				System.out.println("Incorrect input, returning to menu...");
			}
			DBNinja.insertDeliveryInformation(DBNinja.getNextOrderID()-1, address);
		}
		System.out.println("Finished adding order...Returning to menu...");
	}


	public static void viewCustomers() throws SQLException, IOException
	{
		Connection connection = DBConnector.make_connection();
		ResultSet rs = null;
		/*
		 * Simply print out all of the customers from the database.
		 */
		try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM Customers")) {
			rs = ps.executeQuery();
			ArrayList<Customer> customers = DBNinja.getCustomerList();
			for (Customer c: customers) {
				System.out.println(c.toString());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		connection.close();
	}

	

	// Enter a new customer in the database
	public static void EnterCustomer() throws SQLException, IOException 
	{
		/*
		 * Ask for the name of the customer:
		 *   First Name <space> Last Name
		 * 
		 * Ask for the  phone number.
		 *   (##########) (No dash/space)
		 * 
		 * Once you get the name and phone number, add it to the DB
		 */
		
		// User Input Prompts...
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		System.out.print("What is this customer's first name?");
		String fname = reader.readLine();
		System.out.print("What is this customer's last name?");
		String lname = reader.readLine();
		System.out.print("What is this customer's phone number (##########) (No dash/space): ");
		String phone = reader.readLine();

		Customer customer = new Customer(0, fname, lname, phone);
		DBNinja.addCustomer(customer);

		System.out.println("Customer added successfully!");
	}


	// View any orders that are not marked as completed
	public static void ViewOrders() throws SQLException, IOException
	{
		/*
		* This method allows the user to select between three different views of the Order history:
		* The program must display:
		* a.	all open orders
		* b.	all completed orders
		* c.	all the orders (open and completed) since a specific date (inclusive)
		*
		* After displaying the list of orders (in a condensed format) must allow the user to select a specific order for viewing its details.
		* The details include the full order type information, the pizza information (including pizza discounts), and the order discounts.
		*
		*/

		// User Input Prompts...
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		Connection connection = DBConnector.make_connection();
		ResultSet rs = null;
		System.out.println("Would you like to:\n(a) display all orders [open or closed]\n(b) display all open orders\n(c) display all completed [closed] orders\n(d) display orders since a specific date");
		String option = "";
		option = reader.readLine();
		if (option.equals("a")) {
			String viewOrders = "SELECT * FROM Orders";
			try (PreparedStatement ps = connection.prepareStatement(viewOrders)) {
				rs = ps.executeQuery();

				while (rs.next()) {
					int order_id = rs.getInt("OrderID");
					String date = rs.getString("OrderTime"); // Changed from "OrderDate" to "OrderTime"
					String type = rs.getString("OrderType");
					boolean isComplete = rs.getBoolean("OrderComplete");
					System.out.println("OrderID=" + order_id + " | Date Placed=" + date + " | OrderType=" + type
							+ " | IsComplete=" + isComplete);
				}
			} catch (SQLException e) {
				System.out.println(e);
			}
		}
		else if (option.equals("b")) {
			String viewOpenOrders = "SELECT * FROM Orders WHERE OrderComplete = FALSE";
			try (PreparedStatement ps = connection.prepareStatement(viewOpenOrders)) {
				rs = ps.executeQuery();

				while (rs.next()) {
					int order_id = rs.getInt("OrderID");
					String date = rs.getString("OrderTime"); // Changed from "OrderDate" to "OrderTime"
					String type = rs.getString("OrderType");
					boolean isComplete = rs.getBoolean("OrderComplete");
					System.out.println("OrderID=" + order_id + " | Date Placed=" + date + " | OrderType=" + type
							+ " | IsComplete=" + isComplete);
				}
			} catch (SQLException e) {
				System.out.println(e);
			}

		}
		else if (option.equals("c")) {
			String viewClosedOrders = "SELECT * FROM Orders WHERE OrderComplete = TRUE";
			try (PreparedStatement ps = connection.prepareStatement(viewClosedOrders)) {
				rs = ps.executeQuery();

				while (rs.next()) {
					int order_id = rs.getInt("OrderID");
					String date = rs.getString("OrderTime");
					String type = rs.getString("OrderType");
					boolean isComplete = rs.getBoolean("OrderComplete");
					System.out.println("OrderID=" + order_id + " | Date Placed=" + date + " | OrderType=" + type
							+ " | IsComplete=" + isComplete);
				}
			} catch (SQLException e) {
				System.out.println(e);
			}
		}
		else if (option.equals("d")) {
			System.out.println("What is the date you want to restrict by? (FORMAT= YYYY-MM-DD)");
			String specifiedDate = reader.readLine();

			String viewOrdersSinceDate = "SELECT * FROM Orders WHERE OrderTime >= ?";
			try (PreparedStatement ps = connection.prepareStatement(viewOrdersSinceDate)) {
				ps.setString(1, specifiedDate);
				rs = ps.executeQuery();

				while (rs.next()) {
					int order_id = rs.getInt("OrderID");
					String date = rs.getString("OrderTime");
					String type = rs.getString("OrderType");
					boolean isComplete = rs.getBoolean("OrderComplete");
					System.out.println("OrderID=" + order_id + " | Date Placed=" + date + " | OrderType=" + type
							+ " | IsComplete=" + isComplete);
				}
			} catch (SQLException e) {
				System.out.println(e);
			}
		}
	}

	
	// When an order is completed, we need to make sure it is marked as complete
	public static void MarkOrderAsComplete() throws SQLException, IOException 
	{
		/*
		 * All orders that are created through java (part 3, not the orders from part 2) should start as incomplete
		 * 
		 * When this method is called, you should print all of the "opoen" orders marked
		 * and allow the user to choose which of the incomplete orders they wish to mark as complete
		 * 
		 */
		DBNinja.markOrderAsComplete();
	}

	public static void ViewInventoryLevels() throws SQLException, IOException 
	{
		/*
		 * Print the inventory. Display the topping ID, name, and current inventory
		*/
		DBNinja.printInventory();
	}


	public static void AddInventory() throws SQLException, IOException 
	{
		/*
		 * This should print the current inventory and then ask the user which topping (by ID) they want to add more to and how much to add
		 */
		
		
		// User Input Prompts...
		System.out.println("Which topping do you want to add inventory to? Enter the number: ");
		DBNinja.printInventory();
		int toppingID = Integer.parseInt(reader.readLine());
		System.out.println("How many units would you like to add? ");
		int amount = Integer.parseInt(reader.readLine());
		if(toppingID > 17 || toppingID < 0) {
			System.out.println("Incorrect entry, not an option");
		}
		else {
			DBNinja.addToppingInventory(toppingID, amount);
			DBNinja.printInventory();
		}
	}

	// A method that builds a pizza. Used in our add new order method
	public static Pizza buildPizza(int orderID) throws SQLException, IOException 
	{
		
		/*
		 * This is a helper method for first menu option.
		 * 
		 * It should ask which size pizza the user wants and the crustType.
		 * 
		 * Once the pizza is created, it should be added to the DB.
		 * 
		 * We also need to add toppings to the pizza. (Which means we not only need to add toppings here, but also our bridge table)
		 * 
		 * We then need to add pizza discounts (again, to here and to the database)
		 * 
		 * Once the discounts are added, we can return the pizza
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		ArrayList<Topping> toppingSelection = new ArrayList<Topping>();
		ArrayList<Topping> toppingList = DBNinja.getInventory();
		Pizza p = null;
		String inputSize = " ";
		String inputTopping = " ";
		String inputCrustType = " ";
		int toppingchoice = 0;
		String extraToppings = " ";
		// User Input Prompts...
		System.out.println("What size is the pizza?");
		System.out.println("1."+DBNinja.size_s);
		System.out.println("2."+DBNinja.size_m);
		System.out.println("3."+DBNinja.size_l);
		System.out.println("4."+DBNinja.size_xl);
		System.out.println("Enter the corresponding number: ");
		int size = Integer.parseInt(reader.readLine());

		switch(size) {
			case 1: inputSize = DBNinja.size_s;
				break;
			case 2: inputSize = DBNinja.size_m;
				break;
			case 3: inputSize = DBNinja.size_l;
				break;
			case 4: inputSize = DBNinja.size_xl;
		}
		System.out.println("What crust for this pizza?");
		System.out.println("1."+DBNinja.crust_thin);
		System.out.println("2."+DBNinja.crust_orig);
		System.out.println("3."+DBNinja.crust_pan);
		System.out.println("4."+DBNinja.crust_gf);
		System.out.println("Enter the corresponding number: ");
		int crustType = Integer.parseInt(reader.readLine());
		switch(crustType) {
			case 1: inputCrustType = DBNinja.crust_thin;
				break;
			case 2: inputCrustType = DBNinja.crust_orig;
				break;
			case 3: inputCrustType = DBNinja.crust_pan;
				break;
			case 4: inputCrustType = DBNinja.crust_gf;

		}
		int basePriceID = DBNinja.getBasePriceID(inputSize, inputCrustType);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();

		p = new Pizza(0, inputSize, inputCrustType, orderID, "incomplete", dtf.format(now), 0, 0, basePriceID);
		System.out.println(p.toString());
		return p;
	}


	public static void PrintReports() throws SQLException, NumberFormatException, IOException {
		/*
		 * This method asks the user which report they want to see and prints the appropriate report.
		 *
		 */

		// User Input Prompts...
		System.out.println("Which report do you wish to print? Enter\n(a) ToppingPopularity\n(b) ProfitByPizza\n(c) ProfitByOrderType:");

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String choice = reader.readLine();

			switch (choice.toLowerCase()) {
				case "a":
					DBNinja.printToppingPopReport();
					break;
				case "b":
					DBNinja.printProfitByPizzaReport();
					break;
				case "c":
					DBNinja.printProfitByOrderTypeReport();
					break;
				default:
					System.out.println("Invalid input. Returning to menu...");
			}
		} catch (IOException e) {
			System.out.println("Error reading user input. Returning to menu...");
		}
	}



	//Prompt - NO CODE SHOULD TAKE PLACE BELOW THIS LINE
	// DO NOT EDIT ANYTHING BELOW HERE, THIS IS NEEDED TESTING.
	// IF YOU EDIT SOMETHING BELOW, IT BREAKS THE AUTOGRADER WHICH MEANS YOUR GRADE WILL BE A 0 (zero)!!

	public static void PrintMenu() {
		System.out.println("\n\nPlease enter a menu option:");
		System.out.println("1. Enter a new order");
		System.out.println("2. View Customers ");
		System.out.println("3. Enter a new Customer ");
		System.out.println("4. View orders");
		System.out.println("5. Mark an order as completed");
		System.out.println("6. View Inventory Levels");
		System.out.println("7. Add Inventory");
		System.out.println("8. View Reports");
		System.out.println("9. Exit\n\n");
		System.out.println("Enter your option: ");
	}

	/*
	 * autograder controls....do not modiify!
	 */

	public final static String autograder_seed = "6f1b7ea9aac470402d48f7916ea6a010";

	
	private static void autograder_compilation_check() {

		try {
			Order o = null;
			Pizza p = null;
			Topping t = null;
			Discount d = null;
			Customer c = null;
			ArrayList<Order> alo = null;
			ArrayList<Discount> ald = null;
			ArrayList<Customer> alc = null;
			ArrayList<Topping> alt = null;
			double v = 0.0;
			String s = "";

			DBNinja.addOrder(o);
			DBNinja.addPizza(p);
			DBNinja.useTopping(p, t, false);
			DBNinja.usePizzaDiscount(p, d);
			DBNinja.useOrderDiscount(o, d);
			DBNinja.addCustomer(c);
			DBNinja.completeOrder(o);
			alo = DBNinja.getOrders(false);
			o = DBNinja.getLastOrder();
			alo = DBNinja.getOrdersByDate("01/01/1999");
			ald = DBNinja.getDiscountList();
			d = DBNinja.findDiscountByName("Discount");
			alc = DBNinja.getCustomerList();
			c = DBNinja.findCustomerByPhone("0000000000");
			alt = DBNinja.getToppingList();
			t = DBNinja.findToppingByName("Topping");
			DBNinja.addToInventory(t, 1000.0);
			v = DBNinja.getBaseCustPrice("size", "crust");
			v = DBNinja.getBaseBusPrice("size", "crust");
			DBNinja.printInventory();
			DBNinja.printToppingPopReport();
			DBNinja.printProfitByPizzaReport();
			DBNinja.printProfitByOrderType();
			s = DBNinja.getCustomerName(0);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}


}


