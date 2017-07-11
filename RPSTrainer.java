import java.util.Arrays;
import java.util.Random;

public class RPSTrainer {
        
    public static final int ROCK = 0, PAPER = 1, SCISSORS = 2, NUM_ACTIONS = 3, PLAYER_1 = 0, PLAYER_2 = 1, NUM_PLAYERS = 2;
    public static final Random random = new Random();
    double[] regretSum = new double[NUM_PLAYERS][NUM_ACTIONS], 
             strategy = new double[NUM_PLAYERS][NUM_ACTIONS], 
             strategySum = new double[NUM_PLAYERS][NUM_ACTIONS], 
             // oppStrategy = { 0.4, 0.3, 0.3 }; 

    private double[] getStrategy(int p) {
        double normalizingSum = 0;
        for (int a = 0; a < NUM_ACTIONS; a++) {
            strategy[a] = regretSum[p][a] > 0 ? regretSum[p][a] : 0;
            normalizingSum += strategy[p][a];
        }
        for (int a = 0; a < NUM_ACTIONS; a++) {
            if (normalizingSum > 0)
              strategy[p][a] /= normalizingSum;
            else
              strategy[p][a] = 1.0 / NUM_ACTIONS;
            strategySum[p][a] += strategy[p][a];
        }
        return strategy;
    }
    
    public int getAction(double[] strategy) {
        double r = random.nextDouble();
        int a = 0;
        double cumulativeProbability =  0;
        while (a < NUM_ACTIONS - 1) {
            cumulativeProbability += strategy[a];
            if (r < cumulativeProbability)
                break;
            a++;
        }
        return a;
    }
    
    public void train(int iterations) {
        for (int i = 0; i < iterations; i++) {
            double[] strategy = getStrategy(PLAYER_1);
            double[] oppStrategy = getStrategy(PLAYER_2);
            int myAction = getAction(strategy);
            int otherAction = getAction(oppStrategy);
            
            regretSum = getRegretSum(otherAction, regretSum, PLAYER_1);
            regretSum = getRegretSum(myAction, regretSUm, PLAYER_2);
        }
    }

    public double[][] getRegretSum(int otherAction, double[][] regretSum, int p) {
        double[] actionUtility = new double[NUM_ACTIONS];
        double regretSum = 0;
        actionUtility[otherAction] = 0;
        actionUtility[otherAction == NUM_ACTIONS - 1 ? 0 : otherAction + 1] = 1;
        actionUtility[otherAction == 0 ? NUM_ACTIONS - 1 : otherAction - 1] = -1;
        for (int a = 0; a < NUM_ACTIONS; a++)
            regretSum[p][a] += actionUtility[a] - actionUtility[myAction];
        return regretSum;
    }
    
    public double[] getAverageStrategy(int p) {
        double[] avgStrategy = new double[NUM_ACTIONS];
        double normalizingSum = 0;
        for (int a = 0; a < NUM_ACTIONS; a++)
            normalizingSum += strategySum[p][a];
        for (int a = 0; a < NUM_ACTIONS; a++) 
            if (normalizingSum > 0)
                avgStrategy[a] = strategySum[p][a] / normalizingSum;
            else
                avgStrategy[a] = 1.0 / NUM_ACTIONS;
        return avgStrategy;
    }

    public static void main(String[] args) {
        RPSTrainer trainer = new RPSTrainer();
        trainer.train(1000000);
        System.out.println(Arrays.toString(trainer.getAverageStrategy(PLAYER_1)));
        System.out.println(Arrays.toString(trainer.getAverageStrategy(PLAYER_2)));
    }

}
