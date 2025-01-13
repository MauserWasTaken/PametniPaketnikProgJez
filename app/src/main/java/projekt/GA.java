package projekt;


import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import java.util.ArrayList;
import java.util.Random;

public class GA {

    int popSize;
    double cr; //crossover probability
    double pm; //mutation probability

    ArrayList<TSP.Tour> population;
    ArrayList<TSP.Tour> offspring;
    Function1<TSP.Tour, Unit> updatedTour;

    public GA(int popSize, double cr, double pm, Function1<TSP.Tour, Unit> updatedTour) {
        this.popSize = popSize;
        this.cr = cr;
        this.pm = pm;
        this.updatedTour = updatedTour;
    }

    public TSP.Tour execute(TSP problem) {
        population = new ArrayList<>();
        offspring = new ArrayList<>();
        TSP.Tour best = null;
        for (int i = 0; i < popSize; i++) {
            TSP.Tour newTour = problem.generateTour();
            //System.out.println("neewTour length: "+newTour.path.length);
            problem.evaluate(newTour);
            population.add(newTour);
            if (best == null || newTour.getDistance() < best.getDistance()) {
                best = newTour;
                //System.out.println("best: " + best);
            }
        }
        System.out.println("started program");
        while (problem.getNumberOfEvaluations() < problem.getMaxEvaluations()) {
          //  System.out.println("main itteration "+problem.getNumberOfEvaluations());
            //elitizem - poišči najboljšega in ga dodaj v offspring in obvezno uporabi clone()
            while (offspring.size() < popSize) {
            //    System.out.println("sub iteration "+bb);
                TSP.Tour parent1 = tournamentSelection();
                TSP.Tour parent2 = tournamentSelection();
                if (parent1 == parent2) {
                    continue;
                }

                if (RandomUtils.nextDouble() < cr) {
                    TSP.Tour[] children = pmx(parent1, parent2);
                    offspring.add(children[0]);
                    if (offspring.size() < popSize)
                        offspring.add(children[1]);
                } else {
                    offspring.add(parent1.clone());
                    if (offspring.size() < popSize)
                        offspring.add(parent2.clone());
                }
            }

            for (TSP.Tour off : offspring) {
                if (RandomUtils.nextDouble() < pm) {
                    swapMutation(off);
                }
            }
            for (TSP.Tour testingTour : offspring) {
                assert best != null;
                problem.evaluate(testingTour);
                if (testingTour.getDistance() < best.getDistance()) {
                    best = testingTour;
                    updatedTour.invoke(best);
                    System.out.println("best: " + best + " " + best.getDistance());
                }
            }
            //TODO ovrednoti populacijo in shrani najboljšega (best)

            //implementacijo lahko naredimo bolj učinkovito tako, da overdnotimo samo tiste, ki so se spremenili (mutirani in križani potomci)

            population = new ArrayList<>(offspring);
            offspring.clear();
//            problem.numberOfEvaluations++;
        }
        return best;
    }

    private void swapMutation(TSP.Tour off) {
        int rnd;
        int rnd2;
        do {
            rnd = new Random().nextInt(off.path.length);
            rnd2 = new Random().nextInt(off.path.length);
        } while (rnd == rnd2);
        TSP.City temp = off.path[rnd];
        off.path[rnd] = off.path[rnd2];
        off.path[rnd2] = temp;

    }


    private TSP.City getValidCity(TSP.City city, TSP.Tour parent1, TSP.Tour parent2, boolean isFirstOrNot, TSP.Tour offspring,int firstIndent,int secondIndent) {


        for (int i = firstIndent; i <= secondIndent; i++) {
            if (offspring.path[i] == city) {
                int j = firstIndent;
                boolean found = false;
                if (isFirstOrNot) {
                    while (!found) {
                        if (parent2.path[j] == city) {
                            found = true;
                        }
                        j++;
                    }
                    return getValidCity(parent1.path[j-1], parent1, parent2, true, offspring,firstIndent,secondIndent);
                } else {
                    while (!found) {
                        if (parent1.path[j] == city) {
                            found = true;
                        }
                        j++;
                    }
                    return getValidCity(parent2.path[j-1], parent1, parent2, false, offspring,firstIndent,secondIndent);

                }
            }
        }
        return city;
    }

    private TSP.Tour[] pmx(TSP.Tour parent1, TSP.Tour parent2) {
        int rnd;
        int rnd2;
        do {
            rnd = new Random().nextInt(parent1.path.length);
            rnd2 = new Random().nextInt(parent1.path.length);
        } while (rnd == rnd2);
        TSP.Tour offspring1 = parent1.clone();
        TSP.Tour offspring2 = parent2.clone();
        int firstIndent, secondIndent;
        if (rnd < rnd2) {
            firstIndent = rnd;
            secondIndent = rnd2;
        } else {
            firstIndent = rnd2;
            secondIndent = rnd;
        }
        for (int i = 0; i < parent1.path.length; i++) {
            offspring1.path[i] = null;
            offspring2.path[i] = null;
        }
        for (int i = rnd; i <= rnd2; i++) {
            offspring1.path[i] = parent2.path[i];
            offspring2.path[i] = parent1.path[i];
        }
        for (int i = 0; i < parent1.path.length; i++) {
            if (offspring1.path[i] == null) {
                offspring1.path[i] = getValidCity(parent1.path[i],parent1,parent2,true,offspring1,firstIndent,secondIndent);
            }
            if (offspring2.path[i] == null) {
                offspring2.path[i] = getValidCity(parent2.path[i],parent1,parent2,false,offspring2,firstIndent,secondIndent);
            }
        }


        //izvedi pmx križanje, da ustvariš dva potomca

        TSP.Tour[] array = new TSP.Tour[2];
        array[0]=offspring1;
        array[1]=offspring2;
        return array;
    }

    private TSP.Tour tournamentSelection() {
        int rnd;
        int rnd2;
        do {
            rnd = new Random().nextInt(population.size());
            rnd2 = new Random().nextInt(population.size());
        } while (rnd == rnd2);
        TSP.Tour contestant1 = population.get(rnd);
        TSP.Tour contestant2 = population.get(rnd2);
        if (contestant2.getDistance() < contestant1.getDistance())
            return contestant2;
        else
            return contestant1;


        // naključno izberi dva RAZLIČNA posameznika in vrni boljšega
    }
}
