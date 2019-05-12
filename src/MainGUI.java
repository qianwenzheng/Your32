import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MainGUI extends JFrame implements ActionListener{
	
	private static final int MAX_CRSES = 10;
	private static final int CRSE_LOAD = 4;
	private static DefaultTableModel dtm;
	private static JTable crseTable;
	private static JButton deleteBtn;
	private static JButton addBtn;
	private static JButton genBtn;
	private static ArrayList<Course> allCrses;
	private static HashSet<Integer> selCrseIndices;
	private static int numSelCrses;
	private static HashMap<String,Integer> tbleMap; 
	
	public MainGUI() {
		JFrame frame = new JFrame("Your 32");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,650);
        JLabel logoLabel = new JLabel(new ImageIcon(MainGUI.class.getResource("/williamslogo.png")), JLabel.CENTER);
        JLabel sloganLabel = new JLabel("Choose Your Courses", JLabel.CENTER);
        sloganLabel.setFont(new Font("Serif", Font.BOLD, 20));
        JPanel topPanel = new JPanel(new BorderLayout(0,10));
        topPanel.setBackground(new Color(255,204,51));
        topPanel.add(logoLabel, BorderLayout.NORTH); topPanel.add(sloganLabel, BorderLayout.SOUTH);
        topPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        frame.getContentPane().add(topPanel, BorderLayout.NORTH);
        
        dtm = new DefaultTableModel(0,2);
        String[] headers = new String[] {"Course Name", "Time"};
        dtm.setColumnIdentifiers(headers);
        crseTable = new JTable();
        crseTable.setModel(dtm);
        crseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        crseTable.setDefaultEditor(Object.class, null);
        crseTable.setRowHeight(crseTable.getRowHeight() + 3);
        JScrollPane crseScrollPane = new JScrollPane(crseTable);
        JPanel scrollPanel = new JPanel();
        scrollPanel.add(crseScrollPane);
        scrollPanel.setBorder(BorderFactory.createEmptyBorder(2,1,2,1));
        scrollPanel.setBackground(new Color(255,204,51));
        frame.getContentPane().add(scrollPanel, BorderLayout.CENTER);
        
        deleteBtn = new JButton("Delete Selected Course");
        addBtn = new JButton("Add Course");
        genBtn = new JButton("Generate Schedules");
        deleteBtn.addActionListener(this); addBtn.addActionListener(this); genBtn.addActionListener(this);
        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.add(addBtn, BorderLayout.WEST); btnPanel.add(genBtn, BorderLayout.EAST); btnPanel.add(deleteBtn, BorderLayout.CENTER);
        btnPanel.setBackground(new Color(255,204,51));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        frame.getContentPane().add(btnPanel,BorderLayout.SOUTH);
       
        frame.setVisible(true);
	}
	
	public static void main(String args[]){
        MainGUI gui = new MainGUI();        
        CatalogReader reader = new CatalogReader();
        allCrses = reader.getAllCourses();
        selCrseIndices = new HashSet<Integer>();
        numSelCrses = 0;
        tbleMap = new HashMap<String,Integer>();
     }


	@Override
	public void actionPerformed(ActionEvent e) {
		JButton source = (JButton)e.getSource();
		if(source == addBtn) {
			if(numSelCrses == MAX_CRSES) {
				JOptionPane.showMessageDialog(null, "Maximum Courses reached", "Error",
					 JOptionPane.PLAIN_MESSAGE);
			} else {
				int index = addCrseDialog();
				
				//Keep track of which course in allCrses array was selected
				if(!selCrseIndices.contains(index) && index > -1) {
					selCrseIndices.add(index);
					numSelCrses++;
					String[] crseStr = new String[]{allCrses.get(index).getName(),allCrses.get(index).getTimeStr()};
					dtm.addRow(crseStr);
					crseTable.validate();
					tbleMap.put(crseStr[0], index);
				}
			}
			
		} else if(source == deleteBtn) {
			int selected = crseTable.getSelectedRow();
			String crseName = (String)crseTable.getValueAt(selected, 0);
			if(selected != -1) {
				dtm.removeRow(selected);
				crseTable.validate();
				selCrseIndices.remove(tbleMap.get(crseName));
				numSelCrses--;
			}
		} else if(source == genBtn) {
			genSchedDialog();
		}	
	}
	
	private void genSchedDialog() {
		Course[] selCrses = new Course[numSelCrses];
		int counter = 0;
		for(int index: selCrseIndices) {
			selCrses[counter] = allCrses.get(index);
			System.out.println(selCrses[counter].getName() + " " + selCrses[counter].getSched());
			counter++;
		}
		SchedGenerator schedGen = new SchedGenerator(selCrses);
		
		ArrayList<ArrayList<Course>> potScheds = schedGen.indSets(CRSE_LOAD);
		StringBuilder listOfScheds = new StringBuilder();
		int schedCounter = 1;
		for(ArrayList<Course> subList: potScheds) {
			listOfScheds.append("Potential Schedule " + schedCounter + ":" + "\n");
			schedCounter++;
			for(Course curr: subList) {
				listOfScheds.append(curr.getTimeStr() + ":" + " " + curr.getName() + "\n");
			}
			listOfScheds.append("\n");
		}
		
		String output = (listOfScheds.toString().isEmpty() || numSelCrses < 4) ? "No class schedules of 4 courses possible." : listOfScheds.toString();
		JTextArea outputText = new JTextArea();
		outputText.setText(output);
		outputText.setEditable(false);
		outputText.setLineWrap(true);
		JScrollPane scrollP = new JScrollPane(outputText);
		scrollP.setMaximumSize(new Dimension(600,500));
		scrollP.setPreferredSize(new Dimension(600,500));
		JOptionPane.showMessageDialog(null, scrollP, "Potential Schedules", JOptionPane.PLAIN_MESSAGE);
	}
	
	private int addCrseDialog() {
		JFrame frame = new JFrame("Add a course");
		frame.setPreferredSize(new Dimension(600,600));
		frame.setLocationRelativeTo(null);
		String[] allCrsesNames = new String[allCrses.size()];
		for(int i = 0; i < allCrses.size(); i++) {
			allCrsesNames[i] = allCrses.get(i).getName();
		}
		String crse = (String)JOptionPane.showInputDialog(frame, "Select a course","Add a course", JOptionPane.PLAIN_MESSAGE, null, allCrsesNames, allCrsesNames[0]);
		if(crse == null) return -1;
		int index = 0;
		for(int i = 0; i < allCrsesNames.length; i++) {
			if(allCrsesNames[i].equals(crse)) {
				index = i;
				break;
			}
		}
		return index;
	}
	
	
	
}
