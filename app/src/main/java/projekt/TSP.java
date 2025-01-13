package projekt;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TSP {

    enum DistanceType {EUCLIDEAN, WEIGHTED}

    public class City {
        public int index;
        public double x, y;

        public City(int index, double x, double y) {
            this.index = index;
            this.x = x;
            this.y = y;
        }
        @Override
        public String toString() {
            return "City{" +
                    "index=" + index +
                    ", x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    public class Tour {

        double distance;
        int dimension;
        City[] path;

        public Tour(Tour tour) {
            distance = tour.distance;
            dimension = tour.dimension;
            path = tour.path.clone();
        }

        public Tour(int dimension) {
            this.dimension = dimension;
            path = new City[dimension];
            distance = Double.MAX_VALUE;
        }

        public Tour clone() {
            return new Tour(this);
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public City[] getPath() {
            return path;
        }

        public void setPath(City[] path) {
            this.path = path.clone();
        }

        public void setCity(int index, City city) {
            path[index] = city;
            distance = Double.MAX_VALUE;
        }
    }

    String name;
    City start;
    List<City> cities = new ArrayList<>();
    int numberOfCities;
    double[][] weights;
    DistanceType distanceType = DistanceType.EUCLIDEAN;
    int numberOfEvaluations, maxEvaluations;


    public TSP(String path, int maxEvaluations) {
        loadData(path);
        numberOfEvaluations = 0;
        this.maxEvaluations = maxEvaluations;
    }

    public void evaluate(Tour tour) {
        double distance = 0;
        distance += calculateDistance(start, tour.getPath()[0]);
        for (int index = 0; index < numberOfCities; index++) {
            if (index + 1 < numberOfCities)
                distance += calculateDistance(tour.getPath()[index], tour.getPath()[index + 1]);
            else
                distance += calculateDistance(tour.getPath()[index], start);
        }
        tour.setDistance(distance);
        numberOfEvaluations++;
    }

    private double calculateDistance(City from, City to) {
        switch (distanceType) {
            case EUCLIDEAN:
                // Izračunaj Evklidsko razdaljo med mesti
                double dx = to.x - from.x;  // Direktni dostop do lastnosti
                double dy = to.y - from.y;  // Direktni dostop do lastnosti
                return Math.sqrt(dx * dx + dy * dy);
            case WEIGHTED:
                return weights[from.index-1][to.index-1];
            default:
                return Double.MAX_VALUE;
        }
    }

    public Tour generateTour() {
        int numberOfCities = cities.size();
        City[] path = new City[numberOfCities];
        List<City> shuffledCities = new ArrayList<>(cities);

        // Use RandomUtils.nextInt(int) to generate a random index for shuffling
        for (int i = numberOfCities - 1; i > 0; i--) {
            int index = RandomUtils.nextInt(i + 1);  // Generates a random index between 0 and i inclusive
            Collections.swap(shuffledCities, i, index);  // Swap elements to shuffle the list
        }

        shuffledCities.toArray(path);
        Tour tour = new Tour(numberOfCities);
        tour.setPath(path);

        return tour;
    }

    private void loadData(String path) {
        // Nastavi začetno mesto (prvo mesto v seznamu)
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            System.err.println("File " + path + " not found!");
            return;
        }

        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line = br.readLine();
            while (line != null) {
                lines.add(line);
                line = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Poišči vrednost EDGE_WEIGHT_TYPE
        String edgeWeightType = null;
        for (String line : lines) {
            if (line.startsWith("EDGE_WEIGHT_TYPE")) {
                edgeWeightType = line.split(":")[1].trim();
                break;
            }
        }

        // Če je EDGE_WEIGHT_TYPE EUC_2D, obdelaj NODE_COORD_SECTION
        if ("EUC_2D".equals(edgeWeightType)) {
            distanceType = DistanceType.EUCLIDEAN;
            boolean inNodeCoordSection = false;

            // First loop: Process the NODE_COORD_SECTION to read cities
            for (String line : lines) {
                if (line.startsWith("NODE_COORD_SECTION")) {
                    inNodeCoordSection = true; // Start processing coordinates
                    continue;
                }

                if (inNodeCoordSection) {
                    line = line.trim();
                    if (line.equals("EOF") || line.isEmpty()) {
                        break; // End of the NODE_COORD_SECTION
                    }

                    // Split the line into ID, x, and y
                    String[] parts = line.split("\\s+");
                    if (parts.length == 3) {
                        try {
                            int id = Integer.parseInt(parts[0]);
                            double x = Double.parseDouble(parts[1]);
                            double y = Double.parseDouble(parts[2]);

                            // Create a new City object and add it to the cities collection
                            cities.add(new City(id, x, y));
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid coordinate format: " + line);
                        }
                    }
                }
            }

            // Initialize the weight matrix (weights) using Euclidean distances
            numberOfCities = cities.size(); // Number of cities equals the number of coordinates
            weights = new double[numberOfCities][numberOfCities]; // Initialize the weight matrix

            // Calculate Euclidean distances between cities and fill the weights matrix
            for (int i = 0; i < numberOfCities; i++) {
                for (int j = i; j < numberOfCities; j++) {
                    double distance = calculateDistance(cities.get(i), cities.get(j));
                    weights[i][j] = distance; // Save the distance
                    weights[j][i] = distance;
                }
            }

            /*// Print the weight matrix after calculating distances
            System.out.println("\nWeight matrix (after calculating distances):");
            for (int i = 0; i < numberOfCities; i++) {
                for (int j = 0; j < numberOfCities; j++) {
                    System.out.print(weights[i][j] + " ");
                }
                System.out.println();
            }*/
        } else if ("EXPLICIT".equals(edgeWeightType)) {
            distanceType = DistanceType.WEIGHTED;
            boolean inDisplayDataSection = false;
            boolean inEdgeWeightSection = false;
            boolean edgeWeightSectionProcessed = false;
            List<String> weightLines = new ArrayList<>();

            // First loop: Process the DISPLAY_DATA_SECTION to read cities
            for (String line : lines) {
                if (line.startsWith("DISPLAY_DATA_SECTION")) {
                    inDisplayDataSection = true; // Start processing coordinates
                    continue;
                }

                if (inDisplayDataSection) {
                    line = line.trim();
                    if (line.equals("EOF") || line.isEmpty()) {
                        break; // End of the DISPLAY_DATA_SECTION
                    }

                    // Split the line into ID, x, and y
                    String[] parts = line.split("\\s+");
                    if (parts.length == 3) {
                        try {
                            int id = Integer.parseInt(parts[0]);
                            double x = Double.parseDouble(parts[1]);
                            double y = Double.parseDouble(parts[2]);

                            // Create a new City object and add it to the cities collection
                            cities.add(new City(id, x, y));
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid coordinate format: " + line);
                        }
                    }
                }
            }

            // Second loop: Process the EDGE_WEIGHT_SECTION and store the weight matrix
            for (String line : lines) {
                if (line.startsWith("EDGE_WEIGHT_SECTION")) {
                    inEdgeWeightSection = true; // Start processing the edge weight section
                    continue;
                }

                if (inEdgeWeightSection) {
                    line = line.trim();
                    if (line.equals("DISPLAY_DATA_SECTION") || line.isEmpty()) {
                        break; // End of the EDGE_WEIGHT_SECTION
                    }
                    weightLines.add(line); // Add the line to the weightLines list
                }
            }

            // Now, convert the weightLines into the weight matrix (double[][])
            numberOfCities = cities.size(); // Number of cities equals the number of coordinates
            weights = new double[numberOfCities][numberOfCities]; // Initialize the weight matrix

            int rowIndex = 0; // Tracks the current row index

            for (String weightLine : weightLines) {
                String[] weightParts = weightLine.split("\\s+"); // Split line by spaces
                for (int colIndex = 0; colIndex < weightParts.length; colIndex++) {
                    try {
                        double weight = Double.parseDouble(weightParts[colIndex]);
                        weights[rowIndex][colIndex] = weight; // Store the value directly
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid weight format: " + weightParts[colIndex]);
                    }
                }
                rowIndex++; // Move to the next row
            }

        }

        // Nastavi začetno mesto (prvo mesto v seznamu)
        start = cities.get(0);

        /*// Izpis mest z njihovimi indeksi in koordinatami (x, y)
        System.out.println("Cities and their coordinates:");
        for (City city : cities) {
            System.out.println("City " + city.index + ": (" + city.x + ", " + city.y + ")");
        }*/
    }

    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    public int getNumberOfEvaluations() {
        return numberOfEvaluations;
    }
}
