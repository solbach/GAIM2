package concurrent.genetic.algorithm.tsp;

import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.List;

public class CGaimMain {
	
	private List<CGaimPopulation> islands = new ArrayList<CGaimPopulation>();
	
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
    	final int numberIslands = 3;
    	
        // Create and add our cities
    	int x, y;
    	for(int i = 0; i < numberCities; i++)
    	{
    		
    		x = randInt(0, mapBoundaries);
    		y = randInt(0, mapBoundaries);
    				
            CGaimDestination city = new CGaimDestination(x, y);
            CGaimDestinationPool.addCity(city);    		
    	}

    	
        // Initialize population
        CGaimPopulation pop = new CGaimPopulation(100, true);
        System.out.println("Initial distance: " + pop.getFittest().getDistance());
        
        int bestFitness = 999999;
        int counter = 1;
        
        /* populate islands */
        for(int i = 0; i < numberIslands; i++)
        {
        	CGaim island = new CGaim();
        }
        
        
        
        CGaim island = new CGaim(); 
        
        pop = island.evolvePopulation(pop);        
        while(bestFitness > 900)
        {
            pop = island.evolvePopulation(pop);
            bestFitness = pop.getFittest().getDistance();
            
            if(counter%10 == 0)
            	System.out.println("Best Fitness at Generation: " + counter + " is \t" +  bestFitness);
            counter++;
        }

        // Print final results
        System.out.println("Finished");
        System.out.println("Final distance: " + pop.getFittest().getDistance());
        System.out.println("Solution:");
        System.out.println(pop.getFittest());
    }
}