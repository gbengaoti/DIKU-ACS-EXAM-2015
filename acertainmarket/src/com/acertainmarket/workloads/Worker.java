package com.acertainmarket.workloads;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;

import com.acertainmarket.business.Bid;
import com.acertainmarket.business.Item;
import com.acertainmarket.utils.CertainMarketException;

public class Worker implements Callable<WorkerRunResult> {
	private WorkloadConfiguration configuration = null;
	private int numSuccessfulFrequentInteraction = 0;
	private int numTotalFrequentInteraction = 0;
	private long timeForFreqRunsInNanoSecs = 0;

	public Worker(WorkloadConfiguration config) {
		configuration = config;
	}

	/**
	 * Run the interactions using the configured distributions
	 * 
	 * Updates the counts of total runs and successful runs
	 * 
	 * @param selectInteraction
	 * @return
	 */
	private boolean runInteraction(float selectInteraction) {
		long startTimeFreqIntInNanoSecs = 0;
		long endTimeFreqIntInNanoSecs = 0;

		try {
			if (selectInteraction < configuration
					.getPercentAddItemsInteraction()) {
				runAddItemsInteraction();
			} else if (selectInteraction < configuration
					.getPercentQueryItemsInteraction()) {
				runQueryItemsInteraction();
			} else {
				numTotalFrequentInteraction++;
				// start timing
				startTimeFreqIntInNanoSecs = System.nanoTime();
				runFrequentBidInteraction();
				// end timing
				endTimeFreqIntInNanoSecs = System.nanoTime();
				timeForFreqRunsInNanoSecs += (endTimeFreqIntInNanoSecs - startTimeFreqIntInNanoSecs);
				numSuccessfulFrequentInteraction++;
			}
		} catch (CertainMarketException ex) {
			return false;
		}
		return true;
	}

	@Override
	public WorkerRunResult call() throws Exception {
		int count = 1;
		long startTimeInNanoSecs = 0;
		long endTimeInNanoSecs = 0;
		int successfulInteractions = 0;

		Random rand = new Random();
		float selectInteraction;

		// Perform the warmup runs
		while (count++ <= configuration.getWarmUpRuns()) {
			selectInteraction = rand.nextFloat() * 100f;
			runInteraction(selectInteraction);
		}

		count = 1;
		numTotalFrequentInteraction = 0;
		numSuccessfulFrequentInteraction = 0;

		// Perform the actual runs
		startTimeInNanoSecs = System.nanoTime();
		while (count++ <= configuration.getNumActualRuns()) {
			selectInteraction = rand.nextFloat() * 100f;
			if (runInteraction(selectInteraction)) {
				successfulInteractions++;
			}
		}
		endTimeInNanoSecs = System.nanoTime();
		long timeForRunsInNanoSecs = (endTimeInNanoSecs - startTimeInNanoSecs);
		return new WorkerRunResult(successfulInteractions,
				timeForRunsInNanoSecs, timeForFreqRunsInNanoSecs,
				configuration.getNumActualRuns(),
				numSuccessfulFrequentInteraction, numTotalFrequentInteraction);
	}

	private void runFrequentBidInteraction() throws CertainMarketException {
		int numTimesToBid = configuration.getNumTimesToBid();
		// Bid on items in the store
		List<Item> itemsInStore = configuration.getMarket().queryItems();
		Set<Bid> toBid = configuration.getItemBidGenerator().generateSetOfBids(
				itemsInStore, numTimesToBid);
		configuration.getMarket().bid(toBid);
	}

	private void runQueryItemsInteraction() throws CertainMarketException {
		configuration.getMarket().queryItems();

	}

	private void runAddItemsInteraction() throws CertainMarketException {
		int numItemsToAdd = configuration.getNumItemsToAdd();
		Set<Item> itemsToAdd = configuration.getItemBidGenerator()
				.generateSetOfItems(numItemsToAdd);

		// check if items are already in store
		Set<Item> toAdd = new HashSet<Item>();
		List<Item> itemsInStore = configuration.getMarket().queryItems();
		// get ItemIds of items in store
		List<Integer> itemsIdInStore = new ArrayList<Integer>();
		for (Item item : itemsInStore) {
			itemsIdInStore.add(item.getItemID());
		}
		for (Item item : itemsToAdd) {
			if (!(itemsIdInStore.contains(item.getItemID()))) {
				toAdd.add(item);
			}
		}
		configuration.getMarket().addItems(toAdd);
	}

}
