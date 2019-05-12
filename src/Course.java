import java.time.LocalTime;
import java.util.*;

public class Course {
	private String cName;
	private String cTimeStr;
	private String cNameAbbrev;
	private HashMap<Character,LocalTime[]> sched;
	
	public Course(String abbrev, String name, String timeStr){
		cName = name;
		cTimeStr = timeStr;
		cNameAbbrev = abbrev;
	}
	
	public HashMap<Character,LocalTime[]> getSched() {
		HashMap<Character,LocalTime[]> sched = new HashMap<Character,LocalTime[]>();
		
		//Times not available, returns empty schedule
		if(cTimeStr.equals("TBA") || cTimeStr.equals(" ")) {
			return sched;  
		}
		
		//Parsing time period/s
		boolean multTimes = cTimeStr.contains("&");  //course has two time periods
		int daysTimeSep = cTimeStr.indexOf(" ");
		char[] days = cTimeStr.substring(0,daysTimeSep).toCharArray();
		String startStr = cTimeStr.substring(daysTimeSep+1,cTimeStr.indexOf("-"));
		boolean pmStart = startStr.contains("pm");
		String endStr = (multTimes) ? cTimeStr.substring(cTimeStr.indexOf("-")+1,cTimeStr.indexOf("&")) : cTimeStr.substring(cTimeStr.indexOf("-")+1); 
		boolean pmEnd = endStr.contains("pm");
		int startHrOrig = Integer.parseInt(startStr.substring(0,startStr.indexOf(":")));
		int startHr = (pmStart && startHrOrig != 12) ? startHrOrig + 12: startHrOrig;
		int startMin = Integer.parseInt(startStr.substring(startStr.indexOf(":")+1,startStr.indexOf("m")-1));
		int endHrOrig = Integer.parseInt(endStr.substring(0,endStr.indexOf(":")));
		int endHr = (pmEnd && endHrOrig != 12) ? endHrOrig + 12: endHrOrig;
		int endMin = Integer.parseInt(endStr.substring(endStr.indexOf(":")+1,endStr.indexOf("m")-1));
		LocalTime start = LocalTime.of(startHr, startMin);
		LocalTime end = LocalTime.of(endHr,endMin);
		
		for(char day: days) {
			System.out.println(cName);
			System.out.println(day + " start:" + " " + startHr + ":"+startMin);
			System.out.println(day + " end:" + " " + endHr + ":"+endMin);
			System.out.println("");
			sched.put(day, new LocalTime[]{start,end});
		}
		
		if(multTimes) {
			String time2 = cTimeStr.substring(cTimeStr.indexOf("&") + 1);
			daysTimeSep = time2.indexOf(" ");
			days = time2.substring(0,daysTimeSep).toCharArray();
			startStr = time2.substring(daysTimeSep+1,time2.indexOf("-"));
			pmStart = startStr.contains("pm");
			endStr = (multTimes) ? time2.substring(time2.indexOf("-")+1,time2.indexOf("&")) : time2.substring(time2.indexOf("-")+1); 
			pmEnd = endStr.contains("pm");
			startHrOrig = Integer.parseInt(startStr.substring(0,startStr.indexOf(":")));
			startHr = (pmStart && startHrOrig != 12) ? startHrOrig + 12 : startHrOrig;
			startMin = Integer.parseInt(startStr.substring(startStr.indexOf(":")+1,startStr.indexOf("m")-1));
			endHrOrig = Integer.parseInt(endStr.substring(0,endStr.indexOf(":")));
			endHr = (pmEnd && endHrOrig != 12) ? endHrOrig + 12 : endHrOrig;
			endMin = Integer.parseInt(endStr.substring(endStr.indexOf(":")+1,endStr.indexOf("m")-1));
			start = LocalTime.of(startHr, startMin);
			end = LocalTime.of(endHr,endMin);
			
			for(char day: days) {
				if(sched.containsKey(day)) {
					//Multiple periods in same day
					LocalTime[] times = sched.get(day);
					times[2] = start; times[3] = end;
					sched.put(day, times);
				} else {
					sched.put(day, new LocalTime[]{start,end});
				}
			}
		}
		
		return sched;
	}
	
	public String getName() {
		return cName;
	}
	
	public String getAbbrev() {
		return cNameAbbrev;
	}
	
	public String getTimeStr() {
		return cTimeStr;
	}
}
