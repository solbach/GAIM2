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
    /* The strategy is to choose n random individuals. Since we initialized everything
     * randomly we can just pick the first n */
    public CGaimConnection[] getMigrants(int nMigrants) {
    	
    	CGaimConnection[] migrants;
    	migrants = new CGaimConnection[nMigrants];
    	
    	//migrants[0] = connections.clone()[getFittestIndex()];
        
//        printConnections(connections);
            	
        for (int i = 0; i < nMigrants; i++) {
            /* Make sure that you clone the element */
            migrants[i] = connections.clone()[i];
        }
        
        return migrants;
    }

    
    
	private void printConnections(CGaimConnection[] c) 
	{
		for(int i = 0; i < c.length; i++)
		{
			System.out.println(c[i]);
		}
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

	public void setMigrants(CGaimConnection[] migrants) {
		CGaimConnection[] n = new CGaimConnection[connections.length + migrants.length];

		/* add migrants at the end of the connection array */
		for (int i = 0; i < migrants.length; i++) {
			connections[connections.length-(1+i)] = migrants.clone()[i];
		}

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