import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class KMeans {
	
	public static class Point{
		double X,Y;
		public Point(double x, double y){
			X = x;
			Y = y;
		}
		public double getX() {
			return X;
		}
		public void setX(double x) {
			X = x;
		}
		public double getY() {
			return Y;
		}
		public void setY(double y) {
			Y = y;
		}
	}
	
	public static int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	private static double computeDistance(Point p, Point c){
		return Math.sqrt(Math.pow(p.getX()-c.getX(), 2) + Math.pow(p.getY() - c.getY(), 2));
	}
	
	private static boolean checkConvergence(ArrayList<Point> oldCentroid, 
			ArrayList<Point> newCentroid){
		assert(oldCentroid.size() == newCentroid.size());
		for (int i = 0; i < oldCentroid.size(); i++){
			if ((Math.abs(oldCentroid.get(i).getX() - newCentroid.get(i).getX()) > 0.000001) && 
					(Math.abs(oldCentroid.get(i).getY() - newCentroid.get(i).getY()) > 0.000001)){
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args){
		ArrayList<Point> points = new ArrayList<Point>();
		BufferedReader br;
		int k = Integer.parseInt(args[1]);
		ArrayList<Point> centroids = new ArrayList<Point>();
		HashMap<Point, ArrayList<Point>> clusters = new HashMap<Point, ArrayList<Point>>();
		try {
			br = new BufferedReader(new FileReader(args[0]));
			String line;
			while ((line = br.readLine()) != null){
				points.add(new Point(Double.parseDouble(line.split(",")[0]),
						Double.parseDouble(line.split(",")[1])));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int randIndex;
		//Initialize k random clusters. Ensure we don't 
		//accidentally add same point
		while(centroids.size() != k){
			randIndex = randInt(0, points.size() - 1);
			if(!centroids.contains(points.get(randIndex)))
				centroids.add(points.get(randIndex));
		}
		//Iterate over the points assigning them to clusters
		while(true){
			ArrayList<Point> newCentroids = new ArrayList<Point>();
			clusters.clear();
			
			for (Point c : centroids){
				clusters.put(c, new ArrayList<Point>());
			}
			
			for (Point p : points){
				double minDist = Double.MAX_VALUE;
				Point minCentroid = null;
				for (Point c : centroids){
					double dist = computeDistance(p, c);
					if(dist < minDist){
						minDist = dist;
						minCentroid = c;
					}
				}
				clusters.get(minCentroid).add(p);
			}
			
			for(Point c : clusters.keySet())
				assert(clusters.get(c).size() > 0);
			
			//Recompute means
			for (Point c: clusters.keySet()){
				double xSum = 0;
				double ySum = 0;
				int length = clusters.get(c).size();
				for(Point p : clusters.get(c)){
					xSum += p.getX();
					ySum += p.getY();
				}
				newCentroids.add(new Point(xSum/length, ySum/length));
			}
			
			if(checkConvergence(centroids, newCentroids))
				break;
			centroids = newCentroids;
		}
		
		for (Point c : clusters.keySet()){
			System.out.println("cluster centroid is " + c.getX() + "," + c.getY());
			for(Point p : clusters.get(c)){
				System.out.println(p.getX() + "," + p.getY());
			}
			System.out.println();
		}
	}
}
