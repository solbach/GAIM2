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
//		System.out.println(min +"/"+ max + " - " + result);
		
		return result;
	}
	
    public static void main(String[] args) {
    	
    	final int numberCities = 1000;
    	final int mapBoundaries = 10000;
    	final int numberIslands = 4;
    	final int nMigrants = 3;
    	final int popSize = 50; // for each island
    	final int epochL = 100;
    	
    	List<CGaimIsland> islands = new ArrayList<CGaimIsland>();
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

        /* Create Islands */
        for(int i = 0; i < numberIslands; i++)
        {
        	CGaimIsland island = new CGaimIsland(pool, nMigrants, popSize, epochL, i+1);
        	islands.add(island);
        }
        
        /* Start the Threads */
        for(int i = 0; i < numberIslands; i++)
        { 
        	islands.get(i).start();        	
        } 
        
        
        

        System.out.println("GOOD NIGHT");
//        int bestFitness = 999999;
//        int counter = 1;
//        
//        pop = island.evolvePopulation(pop);        
//        while(bestFitness > 900)
//        {
//            pop = island.evolvePopulation(pop);
//            bestFitness = pop.getFittest().getDistance();
//            
//            if(counter%10 == 0)
//            	System.out.println("Best Fitness at Generation: " + counter + " is \t" +  bestFitness);
//            counter++;
//        }
//
//        // Print final results
//        System.out.println("Finished");
//        System.out.println("Final distance: " + pop.getFittest().getDistance());
//        System.out.println("Solution:");
//        System.out.println(pop.getFittest());
    }
}