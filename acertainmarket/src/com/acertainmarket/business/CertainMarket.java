/**
 * 
 */
package com.acertainmarket.business;

import com.acertainmarket.interfaces.AuctionMarket;
import com.acertainmarket.utils.CertainMarketConstants;
import com.acertainmarket.utils.CertainMarketException;
import com.acertainmarket.utils.myMap;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * @author User
 *
 */

public class CertainMarket implements AuctionMarket {
	private Set<Item> allItems = null;
	private Map<myMap, Float> allBids = null;
	private Set<Integer> allBuyerIds = null;
	private final ReentrantReadWriteLock accessLock = new ReentrantReadWriteLock(true);
	private final Lock rReadLock = accessLock.readLock();
	private final Lock wWriteLock = accessLock.writeLock();

	public CertainMarket() {
		allItems = new HashSet<Item>();
		allBuyerIds = new HashSet<Integer>();
		allBids = new HashMap<myMap, Float>();
	}

	@Override
	public void addItems(Set<Item> items) throws CertainMarketException {
		wWriteLock.lock();
		try {
			// test that inputs are valid
			// check that itemIDs are non negative
			// check that items are not already in the market
			if (items == null) {
				throw new CertainMarketException(
						CertainMarketConstants.NULL_INPUT);
			}
			Iterator<Item> it = items.iterator();
			for (Item item : items) {
				item = it.next();
				if (!(item.getItemID() >= 0)
						|| (!(item.getSellerOrganizationID() >= 0))
						|| (allItems.contains(item))
						|| (item.getItemDescription().equals(""))
						|| (item.getItemDescription() == null))
					throw new CertainMarketException(
							CertainMarketConstants.INVALID);

			}
			// after performing all the checks on the items
			// add all items
			for (Item item : items) {
				allItems.add(item);
			}
		} finally {
			wWriteLock.unlock();
		}

	}

	@Override
	public List<Item> queryItems() {
		rReadLock.lock();
		try {
			List<Item> itemsInEpoch = allItems.stream().collect(
					Collectors.toList());
			return itemsInEpoch;
		} finally {
			rReadLock.unlock();
		}
	}

	@Override
	public void bid(Set<Bid> bids) throws CertainMarketException {
		wWriteLock.lock();
		try {
			int buyerID;
			int itemID;
			float price;
			Set<Integer> allItemIds = new HashSet<Integer>();
			// get all item IDs
			for (Item item : allItems) {
				allItemIds.add(item.getItemID());
			}
			// checks inputs
			if (bids == null) {
				throw new CertainMarketException(
						CertainMarketConstants.NULL_INPUT);
			}
			// check that item is in the market
			for (Bid bid : bids) {
				if ((bid.getBidAmount() < 0)
						|| (bid.getBuyerOrganizationID() < 0)
						|| (!(allItemIds.contains(bid.getItemID()))))
					throw new CertainMarketException(
							CertainMarketConstants.INVALID);
			}

			// add Bids
			for (Bid bid : bids) {
				itemID = bid.getItemID();
				buyerID = bid.getBuyerOrganizationID();
				price = bid.getBidAmount();
				myMap mapkeys = new myMap();
				mapkeys.setKey1(itemID);
				mapkeys.setKey2(buyerID);
				allBuyerIds.add(bid.getBuyerOrganizationID());
				allBids.put(mapkeys, price);
			}
		} finally {
			wWriteLock.unlock();
		}
	}

	@Override
	public void switchEpoch() throws CertainMarketException {
		wWriteLock.lock();
		try {
			try {
				Map<myMap, Float> sortedBids = sortMapByKey(allBids);
				Float bidPrice;
				BufferedWriter out = null;
				FileWriter fstream;
				try {
					fstream = new FileWriter("bidWinners.txt", true);
					out = new BufferedWriter(fstream);
					out.write("ITEM ID" + ",");
					out.write("ITEM DESCRIPTION" + ",");
					out.write("SELLER ORG. ID" + ",");
					out.write("BUYER ORG. ID" + ",");
					out.write("BID AMOUNT" + "\n");
					out.newLine();

					for (Item item : allItems) {
						Map<myMap, Float> itemAndBids = new LinkedHashMap<myMap, Float>();
						for (Integer buyerId : allBuyerIds) {
							myMap mapkeys = new myMap();
							mapkeys.setKey1(item.getItemID());
							mapkeys.setKey2(buyerId);
							// check if item was bid on
							if (sortedBids.containsKey(mapkeys)) {
								bidPrice = sortedBids.get(mapkeys);
								itemAndBids.put(mapkeys, bidPrice);
							}
						}
						// if an item was not bid on
						if (!(itemAndBids.isEmpty())) {
							Map<myMap, Float> newItemAndBids = sortMapByValue(itemAndBids);
							Object firstKey = newItemAndBids.keySet().toArray()[0];
							Object BuyerOrgId = ((myMap) firstKey).getKey2();
							Float WinnerPrice = newItemAndBids.get(firstKey);
							// Write the results in a file (make durable)
							out.write(item.getItemID() + ",");
							out.write(item.getItemDescription() + ",");
							out.write(item.getSellerOrganizationID() + ",");
							out.write(BuyerOrgId + ",");
							out.write(WinnerPrice + "\n");
							out.newLine();
						} else {
							out.write(item.getItemID() + " ,");
							out.write(item.getItemDescription() + ",");
							out.write(item.getSellerOrganizationID() + ",");
							out.write("Empty Bid");
							out.newLine();
						}
					}

				} catch (IOException e) {
					System.err.println("Error: " + e.getMessage());
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			} finally {
				allBids.clear();
				allBuyerIds.clear();
				allItems.clear();
			}
		} finally {
			wWriteLock.unlock();
		}
	}

	private static Map<myMap, Float> sortMapByValue(Map<myMap, Float> allBids2) {

		Set<Entry<myMap, Float>> mapEntries = allBids2.entrySet();
		List<Entry<myMap, Float>> aList = new LinkedList<Entry<myMap, Float>>(
				mapEntries);

		// sorting the List
		Collections.sort(aList, new Comparator<Entry<myMap, Float>>() {

			public int compare(Entry<myMap, Float> ele1,
					Entry<myMap, Float> ele2) {
				Float temp1 = ele1.getValue();
				Float temp2 = ele2.getValue();
				return temp2.compareTo(temp1);
			}

		});
		Map<myMap, Float> aMap2 = new LinkedHashMap<myMap, Float>();
		for (Entry<myMap, Float> entry : aList) {
			aMap2.put(entry.getKey(), entry.getValue());

		}

		return aMap2;
	}

	private static Map<myMap, Float> sortMapByKey(Map<myMap, Float> allBids2) {

		Set<Entry<myMap, Float>> mapEntries = allBids2.entrySet();
		List<Entry<myMap, Float>> aList = new LinkedList<Entry<myMap, Float>>(
				mapEntries);

		// sorting the List
		Collections.sort(aList, new Comparator<Entry<myMap, Float>>() {

			public int compare(Entry<myMap, Float> ele1,
					Entry<myMap, Float> ele2) {
				Integer temp1 = (int) ele1.getKey().getKey1();
				Integer temp2 = (int) ele2.getKey().getKey1();
				return temp1.compareTo(temp2);
			}

		});
		Map<myMap, Float> aMap2 = new LinkedHashMap<myMap, Float>();
		for (Entry<myMap, Float> entry : aList) {
			aMap2.put(entry.getKey(), entry.getValue());
		}

		return aMap2;
	}
}
