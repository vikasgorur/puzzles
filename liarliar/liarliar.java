/**
 * (C) 2011 Vikas Gorur <vikasgp@gmail.com>
 */

/**
 * This is a solution to the Facebook puzzle posted at:
 * http://www.facebook.com/careers/puzzles.php?puzzle_id=20
 * 
 * From the problem statement, we observe two things:
 * 	- Any two nodes that share an edge (regardless of direction) must belong to different sets.
 * 	- If a unique solution exists, then the closure of the graph starting from any arbitrary node
 * 	  is equal to the entire graph.
 * 
 * Given the above, the solution is to simply start at an arbitrary node and visit the entire
 * graph while alternately painting the nodes "red" or "blue".
 */

import java.io.*;
import java.util.*;

/**
 * Colors for the nodes.
 *
 */
enum NodeColor {RED, BLUE};

/**
 * Represents a person and keeps a list of all its connections. 
 *
 */
class Person {
	/**
	 * Whether this node has been visited already.
	 */
	public boolean visited;
	
	/**
	 * Color of this node.
	 */
	public NodeColor color;
	
	private LinkedList connectedTo;
	
	/**
	 * Create a person.
	 */
	public Person() {
		connectedTo = new LinkedList();
	}
	
	/**
	 * Add a connection to another Person.
	 * @param person	Person to be connected to this.
	 */
	public void addConnection(Person person) {
		connectedTo.addFirst(person);
	}
	
	/**
	 * All the Person's connected to this Person.
	 * @return An iterator for the connected Persons.
	 */
	public ListIterator connections() {
		return connectedTo.listIterator();
	}
}


/**
 * A table of unique Person's.
 */
class PersonTable {
    private Hashtable people;
    private Person firstPerson;
    
    /**
     * Create a new PersonTable.
     * @param nPersons	Number of Person's expected to be in the table. This is used as a hint
     * for the internal hash table's capacity.
     */
    public PersonTable(int nPersons) {
        people = new Hashtable(nPersons);
    }

    /**
     * Add a new person to the list. If the person is already on the list, do nothing. 
     */
    public Person addPerson(String name) {
        Person p = (Person) people.get(name);

        if (p == null) {
        	p = new Person();
        	people.put(name, p);
        }
    
        if (firstPerson == null) {
        	firstPerson = p;
        }
        
        return p;
    }

    /**
     * Get the first Person that was inserted.
     */
	public Person firstPerson() {
		return firstPerson;
	}
}


/**
 * Main class for the program.
 *
 */
class liarliar {
    private static PersonTable personTable;
    private static int nPersons;

    /**
     * Return the color opposite to the given argument.
     * @param color	RED or BLUE
     */
    private static NodeColor toggleColor(NodeColor color) {
    	if (color == NodeColor.RED) {
    		return NodeColor.BLUE;
    	} else {
    		return NodeColor.RED;
    	}
    }

    /**
     * Find the partition by doing a depth-first walk of the graph.
     */
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
    		
    		current.visited = true;
    		visitedCount++;

    		if (current.color == NodeColor.RED) {
    			redTotal++;
    		} else {
    			blueTotal++;
    		}

    		ListIterator currentConnections = current.connections();

    		while (currentConnections.hasNext()) {
    			Person currentConnection = (Person) currentConnections.next();
    			currentConnection.color = toggleColor(current.color);

    			if (!currentConnection.visited) {
    				toVisit.addFirst(currentConnection);
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
    
    /**
     * Read the input file and add names and associations to the PersonTable.
     * @param filename	File to read names from.
     */
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
                        
                        accuser.addConnection(accusee);
                        accusee.addConnection(accuser);
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