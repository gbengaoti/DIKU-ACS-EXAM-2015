package com.acertainmarket.tests;

import com.acertainmarket.business.Bid;
import com.acertainmarket.business.CertainMarket;
import com.acertainmarket.business.Item;
import com.acertainmarket.client.CertainMarketHTTPProxy;
import com.acertainmarket.interfaces.AuctionMarket;
import com.acertainmarket.utils.CertainMarketConstants;
import com.acertainmarket.utils.CertainMarketException;

import org.junit.*;
import java.util.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CertainMarketTest {
	private static final int ITEM_ID = 59;
	private static final int INVALID_ITEM_ID = -1;
	private static final String INVALID_ITEM_DESCRIPTION = "";
	private static final String ITEM_DESCRIPTION1 = "A bag";
	private static final String ITEM_DESCRIPTION2 = "A item";
	private static final int SELLER_ID = 5090;
	private static final int BUYER_ORG_ID = 5030;
	private static final int INVALID_SELLER_ID = -19;
	private static final int NUM_OPS = 10;
	private static final int NUM_THREADS = 100;
	private static final float PRICE = 90;
	private static AuctionMarket client;
	private static boolean localTest = true;
	private static boolean ERROR = false;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			String localTestProperty = System
					.getProperty(CertainMarketConstants.PROPERTY_KEY_LOCAL_TEST);
			localTest = (localTestProperty != null) ? Boolean
					.parseBoolean(localTestProperty) : localTest;
			if (localTest) {
				CertainMarket market = new CertainMarket();
				client = market;
			} else {
				client = new CertainMarketHTTPProxy("http://localhost:8081");
			}
			client.switchEpoch();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@After
	public void cleanupMarket() throws CertainMarketException {
		client.switchEpoch();
	}

	/**
	 * Helper method for adding items to the market
	 * 
	 */
	public void addItems(int ItemId, int SellerId) {
		Set<Item> items = new HashSet<Item>();

		items.add(new Item(ItemId, ITEM_DESCRIPTION1, SellerId));
		items.add(new Item(ItemId, ITEM_DESCRIPTION2, SellerId));
		try {
			client.addItems(items);
		} catch (CertainMarketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Invalid Item ID should fail
	 */
	@Test
	public void testAddInvalidItemID() throws CertainMarketException {
		Set<Item> items = new HashSet<Item>();
		List<Item> itemInStore = new ArrayList<Item>();
		items.add(new Item(INVALID_ITEM_ID, "A book", SELLER_ID));
		items.add(new Item(ITEM_ID + 1, "A bag", SELLER_ID + 1));

		try {
			client.addItems(items);
			fail();
		} catch (CertainMarketException e) {
			e.printStackTrace();
		}
		itemInStore = client.queryItems();
		assertTrue(itemInStore.isEmpty());
	}

	/**
	 * Adding an existing item to the store should fail
	 * 
	 * @throws CertainMarketException
	 */
	@Test
	public void testAddExistingItem() throws CertainMarketException {
		Set<Item> item1 = new HashSet<Item>();
		Set<Item> item2 = new HashSet<Item>();
		List<Item> itemInStore = new ArrayList<Item>();
		item1.add(new Item(ITEM_ID + NUM_OPS, ITEM_DESCRIPTION1, SELLER_ID));
		item2.add(new Item(ITEM_ID + NUM_OPS, ITEM_DESCRIPTION1, SELLER_ID));
		// add first copy to the market
		try {
			client.addItems(item1);
		} catch (CertainMarketException e1) {
			e1.printStackTrace();
		}
		// add second copy to market
		try {
			client.addItems(item2);
			fail();
		} catch (CertainMarketException e) {
			e.printStackTrace();
		}

		itemInStore = client.queryItems();

		assertTrue(itemInStore.size() == item1.size()
				&& itemInStore.get(0).getItemID() == item1.iterator().next()
						.getItemID());
	}

	/*
	 * an invalid seller Id should fail
	 */
	@Test
	public void testInvalidSellerId() throws CertainMarketException {
		Set<Item> items = new HashSet<Item>();
		List<Item> itemInStore = new ArrayList<Item>();
		items.add(new Item(ITEM_ID, ITEM_DESCRIPTION1, SELLER_ID + 3));
		items.add(new Item(ITEM_ID, ITEM_DESCRIPTION1, INVALID_SELLER_ID));
		try {
			client.addItems(items);
			fail();
		} catch (CertainMarketException e) {
			e.printStackTrace();
		}
		itemInStore = client.queryItems();
		assertEquals(itemInStore.size(), 0);
	}

	/*
	 * 
	 * if the description is either null or empty, it should fail
	 */
	@Test
	public void testInvalidDecription() throws CertainMarketException {
		Set<Item> items = new HashSet<Item>();
		List<Item> itemInStore = new ArrayList<Item>();
		items.add(new Item(ITEM_ID, ITEM_DESCRIPTION1, SELLER_ID + 10));
		items.add(new Item(ITEM_ID, INVALID_ITEM_DESCRIPTION, SELLER_ID + 10));
		try {
			client.addItems(items);
			fail();
		} catch (CertainMarketException e) {
			e.printStackTrace();
		}

		itemInStore = client.queryItems();
		assertTrue(itemInStore.isEmpty());
	}

	/*
	 * insertion of null items should fail
	 */
	@Test
	public void testNullItems() throws CertainMarketException {
		List<Item> itemInStore = new ArrayList<Item>();
		Set<Item> items = null;
		try {
			client.addItems(items);
			fail();

		} catch (CertainMarketException e) {
			e.printStackTrace();
		}
		itemInStore = client.queryItems();
		assertTrue(itemInStore.isEmpty());
	}

	/*
	 * tests functionality of query items
	 */
	@Test
	public void testQueryItems() throws CertainMarketException {
		Set<Item> items = new HashSet<Item>();
		List<Item> itemsAdded = new ArrayList<Item>();
		items.add(new Item(ITEM_ID + 1, ITEM_DESCRIPTION1, SELLER_ID + 11));
		items.add(new Item(ITEM_ID + 2, ITEM_DESCRIPTION2, SELLER_ID + 1));
		itemsAdded
				.add(new Item(ITEM_ID + 1, ITEM_DESCRIPTION1, SELLER_ID + 11));
		itemsAdded.add(new Item(ITEM_ID + 2, ITEM_DESCRIPTION2, SELLER_ID + 1));
		try {
			client.addItems(items);
		} catch (CertainMarketException e) {
			e.printStackTrace();
		}
		List<Item> itemsInMarketAfterTest = new ArrayList<Item>();
		itemsInMarketAfterTest = client.queryItems();
		assertTrue(itemsInMarketAfterTest.size() == itemsAdded.size()
				&& itemsInMarketAfterTest.equals(itemsAdded));
	}

	/*
	 * test the functionality of switch epoch
	 */
	@Test
	public void testSwitchEpoch() throws CertainMarketException {
		Set<Item> items = new HashSet<Item>();
		List<Item> itemInStore = new ArrayList<Item>();
		items.add(new Item(ITEM_ID, ITEM_DESCRIPTION2, SELLER_ID + 1));
		items.add(new Item(ITEM_ID + 1, ITEM_DESCRIPTION2, SELLER_ID + 1));
		items.add(new Item(ITEM_ID + 2, ITEM_DESCRIPTION2, SELLER_ID + 2));
		items.add(new Item(ITEM_ID + 3, ITEM_DESCRIPTION2, SELLER_ID + 4));
		items.add(new Item(ITEM_ID + 4, ITEM_DESCRIPTION2, SELLER_ID + 7));
		items.add(new Item(ITEM_ID + 5, ITEM_DESCRIPTION2, SELLER_ID + 8));

		client.addItems(items);

		Set<Bid> bids = new HashSet<Bid>();
		bids.add(new Bid(BUYER_ORG_ID + 4, ITEM_ID + 1, PRICE + 1));
		bids.add(new Bid(BUYER_ORG_ID + 4, ITEM_ID + 1, PRICE + 100));
		bids.add(new Bid(BUYER_ORG_ID + 5, ITEM_ID + 1, PRICE + 300));
		bids.add(new Bid(BUYER_ORG_ID + 6, ITEM_ID + 1, PRICE));
		bids.add(new Bid(BUYER_ORG_ID + 7, ITEM_ID + 1, PRICE + 900));

		bids.add(new Bid(BUYER_ORG_ID + 1, ITEM_ID + 2, PRICE + 20));
		bids.add(new Bid(BUYER_ORG_ID + 2, ITEM_ID + 2, PRICE + 10));
		bids.add(new Bid(BUYER_ORG_ID + 3, ITEM_ID + 2, PRICE + 50));
		bids.add(new Bid(BUYER_ORG_ID + 4, ITEM_ID + 2, PRICE + 1));
		bids.add(new Bid(BUYER_ORG_ID + 5, ITEM_ID + 2, PRICE + 9));

		bids.add(new Bid(BUYER_ORG_ID + 1, ITEM_ID + 3, PRICE + 200));
		bids.add(new Bid(BUYER_ORG_ID + 2, ITEM_ID + 3, PRICE + 10));
		bids.add(new Bid(BUYER_ORG_ID + 3, ITEM_ID + 3, PRICE + 50));
		bids.add(new Bid(BUYER_ORG_ID + 4, ITEM_ID + 3, PRICE + 1));
		bids.add(new Bid(BUYER_ORG_ID + 5, ITEM_ID + 3, PRICE + 9));

		bids.add(new Bid(BUYER_ORG_ID + 1, ITEM_ID + 4, PRICE + 20));
		bids.add(new Bid(BUYER_ORG_ID + 2, ITEM_ID + 4, PRICE + 10));
		bids.add(new Bid(BUYER_ORG_ID + 3, ITEM_ID + 4, PRICE + 50));
		bids.add(new Bid(BUYER_ORG_ID + 4, ITEM_ID + 4, PRICE + 100));
		bids.add(new Bid(BUYER_ORG_ID + 5, ITEM_ID + 4, PRICE + 9));

		bids.add(new Bid(BUYER_ORG_ID + 1, ITEM_ID + 5, PRICE + 20));
		bids.add(new Bid(BUYER_ORG_ID + 2, ITEM_ID + 5, PRICE + 1000));
		bids.add(new Bid(BUYER_ORG_ID + 3, ITEM_ID + 5, PRICE + 50));
		bids.add(new Bid(BUYER_ORG_ID + 4, ITEM_ID + 5, PRICE));
		bids.add(new Bid(BUYER_ORG_ID + 5, ITEM_ID + 5, PRICE + 9));

		client.bid(bids);

		try {
			client.switchEpoch();
		} catch (CertainMarketException e) {
			e.printStackTrace();
		}

		itemInStore = client.queryItems();

		assertTrue(itemInStore.isEmpty());

	}

	/*
	 * Test concurrency- C1 invokes add items C2 invokes query items the items
	 * should be as though they have not been added by C1 or all the items have
	 * been added(all or nothing)
	 */
	@Test
	public void testAddQueryConcurrency() throws CertainMarketException {
		Set<Item> items = new HashSet<Item>();
		items.add(new Item(ITEM_ID + NUM_OPS, ITEM_DESCRIPTION2, SELLER_ID + 90));
		items.add(new Item(ITEM_ID + NUM_THREADS, ITEM_DESCRIPTION2,
				SELLER_ID + 91));

		// C1 adds items
		Thread t1 = new Thread(new addItemsRunnable(NUM_OPS, items));

		// C2 queries items
		Thread t2 = new Thread(new queryItemsRunnable(NUM_OPS, items));
		t1.start();
		t2.start();

		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			fail();
		}
		// check that all items were added or none were added
		List<Item> itemsInStore = new ArrayList<Item>();
		itemsInStore = client.queryItems();
		assertTrue(items.size() == itemsInStore.size() && ERROR == false);

	}

	// Client C1 adds items
	public class addItemsRunnable implements Runnable {
		private int numOperations;
		private Set<Item> itemList;

		public addItemsRunnable(int numOperations, Set<Item> items) {
			this.numOperations = numOperations;
			this.itemList = items;
		}

		public void run() {
			try {
				for (int i = 0; i < numOperations; i++)
					client.addItems(itemList);
			} catch (CertainMarketException e) {
				e.printStackTrace();
			}
		}
	}

	// Client C2 queries items
	public class queryItemsRunnable implements Runnable {
		private int numOperations;
		private Set<Item> itemList;

		public queryItemsRunnable(int numOperations, Set<Item> items) {
			this.numOperations = numOperations;
			this.itemList = items;
		}

		public void run() {
			List<Item> itemsInStore;
			try {
				for (int i = 0; i < numOperations; i++) {
					itemsInStore = client.queryItems();
					// store should be either empty or have the items
					// added but not in between
					if (!(itemsInStore.size() == itemList.size()
							|| itemsInStore.containsAll(itemList)
							|| itemsInStore.isEmpty() || itemsInStore.size() == 0))
						ERROR = true;
				}
			} catch (CertainMarketException e) {
				e.printStackTrace();
			}
		}

	}

	/*
	 * C1 invokes switch epoch,C2 invokes query items state of the market should
	 * be as though the market is empty or with added items
	 */
	@Test
	public void testQueryAndSwitchEpochConcurrency()
			throws CertainMarketException {
		Set<Item> items = new HashSet<Item>();
		items.add(new Item(ITEM_ID + 1, ITEM_DESCRIPTION2, SELLER_ID + NUM_OPS));
		items.add(new Item(ITEM_ID + 2, ITEM_DESCRIPTION2, SELLER_ID + NUM_OPS));
		client.addItems(items);
		// C1 switches epoch
		Thread t1 = new Thread(new switchEpochRunnable(NUM_OPS));

		// C2 queries items in epoch
		Thread t2 = new Thread(new queryItemsEpochRunnable(NUM_OPS, items));
		t1.start();
		t2.start();

		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			fail();
		}
		// check that all items were added or the epoch was switched
		List<Item> itemsInStore = new ArrayList<Item>();
		itemsInStore = client.queryItems();
		assertTrue((itemsInStore.isEmpty() || itemsInStore.containsAll(items))
				&& ERROR == false);

	}

	// C1 switches the epoch
	public class switchEpochRunnable implements Runnable {
		private int numOperations;

		public switchEpochRunnable(int numOperations) {
			this.numOperations = numOperations;
		}

		public void run() {
			try {
				for (int i = 0; i < numOperations; i++)
					client.switchEpoch();
			} catch (CertainMarketException e) {
				e.printStackTrace();
			}
		}
	}

	// Client C2 queries items
	public class queryItemsEpochRunnable implements Runnable {
		private int numOperations;
		private Set<Item> itemList;

		public queryItemsEpochRunnable(int numOperations, Set<Item> items) {
			this.numOperations = numOperations;
			this.itemList = items;
		}

		public void run() {
			List<Item> itemsInStore;
			try {
				for (int i = 0; i < numOperations; i++) {
					itemsInStore = client.queryItems();
					// store should be either empty or have the items
					// added but not in between
					if (!(itemsInStore.size() == itemList.size()
							|| itemsInStore.containsAll(itemList)
							|| itemsInStore.isEmpty() || itemsInStore.size() == 0))
						ERROR = true;
				}
			} catch (CertainMarketException e) {
				e.printStackTrace();
			}
		}

	}

	/*
	 * C1 invokes switch epoch,C2 invokes add items, C3 invokes query items
	 * state of the market should be as though the market is empty or with added
	 * items
	 */
	@Test
	public void testAddQuerySwitchEpochConcurrency()
			throws CertainMarketException {
		Set<Item> items = new HashSet<Item>();
		items.add(new Item(ITEM_ID + 1, ITEM_DESCRIPTION2, SELLER_ID + NUM_OPS));
		items.add(new Item(ITEM_ID + 2, ITEM_DESCRIPTION2, SELLER_ID + NUM_OPS));

		// C1 switches epoch
		Thread t1 = new Thread(new addItemsRunnable(NUM_OPS, items));

		// C2 queries items in epoch
		Thread t2 = new Thread(new switchEpochRunnable(NUM_OPS));

		// C3 adds items
		Thread t3 = new Thread(new queryItemsEpochRunnable(NUM_OPS, items));

		t1.start();
		t2.start();
		t3.start();
		try {
			t1.join();
			t2.join();
			t3.join();
		} catch (InterruptedException e) {
			fail();
		}
		// check that all items were added or the epoch was switched
		List<Item> itemsInStore = new ArrayList<Item>();
		itemsInStore = client.queryItems();
		assertTrue((itemsInStore.isEmpty() || itemsInStore.containsAll(items))
				&& ERROR == false);

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// clear the current epoch
		client.switchEpoch();
		if (!localTest) {
			((CertainMarketHTTPProxy) client).stop();
		}
	}
}
