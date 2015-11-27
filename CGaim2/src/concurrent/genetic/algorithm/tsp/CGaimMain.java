package concurrent.genetic.algorithm.tsp;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;

public class CGaimMain {

	static List<CGaimIsland> islands = new ArrayList<CGaimIsland>();
	static List<Thread> threads = new ArrayList<Thread>();

	private static int randInt(int min, int max) {

		if (max < min) {
			int temp = max;
			max = min;
			min = temp;
		}

		int result = ThreadLocalRandom.current().nextInt((max - min) + 1) + min;
		// System.out.println(min +"/"+ max + " - " + result);

		return result;
	}

	public static void main(String[] args) throws InterruptedException, BrokenBarrierException, TimeoutException, IOException {
		/*Some Performance Testing Utils*/
		double now = System.nanoTime();
		java.util.Date date= new java.util.Date();
		String path = "out_" + new Timestamp(date.getTime()) + ".txt";
		String outputString = "";

		System.setOut(new PrintStream(new FileOutputStream( new File(path)))); 
		
//		System.out.println("-- Genetic Algorithm with Island Migration -- \n");
		outputString += "-- Genetic Algorithm with Island Migration -- \n";

		final int numberCities = 100;
		final int mapBoundaries = 100;
		final int numberIslands = 1; // => Number threads used
		final int nMigrants = 0; // 1%
		final int popSize = 2000; // for each island
		final int epochL = 100;
		final int realEpochL = 1; // This value stops migration due to epochL. only if a fakeEpochL is reached a migration is executed
		final int stopCriterion = 910;
		final int maxEpochs = 500000;
		int currentEpoch = 0; //This variable works as a counter

		CGaimDestinationPool pool = new CGaimDestinationPool();

		/*
		 * create an cyclic barrier to make sure that all threads start at
		 * exactly the same time
		 */
		/* This Barrier will wait until all threads (numberIslands) are ready */
		final CyclicBarrier barrier = new CyclicBarrier(numberIslands);
		final CyclicBarrier barrierMigration = new CyclicBarrier(numberIslands+1);

		// Create and add our cities
		int x, y;
		for (int i = 0; i < numberCities; i++) {
			x = randInt(0, mapBoundaries);
			y = randInt(0, mapBoundaries);

			/*
			 * Destinations class is static, because it will not change and is
			 * for every island at every time the same
			 */
			CGaimDestination city = new CGaimDestination(x, y);
			pool.addCity(city);
		}
		
		/* Create Islands and Threads */
		for (int i = 0; i < numberIslands; i++) {
			CGaimIsland island = new CGaimIsland(pool, nMigrants, popSize, epochL, i + 1, barrier, barrierMigration);
			islands.add(island);

			/* create thread and associate it with an island */
			Thread t = new Thread(island);

			threads.add(t);

		}
		
		outputString += "Initialized with: ";
		outputString += "\n ";
		outputString += " \t " + numberIslands + " Islands / Threads";
		outputString += "\n ";
		outputString += " \t " + popSize + " Individuals ea island";
		outputString += "\n ";
		outputString += " \t " + numberCities + " Cities";
		outputString += "\n ";
		outputString += " \t " + mapBoundaries + "x" + mapBoundaries + " Map";
		outputString += "\n ";
		outputString += " \t " + stopCriterion + " Fitness Threshold \n";
		outputString += " \t " + epochL + " Epoch Length \n";
		outputString += " \t " + realEpochL + " Epoch Length actual\n";
		outputString += " \t " + nMigrants + " Migrants \n";

		outputString += "Threads wait at the cyclic barrier \n";

		outputString += "Genetic Algorithm evolves...";
		outputString += "\n ";

		double bestFitness = Double.MAX_VALUE;
		int bestIsland = 0;

		/* Create the Threads */
		for (int i = 0; i < numberIslands; i++) {
			/* Using Threads */
			threads.get(i).start();
		}
		
		/* Flush some details to the file */
		System.out.println(outputString);
		
		/* reset logging variable */
		outputString = "";
		
		/* Master Loop STARTS HERE */		
		while ( (bestFitness > stopCriterion) && (currentEpoch < maxEpochs) ) {
			currentEpoch++;
			barrierMigration.await();
			
			/* check best individual on each island */
			for (int i = 0; i < numberIslands; i++) {
				if (islands.get(i).bestFitness() < bestFitness) {
					bestFitness = islands.get(i).bestFitness();
					bestIsland = i + 1;
				}
			}
			
			for (int i = 0; i < numberIslands; i++) {
				if(i == numberIslands-1)
				{
					outputString += islands.get(i).bestFitness();
				}
				else{
					outputString += islands.get(i).bestFitness() + ",";					
				}
			}

			if( (currentEpoch % realEpochL) == 0)
			{
				/* perform island migration (as mentioned in the paper: cyclic) */
				for (int i = 0; i < numberIslands; i++) {
					/* get migrants from ith Island */
					CGaimConnection[] migrants = islands.get(i).getMigrants();
	
					/* set migrants on ith Island or 0 Island */
					if (i < numberIslands - 1) {
						islands.get(i + 1).setMigrants(migrants.clone());
					} else {
						islands.get(0).setMigrants(migrants.clone());
					}
	
				}
			}

			//barrierMigration.reset();
			System.out.println(outputString);
			outputString = "";
		}
		/* Master Loop ENDS HERE */

		for (int i = 0; i < numberIslands; i++) {
			/* create thread and associate it with an island */
			islands.get(i).setExectureThread(false);
		}
		
		/* one last look up */
		/* check best individual on each island */
		for (int i = 0; i < numberIslands; i++) {
			if (islands.get(i).bestFitness() < bestFitness) {
				bestFitness = islands.get(i).bestFitness();
				bestIsland = i + 1;
			}
		}

	    double elapsedSeconds = (System.nanoTime() - now)/1e9; 
		
	    outputString += "\n########################";
	    outputString += "\n##### Terminated #######";
	    outputString += "\n########################\n\n";
	    outputString += "Final distance: " + islands.get(bestIsland - 1).getPopulation().getFittest().getDistance();
		outputString += "\n ";
	    outputString += "Execution Time in sec: " + elapsedSeconds;
		outputString += "\n ";
	    outputString += "Best estimated Solution:";
		outputString += "\n ";
	    outputString += islands.get(bestIsland - 1).getPopulation().getFittest();
		outputString += "\n ";
		System.out.println(outputString);
		
		System.exit(0);		
	}
}