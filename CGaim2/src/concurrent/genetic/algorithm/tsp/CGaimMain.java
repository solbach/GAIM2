package concurrent.genetic.algorithm.tsp;

import java.util.concurrent.ThreadLocalRandom;

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
        // Evolve population for 100 generations
        pop = CGaim.evolvePopulation(pop);        
        while(bestFitness > 900)
        {
            pop = CGaim.evolvePopulation(pop);
            bestFitness = pop.getFittest().getDistance();
            
            if(counter%5 == 0)
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