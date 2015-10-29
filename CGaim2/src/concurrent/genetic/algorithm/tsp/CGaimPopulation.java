package concurrent.genetic.algorithm.tsp;


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

    // Gets population size
    public int populationSize() {
        return connections.length;
    }
}