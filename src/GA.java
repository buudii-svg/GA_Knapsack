import java.util.ArrayList;
import java.util.Random;

public class GA {

    // Knapsack related variables
    private int noItems;
    private double[] values;
    private double[] weights;
    private double knapsackMaxSize;

    private static int noCandidates;
    private static int maxGen;
    public static double crossProb= 0.6;
    public static double mutatProb = 0.001;
    private ArrayList<String> CandidateSolutions;
    private double bestFitness;


    // Class Constructor
    public GA(int noItems, double[] values, double[]weights, double knapsackSize, int population, int generations){
        // Knapsack related initializations
        this.noItems = noItems;
        this.values = values;
        this.weights = weights;
        this.knapsackMaxSize = knapsackSize;

        GA.noCandidates = population;
        maxGen = generations;


        CandidateSolutions = new ArrayList<String>();

        // Initialize first generation
        generateSolutions(GA.noCandidates);

        // Run the GA until required fitness achieved or
        for(int k = 0; k < GA.maxGen && (bestFitness < (0.9*knapsackSize)); k++){
            System.out.print("Generation: " + (k+1));
            System.out.print(" " + "Best solution:" + getBestSolution());
            System.out.println(" " + "Best value:" + bestFitness);
            newGen();
        }
    }

    // This method generates all solution candidates as first generation
    // Takes as parameter the number of candidates in this first gen.
    private void generateSolutions(int candidateSize){
        // We initialize a new Random object
        Random rand = new Random(System.currentTimeMillis());

        String cand;

        // For each candidate solution
        for(int i = 0; i < candidateSize; i++){
            cand = "";

            // Randomly assign 1 or 0 to the item to take in the knapsack
            // 1 => the respective item is taken
            // 0 => we leave the respective item
            for(int j = 0; j < noItems; j++){
                int letter = rand.nextInt(2);
                cand += letter;
            }

            // Add the randomly formed candidate solution to the generation
            CandidateSolutions.add(cand);
        }
    }

    // This method sets the fitness function through which we will check all
    // candidate solution fitnesses
    private void setFitness(double fitness){
        this.bestFitness = fitness;
    }

    // This method calculates the fitness of a given candidate solution
    private double calcFitness(String solution){
        double fit = 0;
        double weight = 0;

        // We iterate over all the candidate solution genes.
        for(int i = 0; i < solution.length(); i++){
            // If gene is 1, respective item is taken into the knapsack
            if(solution.charAt(i) == 49){
                // Increment the weight and the value of taking this
                // item in the sack
                weight += weights[i];
                fit += values[i];
            }
        }

        // If we did not surpass the knapsack size
        // return the fitness for this candidate solution
        // OTHERWISE return -1
        if(weight <= knapsackMaxSize)
            return fit;
        else
            return -1;
    }

    // This method generates new offsprings from two potentially best solutions
    private String crossOver(String candidate1, String candidate2){
        // This will hold the resulting crossed over offspring
        String crossedOver = "";

        // Iterate over the genes of a chromosome
        for(int i = 0; i < candidate1.length(); i++){
            // Checking randomly whether to add to the resulting offspring
            // a gene from candidate1 or from candidate2
            if(Math.random() >= crossProb)
                crossedOver += candidate1.charAt(i);
            else
                crossedOver += candidate2.charAt(i);
        }
//        System.out.println("Cross Over ");
        return crossedOver;
    }

    // This method mutates the genes of the given candidate solution
    // based on the mutation probability
    private String mutate(String candidate){
        // Iterate over the genes of the candidate
        for(int i = 0; i < candidate.length(); i++){
            // Checking randomly whether to mutate the current
            // gene of the iteration
            if(Math.random() <= mutatProb)
                candidate = changeBit(i, candidate);
        }

        // Return the mutated candidate solution
//        System.out.println("mutation");
        return candidate;
    }

    private String changeBit(int idx, String candidate){
        String returnStr = "";
        for(int i = 0; i < candidate.length(); i++){
            if(i == idx){
                if(candidate.charAt(i) == 49)
                    returnStr += 0;
                else
                    returnStr += 1;
            }
            else
                returnStr += candidate.charAt(i);
        }
        return returnStr;
    }


    // This method returns the best solution out of the current generation
    private String getBestSolution(){
        double bestFit = -1;
        String bestSol = "";

        // Iterate over all the generation
        for (String CandidateSolution : CandidateSolutions) {
            double newFit = calcFitness(CandidateSolution);
            if(newFit != -1){
                // If a better fit found
                // update bestSol variable
                if (newFit >= bestFit) {
                    bestSol = CandidateSolution;
                    bestFit = newFit;
                }
            }
        }
        setFitness(bestFit);
        // Return the best candidate solution
        return bestSol;
    }

    // This method takes the 1st and 2nd best candidate solutions
    // and creates a new generation through crossovering them
    // and mutating the new generation
    private void newGen(){
        // Get first best candidate solution
        String can1 = getBestSolution();

        // Remove it from the list of solutions so we can select the 2nd best one
        CandidateSolutions.remove(can1);

        // Get first best candidate solution
        String can2 = getBestSolution();

        // Create new generation
        CandidateSolutions = new ArrayList<String>();

        // Add the best solution thus far
        CandidateSolutions.add(can1);

        // Create all new offsprings from crossover and mutation
        for(int i = 1; i < noCandidates; i++){
            CandidateSolutions.add(mutate(crossOver(can1, can2)));
        }
    }
}