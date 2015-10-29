package concurrent.genetic.algorithm.tsp;


import java.util.ArrayList;

public class CGaimDestinationPool {

    // Holds our cities
    private static ArrayList destinationCities = new ArrayList<CGaimDestination>();

    // Adds a destination city
    public static void addCity(CGaimDestination city) {
        destinationCities.add(city);
    }
    
    // Get a city
    public static CGaimDestination getCity(int index){
        return (CGaimDestination)destinationCities.get(index);
    }
    
    // Get the number of destination cities
    public static int numberOfCities(){
        return destinationCities.size();
    }
}