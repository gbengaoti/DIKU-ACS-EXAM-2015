package com.acertainmarket.workloads;

import com.acertainmarket.interfaces.AuctionMarket;

/**
 * 
 * WorkloadConfiguration represents the configuration parameters to be used by
 * Workers class for running the workloads
 * 
 */
public class WorkloadConfiguration {
	private int numItemsToAdd = 5;
	private int numTimesToBid = 2;
	private int warmUpRuns = 100;
	private int numActualRuns = 200;
	private float percentAddItemsInteraction = 10f;
	private float percentQueryItemsInteraction = 10f;
	private float percentFrequentBidInteraction = 70f;
	private ItemBidGenerator itemBidGenerator = null;
	private AuctionMarket market = null;

	public WorkloadConfiguration(AuctionMarket market) throws Exception {
		// Create a new one so that it is not shared
		itemBidGenerator = new ItemBidGenerator();
		this.market = market;
	}

	public int getNumItemsToAdd() {
		return numItemsToAdd;
	}

	public void setNumItemsToAdd(int numItemsToAdd) {
		this.numItemsToAdd = numItemsToAdd;
	}

	public ItemBidGenerator getItemBidGenerator() {
		return itemBidGenerator;
	}

	public void setItemBidGenerator(ItemBidGenerator itemBidGenerator) {
		this.itemBidGenerator = itemBidGenerator;
	}

	public AuctionMarket getMarket() {
		return market;
	}

	public void setMarket(AuctionMarket market) {
		this.market = market;
	}

	public int getWarmUpRuns() {
		return warmUpRuns;
	}

	public void setWarmUpRuns(int warmUpRuns) {
		this.warmUpRuns = warmUpRuns;
	}

	public int getNumActualRuns() {
		return numActualRuns;
	}

	public void setNumActualRuns(int numActualRuns) {
		this.numActualRuns = numActualRuns;
	}

	public float getPercentAddItemsInteraction() {
		return percentAddItemsInteraction;
	}

	public void setPercentAddItemsInteraction(float percentAddItemsInteraction) {
		this.percentAddItemsInteraction = percentAddItemsInteraction;
	}

	public float getPercentQueryItemsInteraction() {
		return percentQueryItemsInteraction;
	}

	public void setPercentQueryItemsInteraction(
			float percentQueryItemsInteraction) {
		this.percentQueryItemsInteraction = percentQueryItemsInteraction;
	}

	public float getPercentFrequentBidInteraction() {
		return percentFrequentBidInteraction;
	}

	public void setPercentFrequentBidInteraction(
			float percentFrequentBidInteraction) {
		this.percentFrequentBidInteraction = percentFrequentBidInteraction;
	}

	public int getNumTimesToBid() {
		return numTimesToBid;
	}

	public void setNumTimesToBid(int numTimesToBid) {
		this.numTimesToBid = numTimesToBid;
	}

}
