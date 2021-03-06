package concurrent.genetic.algorithm.tsp;

import java.util.concurrent.ThreadLocalRandom;

public class CGaim {

    /* GA parameters */
    private  final double mutationRate = 2; // 2 in %
    private  final int tournamentSize = 10; 	// 5
    private  final boolean elitism = true;	// true

    // Evolves a population over one generation
    public  CGaimPopulation evolvePopulation(CGaimPopulation pop) {
    	CGaimPopulation newPopulation = new CGaimPopulation(pop.populationSize(), false);

        // Keep our best individual if elitism is enabled
        int elitismOffset = 0;
        if (elitism) {
            newPopulation.saveTour(0, pop.getFittest());
            elitismOffset = 1;
        }

        // Crossover population
        // Loop over the new population's size and create individuals from
        // Current population
        for (int i = elitismOffset; i < newPopulation.populationSize(); i++) {
            // Select parents
        	CGaimConnection parent1 = tournamentSelection(pop);
        	CGaimConnection parent2 = tournamentSelection(pop);
            // Crossover parents
        	CGaimConnection child = crossover(parent1, parent2);
            // Add child to new population
            newPopulation.saveTour(i, child);
        }

        // Mutate the new population a bit to add some new genetic material
        for (int i = elitismOffset; i < newPopulation.populationSize(); i++) {
            mutate(newPopulation.getTour(i));
        }

        return newPopulation;
    }

	private static int randInt(int min, int max) {

		if (max < min) {
			int temp = max;
			max = min;
			min = temp;
		}

		int result = ThreadLocalRandom.current().nextInt(min, max + 1);

		return result;
	}
    
    // Applies crossover to a set of parents and creates offspring
    public  CGaimConnection crossover(CGaimConnection parent1, CGaimConnection parent2) {
        // Create new child tour
    	CGaimConnection child = new CGaimConnection();

        // Get start and end sub tour positions for parent1's tour
        int startPos = (int) (randInt(0, parent1.tourSize()-1));
        int endPos = (int) (randInt(0, parent1.tourSize()-1));

        // Loop and add the sub tour from parent1 to our child
        for (int i = 0; i < child.tourSize(); i++) {
            // If our start position is less than the end position
            if (startPos < endPos && i > startPos && i < endPos) {
                child.setCity(i, parent1.getCity(i));
            } // If our start position is larger
            else if (startPos > endPos) {
                if (!(i < startPos && i > endPos)) {
                    child.setCity(i, parent1.getCity(i));
                }
            }
        }

        // Loop through parent2's city tour
        for (int i = 0; i < parent2.tourSize(); i++) {
            // If child doesn't have the city add it
            if (!child.containsCity(parent2.getCity(i))) {
                // Loop to find a spare position in the child's tour
                for (int ii = 0; ii < child.tourSize(); ii++) {
                    // Spare position found, add city
                    if (child.getCity(ii) == null) {
                        child.setCity(ii, parent2.getCity(i));
                        break;
                    }
                }
            }
        }
        return child;
    }

    // Mutate a tour using swap mutation
    private  void mutate(CGaimConnection tour) {
        // Loop through tour cities
        for(int tourPos1=0; tourPos1 < tour.tourSize(); tourPos1++){
            // Apply mutation rate
            if(randInt(0, 100) < mutationRate){
                // Get a second random position in the tour
                int tourPos2 = (int) (randInt(0, tour.tourSize()-1));

                // Get the randomly chosen cities
                CGaimDestination city1 = tour.getCity(tourPos1);
                CGaimDestination city2 = tour.getCity(tourPos2);

                // Swap them
                tour.setCity(tourPos2, city1);
                tour.setCity(tourPos1, city2);
            }
        }
    }

    // Selects candidate tour for crossover
    private  CGaimConnection tournamentSelection(CGaimPopulation pop) {
        // Create a tournament population
        CGaimPopulation tournament = new CGaimPopulation(tournamentSize, false);
        // For each place in the tournament get a random candidate tour and
        // add it
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (randInt(0, pop.populationSize()-1));
            tournament.saveTour(i, pop.getTour(randomId));
        }
        // Get the fittest tour
        CGaimConnection fittest = tournament.getFittest();
        return fittest;
    }
}