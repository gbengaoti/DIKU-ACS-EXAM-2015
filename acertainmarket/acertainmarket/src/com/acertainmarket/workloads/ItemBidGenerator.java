package com.acertainmarket.workloads;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.acertainmarket.business.Bid;
import com.acertainmarket.business.Item;

public class ItemBidGenerator {
	public Set<Item> generateSetOfItems(int num) {
		Random rand = new Random();
		int Item_Id = 3044;
		int Seller_Id = 9000;
		String Item_Description = "A pen";
		Set<Item> itemsToAdd = new HashSet<Item>();

		for (int i = 0; i < num; i++) {
			int idforItem = rand.nextInt(5646388) + i;
			int idforSeller = rand.nextInt(1000) + 20;
			Item item = new Item(Item_Id + idforItem, Item_Description + i,
					Seller_Id + idforSeller);
			itemsToAdd.add(item);
		}

		return itemsToAdd;
	}

	public Set<Bid> generateSetOfBids(List<Item> inStore, int num) {
		Random rand = new Random();
		List<Integer> allItemIds = new ArrayList<Integer>();
		// get all item IDs
		for (Item item : inStore) {
			allItemIds.add(item.getItemID());
		}
		Set<Bid> allBids = new HashSet<Bid>();
		int sizeOfSet = inStore.size();
		for (int i = 0; i < num; i++) {
			int index = rand.nextInt(sizeOfSet);
			int idforOrganization = rand.nextInt(1000) + 1;
			float price = rand.nextFloat() + 100;
			Bid bid = new Bid(idforOrganization, allItemIds.get(index), price);
			allBids.add(bid);
		}

		return allBids;
	}
}
