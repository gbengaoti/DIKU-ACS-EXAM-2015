package com.acertainmarket.workloads;

public class WorkerRunResult {
	private int successfulInteractions; // total number of successful
										// interactions
	private int totalRuns; // total number of interactions run
	private long elapsedTimeInNanoSecs; // total time taken to run all
										// interactions
	private int successfulFrequentInteractionRuns;
	private long timeForFreqRunsInNanoSecs; // total time for frequent
											// interactions
	private int totalFrequentInteractionRuns;

	public WorkerRunResult(int successfulInteractions,
			long elapsedTimeInNanoSecs, long timeForFreqRunsInNanoSecs,
			int totalRuns, int successfulFrequentInteractionRuns,
			int totalFrequentInteractionRuns) {
		this.setSuccessfulInteractions(successfulInteractions);
		this.setElapsedTimeInNanoSecs(elapsedTimeInNanoSecs);
		this.setTimeForFreqRunsInNanoSecs(timeForFreqRunsInNanoSecs);
		this.setTotalRuns(totalRuns);
		this.setSuccessfulFrequentInteractionRuns(successfulFrequentInteractionRuns);
		this.setTotalFrequentInteractionRuns(totalFrequentInteractionRuns);
	}

	public int getSuccessfulInteractions() {
		return successfulInteractions;
	}

	public void setSuccessfulInteractions(int successfulInteractions) {
		this.successfulInteractions = successfulInteractions;
	}

	public int getTotalRuns() {
		return totalRuns;
	}

	public void setTotalRuns(int totalRuns) {
		this.totalRuns = totalRuns;
	}

	public long getElapsedTimeInNanoSecs() {
		return elapsedTimeInNanoSecs;
	}

	public void setElapsedTimeInNanoSecs(long elapsedTimeInNanoSecs) {
		this.elapsedTimeInNanoSecs = elapsedTimeInNanoSecs;
	}

	public void setSuccessfulFrequentInteractionRuns(
			int successfulFrequentInteractionRuns) {
		this.successfulFrequentInteractionRuns = successfulFrequentInteractionRuns;
	}

	public int getSuccessfulFrequentInteractionRuns() {
		return successfulFrequentInteractionRuns;
	}

	public int getTotalFrequentInteractionRuns() {
		return totalFrequentInteractionRuns;
	}

	public void setTotalFrequentInteractionRuns(int totalFrequentInteractionRuns) {
		this.totalFrequentInteractionRuns = totalFrequentInteractionRuns;
	}

	public long getTimeForFreqRunsInNanoSecs() {
		return timeForFreqRunsInNanoSecs;
	}

	public void setTimeForFreqRunsInNanoSecs(long timeForFreqRunsInNanoSecs) {
		this.timeForFreqRunsInNanoSecs = timeForFreqRunsInNanoSecs;
	}

}
