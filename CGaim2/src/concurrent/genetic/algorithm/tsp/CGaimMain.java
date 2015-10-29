package concurrent.genetic.algorithm.tsp;

import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.List;

public class CGaimMain {
	
	
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
	
    public static void main(String[] args) {
    	
    	System.out.println("-- Genetic Algorithm with Island Migration -- \n");
    	
    	final int numberCities = 1000;
    	final int mapBoundaries = 10000;
    	final int numberIslands = 5;
    	final int nMigrants = 5;
    	final int popSize = 90; // for each island
    	final int epochL = 70;
    	
    	List<CGaimIsland> islands = new ArrayList<CGaimIsland>();
    	List<Thread> threads = new ArrayList<Thread>();
    	
        CGaimDestinationPool pool = new CGaimDestinationPool(); 
        
        // Create and add our cities
    	int x, y;
    	for(int i = 0; i < numberCities; i++)
    	{
    		
    		x = randInt(0, mapBoundaries);
    		y = randInt(0, mapBoundaries);
    		
    		/* Destinations class is static, because it will not change
    		 * and is for every island at every time the same */
            CGaimDestination city = new CGaimDestination(x, y);   	
            pool.addCity(city);
    	}

        /* Create Islands and Threads */
        for(int i = 0; i < numberIslands; i++)
        {
        	CGaimIsland island = new CGaimIsland(pool, nMigrants, popSize, epochL, i+1);
        	islands.add(island);

        	/* create thread and associate it with an island */
        	Thread t = new Thread(island);
        	threads.add(t);
        	
        }
        
        double bestFitness = Double.MAX_VALUE;
        int bestIsland = 0;
        
        while(bestFitness > 8000)
        {
	        /* Start the Threads */
	        for(int i = 0; i < numberIslands; i++)
	        { 
	        	/* Using one Thread */

//	     	    islands.get(i).init();
//	        	islands.get(i).evolve();
	
	        	/* Using Threads */
	        	threads.get(i).start();        	
	        } 
   
	        /* Wait for Threads */
	        for(int i = 0; i < numberIslands; i++)
	        { 
		    	try {
					threads.get(i).join();
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
	        }
	        
	        /* check best individual on each island */
	        for(int i = 0; i < numberIslands; i++)
	        {
	        	if(islands.get(i).bestFitness() < bestFitness)
	        	{
	        		bestFitness = islands.get(i).bestFitness();
	        		bestIsland = i + 1;
	        	}
	        }
	        
    		System.out.println("Best Fitness Island " + bestIsland + " - " + bestFitness);
	        
	        /* perform island migration (as mentioned in the paper: cyclic) */
	        for(int i = 0; i < numberIslands; i++)
	        {
	        	/* get migrants from ith Island */
	            CGaimConnection[] migrants =  islands.get(i).getMigrants();
	            
	            /* set migrants on ith Island or 0 Island*/
	            if(i < numberIslands-1)
	            {
	            	islands.get(i+1).setMigrants(migrants.clone());
	            }else{
	            	islands.get(0).setMigrants(migrants.clone());
	            }
	            
	        }
    		
    		/* prepare for next round */
    		threads.clear();
    		for(int i = 0; i < numberIslands; i++)
    		{
    			/* create thread and associate it with an island */
            	Thread t = new Thread(islands.get(i));
            	threads.add(t);
    		}
        }
        
        
        /* one last look up */
        
        /* check best individual on each island */
        for(int i = 0; i < numberIslands; i++)
        {
        	if(islands.get(i).bestFitness() < bestFitness)
        	{
        		bestFitness = islands.get(i).bestFitness();
        		bestIsland = i + 1;
        	}
        }
        
        System.out.println("Final distance: " + islands.get(bestIsland-1).getPopulation().getFittest().getDistance());

        System.out.println("Best estimated Solution:");
        System.out.println(islands.get(bestIsland-1).getPopulation().getFittest());
    }
}