package concurrent.genetic.algorithm.tsp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CGaimIsland implements Runnable {
		
	boolean initialized = false;
	
	private CGaimDestinationPool pool;
	private CGaimPopulation population;
	private CGaim evolution;
	
	private double currentGeneration;
	private int numberMigrants = 0;
	private int popSize = 0;
	private int epochL = 0;
	private int id = 0;

    private CyclicBarrier barrier;
	
	public CGaimIsland(CGaimDestinationPool pool, int nMigrants, int popSize, int epochL, 
					   int id, CyclicBarrier barrier)
	{
		this.pool = pool;
		this.numberMigrants = nMigrants;
		this.popSize = popSize;
		this.epochL = epochL;
		this.id = id;
		this.barrier = barrier;
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
			for(int i = 0; i < this.epochL; i++)
			{
				this.population = evolution.evolvePopulation(this.population);
				this.currentGeneration++;
			}
			evolved = true;
	
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
    	
		population.setMigrants(migrants.clone());
	}
	
	public void printPopSize()
	{
		System.out.println("ID: " + this.id + " " + this.population.populationSize());
	}

	public CGaimPopulation getPopulation()
	{
		return this.population;
	}

	public double getCurrentGeneration() {
		return this.currentGeneration;
	}
	
	@Override
	public void run() {

		try {
			barrier.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/* If the Island is not already initialized - do it */
		if (this.initialized == false) {
			this.init();
		}

		/* evolve */
		// System.out.println("Island " + this.id + " evolves");
		this.evolve();
	}
}
