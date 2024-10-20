package election;

import java.lang.Thread.State;

/* 
 * Election Analysis class which parses past election data for the house/senate
 * in csv format, and implements methods which can return information about candidates
 * and nationwide election results. 
 * 
 * It stores the election data by year, state, then election using nested linked structures.
 * 
 * The years field is a Singly linked list of YearNodes.
 * 
 * Each YearNode has a states Circularly linked list of StateNodes
 * 
 * Each StateNode has its own singly linked list of ElectionNodes, which are elections
 * that occured in that state, in that year.
 * 
 * This structure allows information about elections to be stored, by year and state.
 * 
 * @author Colin Sullivan
 */
public class ElectionAnalysis {

    // Reference to the front of the Years SLL
    private YearNode years;

    public YearNode years() {
        return years;
    }

    /*
     * Read through the lines in the given elections CSV file
     * 
     * Loop Though lines with StdIn.hasNextLine()
     * 
     * Split each line with:
     * String[] split = StdIn.readLine().split(",");
     * Then access the Year Name with split[4]
     * 
     * For each year you read, search the years Linked List
     * -If it is null, insert a new YearNode with the read year
     * -If you find the target year, skip (since it's already inserted)
     * 
     * If you don't find the read year:
     * -Insert a new YearNode at the end of the years list with the corresponding
     * year.
     * 
     * @param file String filename to parse, in csv format.
     */
    public void readYears(String file) {
        // WRITE YOUR CODE HERE
        StdIn.setFile(file);
        while (StdIn.hasNextLine()) {
            String[] split = StdIn.readLine().split(",");
            int year = Integer.parseInt(split[4]);

            YearNode current = years;
            Boolean year_state_exists = false;
            while (current != null) {
                if(current.getYear() == year) {
                year_state_exists = true;
                break;
                }
                current = current.getNext();
            }
    
            if (year_state_exists == false) {
                YearNode new_year = new YearNode(year);
                if (years == null) {
                years = new_year;
                }
    
                else {
                current = years;
                while (current.getNext() != null) {
                    current = current.getNext();
                }
                current.setNext(new_year);
                }
            }
        }
    }

    /*
     * Read through the lines in the given elections CSV file
     * 
     * Loop Though lines with StdIn.hasNextLine()
     * 
     * Split each line with:
     * String[] split = StdIn.readLine().split(",");
     * Then access the State Name with split[1] and the year with split[4]
     * 
     * For each line you read, search the years Linked List for the given year.
     * 
     * In that year, search the states list. If the target state state_exists, continue
     * onto the next csv line. Else, insert a new state node at the END of that
     * year's
     * states list (aka that years "states" reference will now point to that new
     * node).
     * Remember the states list is circularly linked.
     * 
     * @param file String filename to parse, in csv format.
     */
    public void readStates(String file) {
        // WRITE YOUR CODE HERE
        StdIn.setFile(file);
        while (StdIn.hasNextLine()) {
            String[] split = StdIn.readLine().split(",");
            int year = Integer.parseInt(split[4]);
            String state = split[1];
            
            boolean state_exists = false;
            YearNode current_year = years;
            while (current_year != null) {
                if (current_year.getYear() == year) {
                    if (current_year.getStates() == null) {
                        current_year.setStates(new StateNode(state, current_year.getStates()));
                        current_year.getStates().setNext(current_year.getStates());
                        break;
                    }

                    StateNode current_state = current_year.getStates();
                    do {
                        if (current_state.getStateName().equals(state)) {
                            state_exists = true;
                            break;
                        }
                        current_state = current_state.getNext();
                    } while (current_state != current_year.getStates());

                    if (!state_exists) {
                        StateNode new_state = current_year.getStates().getNext();
                        current_year.getStates().setNext(new StateNode(state, new_state));
                        current_year.setStates(current_year.getStates().getNext());
                        break;
                    }
                }
                current_year = current_year.getNext();
            }
        }
    }

