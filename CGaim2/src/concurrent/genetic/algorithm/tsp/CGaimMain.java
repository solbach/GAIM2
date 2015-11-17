package concurrent.genetic.algorithm.tsp;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
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

		System.setOut(new PrintStream(new FileOutputStream( new File(path)))); 
		
		System.out.println("-- Genetic Algorithm with Island Migration -- \n");

		final int numberCities = 100;
		final int mapBoundaries = 1000;
		final int numberIslands = 12;
		final int nMigrants = 5;
		final int popSize = 500; // for each island
		final int epochL = 1000;
		final int stopCriterion = 8000;

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

		System.out.println("Initialized with: ");
		System.out.println(" \t " + numberIslands + " Islands / Threads");
		System.out.println(" \t " + popSize + " Individuals ea island");
		System.out.println(" \t " + numberCities + " Cities");
		System.out.println(" \t " + mapBoundaries + "x" + mapBoundaries + " Map");
		System.out.println(" \t " + stopCriterion + " Fitness Threshold \n");

		System.out.println("Threads wait at the cyclic barrier \n");

		System.out.println("Genetic Algorithm evolves...");

		double bestFitness = Double.MAX_VALUE;
		int bestIsland = 0;

		/* Create the Threads */
		for (int i = 0; i < numberIslands; i++) {
			/* Using Threads */
			threads.get(i).start();
		}
		
		/* Master Loop STARTS HERE */		
		while (bestFitness > stopCriterion) {

			barrierMigration.await();
			
			/* check best individual on each island */
			for (int i = 0; i < numberIslands; i++) {
				if (islands.get(i).bestFitness() < bestFitness) {
					bestFitness = islands.get(i).bestFitness();
					bestIsland = i + 1;
				}
			}

			System.out.println("\tBest Fitness is on Island " + bestIsland + " (" 
					+ islands.get(bestIsland - 1).bestFitness() 
					+ ") - Generation: "
					+ islands.get(bestIsland - 1).getCurrentGeneration());
			
			for (int i = 0; i < numberIslands; i++) {
				System.out.print("\t" + islands.get(i).bestFitness() + " ");
			}

			System.out.print("\n\n");

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
			barrierMigration.reset();
			barrier.reset();
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
		
		System.out.println("Final distance: " + islands.get(bestIsland - 1).getPopulation().getFittest().getDistance());
		System.out.println("Execution Time in sec: " + elapsedSeconds);

		System.out.println("Best estimated Solution:");
		System.out.println(islands.get(bestIsland - 1).getPopulation().getFittest());
		System.exit(0);		
	}
}