/**
 * Vincent Gagnon - GAGV2505980
 * Equivalence Classes:
 * Number of entries in the grocery list (Interval)
 * Number of characters in the UPC (Interval)
 * UPC of the items (Group)
 * Price of the items (Interval)
 * Quantity of the items (Interval, Group)
 * Other conditions (Unique)
 */

package tests;

import stev.kwikemart.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegisterTest {

	@BeforeEach
	public void setUp() throws Exception {
		Register.getRegister().changePaper(PaperRoll.LARGE_ROLL);
	}
	

	/**
	 * Test #1
	 * # of entries in list: 1-10
	 * # of chars in UPC before adding to list: 11
	 * UPC: Has valid control key
	 * Item price: < 35
	 * Item quantity: > 0
	 * 
	 * Expected result: valid output string
	 */
	@Test
	public void valid_test_1() {
		List<Item> grocery = new ArrayList<Item>();
		String incompleteUPC = "12345678901";

		grocery.add(new Item(Upc.generateCode(incompleteUPC), "Bananas", 1, 1));

		String receipt = Register.getRegister().print(grocery);

		int keyDigit = Upc.getCheckDigit(incompleteUPC);

		Assertions.assertEquals(true, receipt.contains(incompleteUPC + keyDigit + " Bananas x 1            1.00$"));
		Assertions.assertEquals(true, receipt.contains("SUB-TOTAL                           1.00$"));
		Assertions.assertEquals(true, receipt.contains("Tax SGST 5%                         0.05$"));
		Assertions.assertEquals(true, receipt.contains("TOTAL                               1.05$"));

	}

	/**
	 * Test #2
	 * # of entries in list: 1-10
	 * # of chars in UPC before adding to list: 11
	 * UPC: Has valid control key and first char is 2 when item quantity is a fraction
	 * Item price: < 35
	 * Item quantity: > 0 and is a fraction
	 * 
	 * Expected result: valid output string
	 */
	@Test
	public void valid_test_2() {
		List<Item> grocery = new ArrayList<Item>();
		String incompleteUPC = "22345678901";

		grocery.add(new Item(Upc.generateCode(incompleteUPC), "Bananas", 1, 1.5));

		String receipt = Register.getRegister().print(grocery);

		int keyDigit = Upc.getCheckDigit(incompleteUPC);

		Assertions.assertEquals(true, receipt.contains(incompleteUPC + keyDigit + " Bananas 1.00 @  1.50   1.50$"));
		Assertions.assertEquals(true, receipt.contains("SUB-TOTAL                           1.50$"));
		Assertions.assertEquals(true, receipt.contains("Tax SGST 5%                         0.08$"));
		Assertions.assertEquals(true, receipt.contains("TOTAL                               1.58$"));
	}

	
	/**
	 * Test #3
	 * # of entries in list: 1-10
	 * # of chars in UPC before adding to list: 11
	 * UPC: Has valid control key and first char is 5 when item is a coupon
	 * Item price: < 35
	 * Item quantity: > 0
	 * Other: coupon value is inferior to the total
	 * 
	 * Expected result: valid output string
	 */
	@Test
	public void valid_test_3() {
		List<Item> grocery = new ArrayList<Item>();
		String incompleteUPC = "52345678901";

		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 1));
		grocery.add(new Item(Upc.generateCode(incompleteUPC), "Coupon", 1, 1));

		String receipt = Register.getRegister().print(grocery);

		int keyDigit = Upc.getCheckDigit(incompleteUPC);

		Assertions.assertEquals(true, receipt.contains("123456789012 Bananas x 1            1.00$"));
		Assertions.assertEquals(true, receipt.contains("SUB-TOTAL                           1.00$"));
		Assertions.assertEquals(true, receipt.contains("Tax SGST 5%                         0.05$"));
		Assertions.assertEquals(true, receipt.contains(incompleteUPC + keyDigit + " Coupon: Coupon        -1.00$"));
		Assertions.assertEquals(true, receipt.contains("TOTAL                               0.05$"));
	}

	/**
	 * Test #4
	 * # of entries in list: 1-10
	 * # of chars in UPC before adding to list: 11
	 * UPC: Has valid control key
	 * Item price: < 35
	 * Item quantity: > 0
	 * Other: 5 distinct items give a 1$ rebate on total
	 * 
	 * Expected result: valid output string
	 */
	@Test
	public void valid_test_4() {
		List<Item> grocery = new ArrayList<Item>();

		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 1.5));
		grocery.add(new Item(Upc.generateCode("64748119599"), "Chewing gum", 2, 0.99));
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));

		String receipt = Register.getRegister().print(grocery);

		Assertions.assertEquals(true, receipt.contains("123456789012 Bananas x 1            1.50$"));
		Assertions.assertEquals(true, receipt.contains("647481195995 Chewing gum x 2        1.98$"));
		Assertions.assertEquals(true, receipt.contains("443482259960 Gobstoppers x 1        0.99$"));
		Assertions.assertEquals(true, receipt.contains("343234323430 Nerds x 2              2.88$"));
		Assertions.assertEquals(true, receipt.contains("615193141593 Doritos x 1            1.25$"));
		Assertions.assertEquals(true, receipt.contains("SUB-TOTAL                           8.60$"));
		Assertions.assertEquals(true, receipt.contains("Tax SGST 5%                         0.43$"));
		Assertions.assertEquals(true, receipt.contains("Rebate for 5 items                 -1.00$"));
		Assertions.assertEquals(true, receipt.contains("TOTAL                               8.03$"));
	}

	
	/**
	 * Test #5
	 * # of entries in list: 1-10
	 * # of chars in UPC before adding to list: 11
	 * UPC: Has valid control key
	 * Item price: < 35
	 * Item quantity: > 0
	 * Other: Possible to cancel an item by adding a negative value of it
	 * 
	 * Expected result: valid output string
	 */
	@Test
	public void valid_test_5() {
		List<Item> grocery = new ArrayList<Item>();

		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 1));
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", -1, 1));

		String receipt = Register.getRegister().print(grocery);

		Assertions.assertEquals(true, receipt.contains("123456789012 Bananas x 1            1.00$"));
		Assertions.assertEquals(true, receipt.contains("123456789012 Bananas x -1          -1.00$"));
		Assertions.assertEquals(true, receipt.contains("SUB-TOTAL                           0.00$"));
		Assertions.assertEquals(true, receipt.contains("Tax SGST 5%                         0.00$"));
		Assertions.assertEquals(true, receipt.contains("TOTAL                               0.00$"));
	}
	
	/**
	 * Test #6
	 * # of entries in list: <1
	 * 
	 * Expected result: error message
	 */
	@Test
	public void print_should_throw_EmptyGroceryListException_when_called_with_empty_grocery_list() {
		List<Item> grocery = new ArrayList<Item>();

		Assertions.assertThrows(RegisterException.EmptyGroceryListException.class, () -> {
			String receipt = Register.getRegister().print(grocery);
		});
	
	}
	
	/**
	 * Test #7
	 * # of entries in list: <10
	 * # of chars in UPC before adding to list: 11
	 * UPC: Has valid control key
	 * Item price: < 35
	 * Item quantity: > 0
	 * 
	 * Expected result: error message
	 */
	@Test
	public void print_should_throw_TooManyItemsException_when_called_with_grocery_list_with_too_many_items() {
		List<Item> grocery = new ArrayList<Item>();
		
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 1.5));
		grocery.add(new Item(Upc.generateCode("64748119599"), "Chewing gum", 2, 0.99));
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 1.5));
		grocery.add(new Item(Upc.generateCode("64748119599"), "Chewing gum", 2, 0.99));
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 1.5));
		grocery.add(new Item(Upc.generateCode("64748119599"), "Chewing gum", 2, 0.99));
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));

		Assertions.assertThrows(RegisterException.TooManyItemsException.class, () -> {
			String receipt = Register.getRegister().print(grocery);
		});
	
	}
	
	/**
	 * Test #8
	 * # of entries in list: 1-10
	 * # of chars in UPC before adding to list: >11
	 * UPC: Has valid control key
	 * Item price: < 35
	 * Item quantity: > 0
	 * 
	 * Expected result: error message
	 */
	@Test
	public void print_should_throw_UpcTooLongException_when_trying_to_add_an_item_with_an_UPC_longer_than_11() {
		List<Item> grocery = new ArrayList<Item>();

		Assertions.assertThrows(InvalidUpcException.UpcTooLongException.class, () -> {
			grocery.add(new Item(Upc.generateCode("123456789015"), "Bananas", 1, 1));

			String receipt = Register.getRegister().print(grocery);
		});
	
	}
	
	/**
	 * Test #9
	 * # of entries in list: 1-10
	 * # of chars in UPC before adding to list: <11
	 * UPC: Has valid control key
	 * Item price: < 35
	 * Item quantity: > 0
	 * 
	 * Expected result: error message
	 */
	@Test
	public void print_should_throw_UpcTooShortException_when_trying_to_add_an_item_with_an_UPC_shorter_than_11() {
		List<Item> grocery = new ArrayList<Item>();

		Assertions.assertThrows(InvalidUpcException.UpcTooShortException.class, () -> {
			grocery.add(new Item(Upc.generateCode("1234567890"), "Bananas", 1, 1));

			String receipt = Register.getRegister().print(grocery);
		});
	
	}
	
	/**
	 * Test #10
	 * # of entries in list: 1-10
	 * UPC: Has invalid control key
	 * Item price: < 35
	 * Item quantity: > 0
	 * 
	 * Expected result: error message
	 */
	@Test
	public void print_should_throw_InvalidCheckDigitException_when_trying_to_add_an_item_with_an_UPC_that_contains_an_invalid_check_digit() {
		List<Item> grocery = new ArrayList<Item>();
		String badUPC = "123456789024";
		grocery.add(new Item(badUPC, "Bananas", 1, 1));

		Assertions.assertThrows(InvalidUpcException.InvalidCheckDigitException.class, () -> {

			String receipt = Register.getRegister().print(grocery);
		});
	
	}
	
	/**
	 * Test #18
	 * # of entries in list: 1-10
	 * UPC: Has invalid character
	 * Item price: < 35
	 * Item quantity: > 0
	 * 
	 * Expected result: error message
	 */
	@Test
	public void print_should_throw_InvalidDigitException_when_trying_to_add_an_item_with_an_UPC_that_contains_an_invalid_character() {
		List<Item> grocery = new ArrayList<Item>();
		String badUPC = "12345678a024";
		grocery.add(new Item(badUPC, "Bananas", 1, 1));

		Assertions.assertThrows(InvalidUpcException.InvalidDigitException.class, () -> {

			String receipt = Register.getRegister().print(grocery);
		});
	
	}
	
	/**
	 * Test #19
	 * # of entries in list: 1-10
	 * UPC: is empty string
	 * Item price: < 35
	 * Item quantity: > 0
	 * 
	 * Expected result: error message
	 */
	@Test
	public void print_should_throw_NoUpcException_when_trying_to_add_an_item_with_an_empty_string_as_a_UPC() {
		List<Item> grocery = new ArrayList<Item>();
		String badUPC = "";
		grocery.add(new Item(badUPC, "Bananas", 1, 1));

		Assertions.assertThrows(InvalidUpcException.NoUpcException.class, () -> {

			String receipt = Register.getRegister().print(grocery);
		});
	
	}
	
	/**
	 * Test #11
	 * # of entries in list: 1-10
	 * # of chars in UPC before adding to list: 11
	 * UPC: has a fractional item quantity but not 2 as the first UPC digit
	 * Item price: < 35
	 * Item quantity: > 0
	 * 
	 * Expected result: error message
	 */
	@Test
	public void print_should_throw_InvalidQuantityForCategoryException_when_trying_to_add_an_item_that_has_a_wrong_UPC_with_a_fractional_qty() {
		List<Item> grocery = new ArrayList<Item>();
		grocery.add(new Item(Upc.generateCode("12345678903"), "Bananas", 1.5, 1));

		Assertions.assertThrows(InvalidQuantityException.InvalidQuantityForCategoryException.class, () -> {
			String receipt = Register.getRegister().print(grocery);
		});
	
	}
	
	/**
	 * Test #12
	 * # of entries in list: 1-10
	 * # of chars in UPC before adding to list: 11
	 * UPC: has a valid control key
	 * Item price: > 35
	 * Item quantity: > 0
	 * 
	 * Expected result: error message
	 */
	@Test
	public void print_should_throw_AmountTooLargeException_when_trying_to_add_an_item_that_has_a_price_higher_than_35() {
		List<Item> grocery = new ArrayList<Item>();
		grocery.add(new Item(Upc.generateCode("12345678903"), "Bananas", 1, 36));

		Assertions.assertThrows(AmountException.AmountTooLargeException.class, () -> {

			String receipt = Register.getRegister().print(grocery);
		});
	
	}
	
	/**
	 * Test #13
	 * # of entries in list: 1-10
	 * # of chars in UPC before adding to list: 11
	 * UPC: has a valid control key
	 * Item price: < 0
	 * Item quantity: > 0
	 * 
	 * Expected result: error message
	 */
	@Test
	public void print_should_throw_NegativeAmountException_when_trying_to_add_an_item_that_has_a_price_lower_than_0() {
		List<Item> grocery = new ArrayList<Item>();
		grocery.add(new Item(Upc.generateCode("12345678903"), "Bananas", 1, -1));

		Assertions.assertThrows(AmountException.NegativeAmountException.class, () -> {

			String receipt = Register.getRegister().print(grocery);
		});
	
	}
	
	/**
	 * Test #14
	 * # of entries in list: 1-10
	 * # of chars in UPC before adding to list: 11
	 * UPC: has a valid control key
	 * Item price: < 35
	 * Item quantity: < 0 and not a cancellation of a previous item
	 * 
	 * Expected result: error message
	 */
	@Test
	public void print_should_throw_NoSuchItemException_when_trying_to_add_an_item_that_has_a_negative_quantity_when_it_was_not_added_before() {
		List<Item> grocery = new ArrayList<Item>();
		grocery.add(new Item(Upc.generateCode("12345678903"), "Bananas", -1, 1));

		Assertions.assertThrows(Register.NoSuchItemException.class, () -> {

			String receipt = Register.getRegister().print(grocery);
		});
	
	}
	
	/**
	 * Test #15
	 * # of entries in list: 1-10
	 * # of chars in UPC before adding to list: 11
	 * UPC: has a valid control key
	 * Item price: < 35
	 * Item quantity: > 0
	 * Other: 2 items with same UPC and not a cancellation
	 * 
	 * Expected result: error message
	 */
	@Test
	public void print_should_throw_DuplicateItemException_when_trying_to_buy_an_item_that_is_already_in_the_list() {
		List<Item> grocery = new ArrayList<Item>();
		grocery.add(new Item(Upc.generateCode("12345678903"), "Bananas", 1, 1));
		grocery.add(new Item(Upc.generateCode("12345678903"), "Bananas", 1, 1));


		Assertions.assertThrows(Register.DuplicateItemException.class, () -> {

			String receipt = Register.getRegister().print(grocery);
		});
	
	}
	
	/**
	 * Test #16
	 * # of entries in list: 1-10
	 * # of chars in UPC before adding to list: 11
	 * UPC: has a valid control key
	 * Item price: < 35
	 * Item quantity: > 0
	 * Other: total is inferior to coupon's amount at the time of adding it
	 * 
	 * Expected result: print() should ignore coupon
	 */
	@Test
	public void print_should_cancel_coupon_if_it_is_added_while_total_is_inferior_to_its_amount() {
		List<Item> grocery = new ArrayList<Item>();
		grocery.add(new Item(Upc.generateCode("12345678903"), "Bananas", 1, 1));
		grocery.add(new Item(Upc.generateCode("52345678903"), "Coupon", 1, 5));



		String receipt = Register.getRegister().print(grocery);
		
		Assertions.assertEquals(false, receipt.contains("523456789034 Coupon: Coupon        -5.00$"));
		Assertions.assertEquals(true, receipt.contains("TOTAL                               1.05$"));

	}
	
	/**
	 * Test #17
	 * # of entries in list: 1-10
	 * # of chars in UPC before adding to list: 11
	 * UPC: has a valid control key and is a coupon
	 * Item price: < 35
	 * Item quantity: != 1
	 * Other: coupon quantity is different than 1
	 * 
	 * Expected result: error message
	 */
	@Test
	public void print_should_throw_InvalidCouponQuantityException_when_trying_to_buy_a_coupon_with_a_quantity_other_than_1() {
		List<Item> grocery = new ArrayList<Item>();
		grocery.add(new Item(Upc.generateCode("12345678903"), "Bananas", 1, 1));
		grocery.add(new Item(Upc.generateCode("52345678903"), "coupon", 2, 1));


		Assertions.assertThrows(CouponException.InvalidCouponQuantityException.class, () -> {

			String receipt = Register.getRegister().print(grocery);
		});
	
	}
	
	/**
	 * Test #20
	 * # of entries in list: 1-10
	 * # of chars in UPC before adding to list: 11
	 * UPC: has a valid control key
	 * Item price: < 35
	 * Item quantity: > 0
	 * Other: no paper roll in register
	 * 
	 * Expected result: error message
	 */
	@Test
	public void print_should_throw_NoPaperRollException_when_trying_to_print_without_a_paper_roll() {
		Register.getRegister().changePaper(null);

		List<Item> grocery = new ArrayList<Item>();
		grocery.add(new Item(Upc.generateCode("12345678903"), "Bananas", 1, 1));
		
		Assertions.assertThrows(Register.NoPaperRollException.class, () -> {

			String receipt = Register.getRegister().print(grocery);
		});
	
	}
	
	/**
	 * Test #21
	 * # of entries in list: 1-10
	 * # of chars in UPC before adding to list: 11
	 * UPC: has a valid control key
	 * Item price: < 35
	 * Item quantity: > 0
	 * Other: paper roll out of lines
	 * 
	 * Expected result: error message
	 */
	@Test
	public void print_should_throw_OutOfPaperException_when_trying_to_print_without_a_paper_roll() {
		Register.getRegister().changePaper(PaperRoll.SMALL_ROLL);

		
		
		Assertions.assertThrows(PaperRollException.OutOfPaperException.class, () -> {
			List<Item> grocery = new ArrayList<Item>();
			grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 1.5));
			grocery.add(new Item(Upc.generateCode("64748119599"), "Chewing gum", 2, 0.99));
			grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
			grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
			grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
			
			Register.getRegister().print(grocery);
			Register.getRegister().print(grocery);
		});
	
	}

}
