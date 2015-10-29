package concurrent.genetic.algorithm.tsp;

/*
* City.java
* Models a city
*/

public class CGaimDestination {
    int x;
    int y;
    
    // Constructs a randomly placed city
    public CGaimDestination(){
        this.x = (int)(Math.random()*1000);
        this.y = (int)(Math.random()*1000);
    }
    
    // Constructs a city at chosen x, y location
    public CGaimDestination(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    // Gets city's x coordinate
    public int getX(){
        return this.x;
    }
    
    // Gets city's y coordinate
    public int getY(){
        return this.y;
    }
    
    // Gets the distance to given city
    public double distanceTo(CGaimDestination city){
        int xDistance = Math.abs(getX() - city.getX());
        int yDistance = Math.abs(getY() - city.getY());
        double distance = Math.sqrt( (xDistance*xDistance) + (yDistance*yDistance) );
        
        return distance;
    }
    
    @Override
    public String toString(){
        return getX()+", "+getY();
    }
}