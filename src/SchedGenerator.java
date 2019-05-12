import java.time.LocalTime;
import java.util.*;

public class SchedGenerator {
	
	private Course[] courses;
	private int numCourses;
	
	public SchedGenerator(Course[] allCourses) {
		courses = allCourses;
		numCourses = allCourses.length;
	}
	
	/*
	 * Builds graph with each vertex being a Course and each edge corresponds 
	 * to a time conflict between two courses
	 */
	private int[][] buildGraph(){
		int[][] graph = new int[numCourses][numCourses];
		for(int i = 0; i < numCourses; i++) {
			for(int j = i; j < numCourses; j++) {
				graph[i][j] = (conflict(courses[i],courses[j]) || i==j) ? 1: 0;
				graph[j][i] = graph[i][j];
			}
		}
		return graph;
	}
	
	/*
	 * Returns all list of all the independent sets in the graph of size k
	 * Note: the integers stored are the indices of the courses in courses array
	 */
	public ArrayList<ArrayList<Course>> indSets(int k){
		ArrayList<ArrayList<Course>> indepSets = new ArrayList<ArrayList<Course>>();
		int[][] graph = buildGraph();
		
		//Integer array to store indices of courses in courses array
		int[] courseIndices = new int[numCourses];
		for(int i = 0; i < numCourses; i++) {
			courseIndices[i] = i;
		}
		
		if(k==0) { return indepSets;} 
		else {
			ArrayList<ArrayList<Integer>> allSubs = new ArrayList<ArrayList<Integer>>();
			subsets(courseIndices,k,0,new ArrayList<Integer>(),allSubs);
			
			//Find all the independent ones
			for(ArrayList<Integer> subset: allSubs) {
				if(checkIndep(subset,graph)) {
					ArrayList<Course> indepCrseSubs = new ArrayList<Course>();
					for(int index: subset) {
						indepCrseSubs.add(courses[index]);
					}
					indepSets.add(indepCrseSubs);
				}
			}
		}
		return indepSets;
	}
	
	//Helper method to get all subsets of size k of courses
	private void subsets(int[] superSet, int k, int index, ArrayList<Integer> currSubs, ArrayList<ArrayList<Integer>> res) {
		if(currSubs.size() == k) {
			res.add(new ArrayList<Integer>(currSubs));
			return;
		}
		if(index >= superSet.length) return;
		
		int curr = superSet[index];
		currSubs.add(curr);
		
		//curr in the subset
		subsets(superSet,k,index+1,currSubs,res);
		currSubs.remove(Integer.valueOf(curr));
		
		//curr not in subset
		subsets(superSet,k,index+1,currSubs,res);
		
	}
	
	//Helper method to determine if a set is independent or not
	private boolean checkIndep(ArrayList<Integer> set, int[][] graph) {
		for(int i = 0; i < set.size(); i++) {
			for(int j = i+1; j < set.size(); j++) {
				if (graph[set.get(i)][set.get(j)] == 1) return false;
			}
		}
		return true;
	}
	
	public boolean conflict(Course c1, Course c2) {
		HashMap<Character,LocalTime[]> sched1 = c1.getSched();
		HashMap<Character,LocalTime[]> sched2 = c2.getSched();
		
		//If one of the courses do not have a specified time
		if(sched1.isEmpty() || sched2.isEmpty()) return false;
		
		boolean isConflict = false;
		//Iterates through both schedules to find conflicts
		for(Character day: sched1.keySet()) {
			//Conflicting days
			if (sched2.containsKey(day)){		
				//Conflicting times
				LocalTime[] times1 = sched1.get(day);
				LocalTime[] times2 = sched2.get(day);
				int times1Size = times1.length;
				int times2Size = times2.length;
				LocalTime[] larger = (times1Size > times2Size) ? times1 : times2;
				LocalTime[] smaller = (larger == times1) ? times2 : times1;	
			
				for(int i = 0; i < larger.length-1; i+=2) {
					for(int j = 0; j < smaller.length-1; j+=2) {
						isConflict = larger[i].isBefore(smaller[j+1]) && larger[i+1].isAfter(smaller[j]);
					} if(isConflict) break;
				}
			} if(isConflict) break;
		}
		return isConflict;
	}
}
