import java.io.*;
import java.util.*;

enum NodeColor {RED, BLUE};

class Person {
	private String name;
	private int id;
	
	private LinkedList accusees;
	private LinkedList accusers;
	
	public boolean visited;
	public NodeColor color;
	
	public Person(String name, int id) {
		this.name = name;
		this.id = id;
		
		accusees = new LinkedList();
		accusers = new LinkedList();
	}
	
	public int id() {
		return this.id;
	}
	
	public String name() {
		return this.name;
	}
	
	public void addAccusee(Person p) {
		accusees.addLast(p);
	}
	
	public void addAccuser(Person p) {
		accusers.addLast(p);
	}
	
	public Object[] accusees() {
		return accusees.toArray();
	}
	
	public Object[] accusers() {
		return accusers.toArray();
	}
}

/** Maintain collection of Person's. */
class PersonList {
    private LinkedList people;

    public PersonList() {
        people = new LinkedList();
    }

    /** Add a new person to the list. If the person is already on the list, do nothing. */
    public Person addPerson(String name) {
        Person p = findPersonByName(name);

        if (p == null) {
        	p = new Person(name, people.size());
        	people.addLast(p);
        }
        
        return p;
    }

    /** Return the Person associated with the name */
    public Person findPersonByName(String name) {
        ListIterator iter = people.listIterator(0);

        while (iter.hasNext()) {
            Person item = (Person) iter.next();
            if (name.equals(item.name())) {
                return item;
            }
        }

        return null;
    }
    
    public Person findPersonById(int id) {
    	return (Person) people.get(id);
    }
}


class liarliar {
    private static PersonList personList;
    private static int nPersons;
    
    private static NodeColor toggleColor(NodeColor color) {
    	if (color == NodeColor.RED) {
    		return NodeColor.BLUE;
    	} else {
    		return NodeColor.RED;
    	}
    }
    
    private static void doPartition() {
    	Person current = personList.findPersonById(0);
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
    				toVisit.addLast(currentAccusers[i]);
    			}

    			for (int i = 0; i < currentAccusees.length; i++) {
    				((Person)(currentAccusees[i])).color = toggleColor(current.color);
    				toVisit.addLast(currentAccusees[i]);
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
        personList = new PersonList();

        try {
            BufferedReader input = new BufferedReader(new FileReader(filename));

            String s = input.readLine();
            nPersons = Integer.parseInt(s);

            s = input.readLine();
            while (s != null) {
            	String[] pieces = s.split("\\s+");

                if (pieces.length == 2) {
                    // This is a line of the form "Name <n>"
                    int naccused = Integer.parseInt(pieces[1]);
                    Person accuser = personList.addPerson(pieces[0]);

                    int i = 0;
                    for (i = 0; i < naccused; i++) {
                        s = input.readLine();
                        Person accusee = personList.addPerson(s);
                        
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