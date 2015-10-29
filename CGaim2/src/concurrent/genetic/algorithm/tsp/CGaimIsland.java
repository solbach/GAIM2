package concurrent.genetic.algorithm.tsp;

import java.util.ArrayList;
import java.util.List;

public class CGaimIsland implements Runnable {
		
	boolean initialized = false;
	
	private CGaimDestinationPool pool;
	private CGaimPopulation population;
	private CGaim evolution;
	
	private double currentGeneration;
	private int numberMigrants = 0;
	private int bestFitness = 0;
	private int popSize = 0;
	private int epochL = 0;
	private int id = 0;
	
	
	public CGaimIsland(CGaimDestinationPool pool, int nMigrants, int popSize, int epochL, int id)
	{
		this.pool = pool;
		this.numberMigrants = nMigrants;
		this.popSize = popSize;
		this.epochL = epochL;
		this.id = id;
	}
	
	public void init()
	{
		try{			
			/* Initialize Population */
			this.population = new CGaimPopulation(this.popSize, true);
						
			/* Initialize Evolution Logic */
			this.evolution = new CGaim();
			this.initialized = true;
		}catch(IndexOutOfBoundsException e)
		{
			this.initialized = false;
			//System.err.println(e.getMessage());			
		}
	}
	
	public boolean evolve()
	{
		boolean evolved = false;
		try{
			for(int i = 0; i < this.epochL; i++)
			{
				this.population = evolution.evolvePopulation(this.population);
			}
			evolved = true;
		}catch(IndexOutOfBoundsException e)
		{
			evolved = false;
			//System.err.println(e.getMessage());
		}
		
		return evolved;
	}
	
	public int bestFitness()
	{
		return population.getFittest().getDistance();		
	}
	
	public CGaimConnection[] getMigrants()
	{
		CGaimConnection[] migrants;
    	migrants = new CGaimConnection[numberMigrants];
    	migrants = population.getMigrants(numberMigrants);
    	
    	/* Update population size - its shrinked after migration */
    	this.popSize = this.population.populationSize();
    	
    	/* Return a clone of the object to avoid shared memory access */    	
    	return migrants.clone();
	}
	
	public void setMigrants(CGaimConnection migrants[])
	{
    	/* Update population size - its shrinked after migration */
    	this.popSize = this.population.populationSize();
    	
    	System.err.println(this.popSize);
    	
		population.setMigrants(migrants.clone());
	}
	
	public void printPopSize()
	{
		System.out.println("ID: " + this.id + " " + this.population.populationSize());
	}
	

	@Override
	public void run() {
		/* If the Island is not already initialized - do it */
		if(this.initialized == false)
		{
			this.init();
		}
		
		/* evolve */
		System.out.println("Island " + this.id + " evolves");
		this.evolve();
		
	}
}
