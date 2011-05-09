import java.io.*;
import java.util.*;

enum NodeColor {RED, BLUE};

class Person {
	private String name;
	
	private LinkedList accusees;
	private LinkedList accusers;
	
	public boolean visited;
	public NodeColor color;
	
	public Person(String name) {
		this.name = name;
		
		accusees = new LinkedList();
		accusers = new LinkedList();
	}
	
	public String name() {
		return this.name;
	}
	
	public void addAccusee(Person p) {
		accusees.addFirst(p);
	}
	
	public void addAccuser(Person p) {
		accusers.addFirst(p);
	}
	
	public Object[] accusees() {
		return accusees.toArray();
	}
	
	public Object[] accusers() {
		return accusers.toArray();
	}
}

/** Maintain collection of Person's. */
class PersonTable {
    private Hashtable people;
    private Person firstPerson;
    
    public PersonTable(int nPersons) {
        people = new Hashtable(nPersons);
    }

    /** Add a new person to the list. If the person is already on the list, do nothing. */
    public Person addPerson(String name) {
        Person p = (Person) people.get(name);

        if (p == null) {
        	p = new Person(name);
        	people.put(name, p);
        }
    
        if (firstPerson == null) {
        	firstPerson = p;
        }
        
        return p;
    }

    /** Return the Person associated with the name */
    public Person findPersonByName(String name) {
        return (Person) people.get(name);
    }

	public Person firstPerson() {
		return firstPerson;
	}
}


class liarliar {
    private static PersonTable personTable;
    private static int nPersons;
    
    private static NodeColor toggleColor(NodeColor color) {
    	if (color == NodeColor.RED) {
    		return NodeColor.BLUE;
    	} else {
    		return NodeColor.RED;
    	}
    }
    
    private static void doPartition() {
    	Person current = personTable.firstPerson();
    	
    	current.color = NodeColor.RED;
    	
    	int redTotal = 0;
    	int blueTotal = 0;
    	
    	LinkedList toVisit = new LinkedList();
    	int visitedCount = 0;
    	
    	while (visitedCount < nPersons) {
    		// If the graph has a solution, then the closure of any arbitrary node will be
    		// equal to the entire graph
    		
    		if (!current.visited) {
    			current.visited = true;
    			visitedCount++;
    			
    			if (current.color == NodeColor.RED) {
    				redTotal++;
    			} else {
    				blueTotal++;
    			}
    			
    			Object[] currentAccusers = current.accusers();
    			Object[] currentAccusees = current.accusees();

    			for (int i = 0; i < currentAccusers.length; i++) {
    				((Person)(currentAccusers[i])).color = toggleColor(current.color);
    				toVisit.addFirst(currentAccusers[i]);
    			}

    			for (int i = 0; i < currentAccusees.length; i++) {
    				((Person)(currentAccusees[i])).color = toggleColor(current.color);
    				toVisit.addFirst(currentAccusees[i]);
    			}
    		}
    		
    		current = (Person) toVisit.removeFirst();
    	}
    	
    	// According to problem statement, the first number should be the size of the
    	// larger set.
    	
    	if (redTotal > blueTotal) {
    		System.out.format("%d %d\n", redTotal, blueTotal);
    	} else {
    		System.out.format("%d %d\n", blueTotal, redTotal);
    	}
    	
    }
    
    private static void readPeople(String filename) {
    	try {
            BufferedReader input = new BufferedReader(new FileReader(filename));

            String s = input.readLine();
            nPersons = Integer.parseInt(s);

            personTable = new PersonTable(nPersons);
            
            s = input.readLine();
            while (s != null) {
            	String[] pieces = s.split("\\s+");

                if (pieces.length == 2) {
                    // This is a line of the form "Name <n>"
                    int naccused = Integer.parseInt(pieces[1]);
                    Person accuser = personTable.addPerson(pieces[0]);

                    int i = 0;
                    for (i = 0; i < naccused; i++) {
                        s = input.readLine();
                        Person accusee = personTable.addPerson(s);
                        
                        accuser.addAccusee(accusee);
                        accusee.addAccuser(accuser);
                    }
                }
                
                s = input.readLine();
            }
        } catch (java.io.IOException e) {
            // do nothing
        }
    }

    public static void main(String[] args) {
        readPeople(args[0]);
        doPartition();
    }
}