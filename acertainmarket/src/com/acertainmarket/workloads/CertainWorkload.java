package com.acertainmarket.workloads;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.acertainmarket.business.CertainMarket;
import com.acertainmarket.business.Item;
import com.acertainmarket.client.CertainMarketHTTPProxy;
import com.acertainmarket.interfaces.AuctionMarket;
import com.acertainmarket.utils.CertainMarketConstants;
import com.acertainmarket.utils.CertainMarketException;

public class CertainWorkload {
	private static final int NUM_ITEMS = 20; // number of initial books

	public static void main(String[] args) throws Exception {
		for (int numConcurrentWorkloadThreads = 1; numConcurrentWorkloadThreads <= 100; numConcurrentWorkloadThreads++) {

			String serverAddress = "http://localhost:8081";
			boolean localTest = true;

			List<WorkerRunResult> workerRunResults = new ArrayList<WorkerRunResult>();
			List<Future<WorkerRunResult>> runResults = new ArrayList<Future<WorkerRunResult>>();

			// Initialize the RPC interfaces if its not a localTest, the
			// variable is
			// overriden if the property is set
			String localTestProperty = System
					.getProperty(CertainMarketConstants.PROPERTY_KEY_LOCAL_TEST);
			localTest = (localTestProperty != null) ? Boolean
					.parseBoolean(localTestProperty) : localTest;

			AuctionMarket client = null;
			if (localTest) {
				CertainMarket market = new CertainMarket();
				client = market;
			} else {
				client = new CertainMarketHTTPProxy(serverAddress);
			}
			// start with some items in the market
			addInitialItems(client);
			ExecutorService exec = Executors
					.newFixedThreadPool(numConcurrentWorkloadThreads);

			for (int i = 0; i < numConcurrentWorkloadThreads; i++) {
				WorkloadConfiguration config = new WorkloadConfiguration(client);
				Worker workerTask = new Worker(config);
				// Keep the futures to wait for the result from the thread
				runResults.add(exec.submit(workerTask));
			}

			// Get the results from the threads using the futures returned
			for (Future<WorkerRunResult> futureRunResult : runResults) {
				WorkerRunResult runResult = futureRunResult.get(); // blocking
																	// call
				workerRunResults.add(runResult);
			}

			exec.shutdownNow(); // shutdown the executor

			// Finished initialization, stop the clients if not localTest
			if (!localTest) {
				((CertainMarketHTTPProxy) client).stop();
			}

			reportMetric(workerRunResults);
		}
	}

	public static void reportMetric(List<WorkerRunResult> workerRunResults) {

		Float throughput = 0.0f;
		for (WorkerRunResult worker : workerRunResults) {
			throughput += worker.getSuccessfulFrequentInteractionRuns()
					/ (worker.getElapsedTimeInNanoSecs() * 0.000000001f); // to
																			// seconds
		}

		// Write the results in a file
		BufferedWriter out = null;
		try {
			FileWriter fstream = new FileWriter("resultsnonRPC2.txt", true);
			out = new BufferedWriter(fstream);
			out.write(workerRunResults.size() + ",");// no of threads
			out.write(throughput + ",");
			out.newLine();
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
	}

	private static void addInitialItems(AuctionMarket client)
			throws CertainMarketException {
		Random rand = new Random();

		for (int i = 0; i < NUM_ITEMS; i++) {
			Set<Item> items = new HashSet<Item>();
			int itemId = rand.nextInt(1000) + i;
			String itemDescription = "A glassware";
			int sellerOrgId = rand.nextInt(2000) + i;
			items.add(new Item(itemId, itemDescription, sellerOrgId));
			client.addItems(items);
		}

	}

}