    /*
     * Read in Elections from a given CSV file, and insert them in the
     * correct states list, inside the correct year node.
     * 
     * Each election has a unique ID, so multiple people (lines) can be inserted
     * into the same ElectionNode in a single year & state.
     * 
     * Before we insert the candidate, we should check that they dont exist already.
     * If they do exist, instead modify their information new data.
     * 
     * The ElectionNode class contains addCandidate() and modifyCandidate() methods
     * for you to use.
     * 
     * @param file String filename of CSV to read from
     */
    public void readElections(String file) {
        // WRITE YOUR CODE HERE
        StdIn.setFile(file);
        while (StdIn.hasNextLine()) {
            String line = StdIn.readLine();
            String[] split = line.split(",");
            int raceID = Integer.parseInt(split[0]);
            String stateName = split[1];
            int seat = Integer.parseInt(split[2]);
            boolean senate = split[3].equals("U.S. Senate");
            int year = Integer.parseInt(split[4]);
            String canName = split[5];
            String party = split[6];
            int votes = Integer.parseInt(split[7]);
            boolean winner = split[8].toLowerCase().equals("true");

            YearNode current_year = years;
            while (current_year != null) {
                if (current_year.getYear() == year) {
                    StateNode current_state = current_year.getStates();

                    do {
                        if (current_state.getStateName().equals(stateName)) {
                            if (current_state.getElections() == null) {
                                current_state.setElections(new ElectionNode());
                                current_state.getElections().setRaceID(raceID);
                                current_state.getElections().setoOfficeID(seat);
                                current_state.getElections().setSenate(senate);
                                current_state.getElections().addCandidate(canName, votes, party, winner);
                                break;
                            }

                            ElectionNode current_election = current_state.getElections();
                            while (current_election != null) {
                                if (current_election.getRaceID() == raceID) {
                                    if (current_election.getCandidates().contains(canName)) {
                                        current_election.modifyCandidate(canName, votes, party);
                                    } else {
                                        current_election.addCandidate(canName, votes, party, winner);
                                    }
                                    break;
                                }

                                if (current_election.getNext() == null) {
                                    ElectionNode new_election = new ElectionNode();
                                    new_election.setRaceID(raceID);
                                    new_election.setoOfficeID(seat);
                                    new_election.setSenate(senate);
                                    new_election.addCandidate(canName, votes, party, winner);
                                    current_election.setNext(new_election);
                                    break;
                                }
                                current_election = current_election.getNext();
                            }
                        }
                        current_state = current_state.getNext();
                    } while (current_state != current_year.getStates());
                }
                current_year = current_year.getNext();
            }
        }
    }

    /*
     * DO NOT EDIT
     * 
     * Calls the next method to get the difference in voter turnout between two
     * years
     * 
     * @param int firstYear First year to track
     * 
     * @param int secondYear Second year to track
     * 
     * @param String state State name to track elections in
     * 
     * @return int Change in voter turnout between two years in that state
     */
    public int changeInTurnout(int firstYear, int secondYear, String state) {
        // DO NOT EDIT
        int last = totalVotes(firstYear, state);
        int first = totalVotes(secondYear, state);
        return last - first;
    }

    /*
     * Given a state name, find the total number of votes cast
     * in all elections in that state in the given year and return that number
     * 
     * If no elections occured in that state in that year, return 0
     * 
     * Use the ElectionNode method getVotes() to get the total votes for any single
     * election
     * 
     * @param year The year to track votes in
     * 
     * @param stateName The state to track votes for
     * 
     * @return avg number of votes this state in this year
     */
    public int totalVotes(int year, String stateName) {
        // WRITE YOUR CODE HERE
        int total_votes = 0;
        YearNode current_year = years;
        while (current_year != null) {
            if (current_year.getYear() == year) {
                StateNode current_state = current_year.getStates();
                do {
                    if (current_state.getStateName().equals(stateName)) {
                        ElectionNode current_election = current_state.getElections();
                        while (current_election != null) {
                            total_votes += current_election.getVotes();
                            current_election = current_election.getNext();
                        }
                        break;
                    }
                    current_state = current_state.getNext();
                } while (current_state != current_year.getStates());
            }
            current_year = current_year.getNext();
        }
        return total_votes;
    }

    /*
     * Given a state name and a year, find the average number of votes in that
     * state's elections in the given year
     * 
     * @param year The year to track votes in
     * 
     * @param stateName The state to track votes for
     * 
     * @return avg number of votes this state in this year
     */
    public int averageVotes(int year, String stateName) {
        // WRITE YOUR CODE HERE
        int total_votes = 0;
        int total_elections = 0;
        YearNode current_year = years;
        while (current_year != null) {
            if (current_year.getYear() == year) {
                StateNode current_state = current_year.getStates();
                do {
                    if (current_state.getStateName().equals(stateName)) {
                        ElectionNode current_election = current_state.getElections();
                        while (current_election != null) {
                            total_votes += current_election.getVotes();
                            total_elections++;
                            current_election = current_election.getNext();
                        }
                        break;
                    }
                    current_state = current_state.getNext();
                } while (current_state != current_year.getStates());
            }
            current_year = current_year.getNext();
        }
        return total_votes / total_elections;
    }

    /*
     * Given a candidate name, return the party they most recently ran with
     * 
     * Search each year node for elections with the given candidate
     * name. Update that party each time you see the candidates name and
     * return the party they most recently ran with
     * 
     * @param candidateName name to find
     * 
     * @return String party abbreviation
     */
    public String candidatesParty(String candidateName) {
        // WRITE YOUR CODE HERE
        String party = "";
        YearNode current_year = years;
        while (current_year != null) {
            StateNode current_state = current_year.getStates();
            do {
                ElectionNode current_election = current_state.getElections();
                while (current_election != null) {
                    if (current_election.getCandidates().contains(candidateName)) {
                        party = current_election.getParty(candidateName);
                    }
                    current_election = current_election.getNext();
                }
                current_state = current_state.getNext();
            } while (current_state != current_year.getStates());
            current_year = current_year.getNext();
        }
        return party;
    }
}
