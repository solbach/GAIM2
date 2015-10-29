package concurrent.genetic.algorithm.tsp;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class CGaimPopulation {

    // Holds population of tours
	
	CGaimConnection[] connections;

    // Construct a population
    public CGaimPopulation(int populationSize, boolean initialise) {
    	connections = new CGaimConnection[populationSize];
        // If we need to initialise a population of tours do so
        if (initialise) {
            // Loop and create individuals
            for (int i = 0; i < populationSize(); i++) {
            	CGaimConnection newTour = new CGaimConnection();
                newTour.generateIndividual();
                saveTour(i, newTour);
            }
        }
    }
    
    // Saves a tour
    public void saveTour(int index, CGaimConnection tour) {
    	connections[index] = tour;
    }
    
    // Gets a tour from population
    public CGaimConnection getTour(int index) {
        return connections[index];
    }

    // Gets the best tour in the population
    public CGaimConnection getFittest() {
    	CGaimConnection fittest = connections[0];
        // Loop through individuals to find fittest
        for (int i = 1; i < populationSize(); i++) {
            if (fittest.getFitness() <= getTour(i).getFitness()) {
                fittest = getTour(i);
            }
        }
        return fittest;
    }
    
    // Gets the best tour in the population
    public int getFittestIndex() {
    	int result = 0;
    	CGaimConnection fittest = connections[0];
        // Loop through individuals to find fittest
        for (int i = 1; i < populationSize(); i++) {
            if (fittest.getFitness() <= getTour(i).getFitness()) {
                fittest = getTour(i);
                result = i;
            }
        }
        return result;
    }
    
    // Get Tours which are used to Migrate
    /* The strategy is to choose the best individual
     * and n random chosen ones */
    public CGaimConnection[] getMigrants(int nMigrants) {
    	
    	CGaimConnection[] migrants;
    	migrants = new CGaimConnection[nMigrants];
    	
    	migrants[0] = connections.clone()[getFittestIndex()];

        connections = removeElement(connections, getFittestIndex());
    	
    	migrants[0].generateIndividual();
    	
        for (int i = 1; i < nMigrants; i++) {
            int index = randInt(0, populationSize());
            /* Make sure that you clone the element */
            migrants[i] = connections.clone()[index];
            
            /* And delete them from the island */
            System.out.println("BEFORE: " + connections.length);
            connections = removeElement(connections, index);
            System.out.println("AFTER: " + connections.length);
        }
        
        return migrants;
    }

	private CGaimConnection[] removeElement(CGaimConnection[] c, int index) 
	{
		CGaimConnection[] n = new CGaimConnection[c.length-1];
		
		for(int i = 0; i < n.length; i++)
		{
			if(i != index)
			{
				n[i] = c.clone()[i];
			}
		}
		
		return n;
	}

	public void setMigrants(CGaimConnection[] migrants) 
	{
		/* add migrants at the end of the connection array  */
	}
	
	
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
    
    // Gets population size
    public int populationSize() {
        return connections.length;
    }
}