package behaviours;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import agents.UserAgent;
import behaviours.UserBehaviour;
import format.Configuration;
import format.MessageToAgent;
import format.Request;
import format.Answer;
import others.UtilLoggingConfiguration;
import others.Utils;

public class UserBehaviour extends CyclicBehaviour {

	private UserAgent myAgent;
	private Scanner scanner;
	private ArrayList<Configuration> configs = new ArrayList<Configuration>();
	private UtilLoggingConfiguration logger = new UtilLoggingConfiguration();
	
	public UserBehaviour(UserAgent a) {
		super(a);
		this.myAgent = a;
		this.scanner = new Scanner(System.in);;
	}
	
	@Override
	public void action () {
		scanner = new Scanner (System.in);
		boolean go = true;
		do {
			go = true;
			System.out.printf("[" + this.myAgent.getLocalName() + "] Introduce command (I, D, L, Q or H): ");
			String action = scanner.nextLine().toUpperCase();
			
			if (!action.equals("I") && !action.equals("D") && !action.equals("L") && !action.equals("H") && !action.equals("Q")) {
				System.out.println("\n[" + this.myAgent.getLocalName() + "] Incorrect command, action must be (I, D, L, Q or H)\n");
				go = false;
				continue;
			}
			
			if (action.equals("I") || action.equals("D")){
				System.out.printf("[" + this.myAgent.getLocalName() + "] Introduce filename: ");
				String filename = "";
			
				if (!(filename = scanner.nextLine()).isEmpty()) {
				
				try {				
					if (action.equals("I")) {
						// Read xml
						Configuration config = Utils.parseConfig(filename);
						if (config != null) {
							boolean same = false;
							for (int i=0; i<configs.size(); ++i) {
								if (configs.get(i).getApplication().equals(config.getApplication())) {
									go = false;
									System.out.println("\n[" + this.myAgent.getLocalName() + "] Configuration already defined for "+config.getApplication()+"\n");
									same = true;
									break;
								}
							}
							if (!same) {
								configs.add(config);
								System.out.println("[" + this.myAgent.getLocalName() + "] Configuration loaded for " + config.getApplication() + "!\n");
								MessageToAgent msm = new MessageToAgent("configuration", config);
								Utils.sendMessage(this.myAgent, "manage", msm);
							}
						}
						else {
							go = false;
						}
					} else if (action.equals("D")) {
						// Read .txt request
						Request req = Utils.parseRequest(filename);
						
						if (req != null) {
							boolean found = false;
							for (int i=0; i<configs.size(); ++i) {
								if (configs.get(i).getApplication().equals(req.getApplication())) {
									System.out.println("[" + this.myAgent.getLocalName() + "] Application "+ req.getApplication()+ " found\n");
									found = true;
									break;
								}
							}
							if (found) {
								MessageToAgent msm = new MessageToAgent("request", req);
								Utils.sendMessage(this.myAgent, "manage", msm);
							}
							else {
								go = false;
								System.out.println("[" + this.myAgent.getLocalName() + "] Configuration " + req.getApplication() + " has not been loaded yet! Load it first.\n");
							}
						} else {
							go = false;
						}
					}
				
				}catch (Exception e) {
					e.printStackTrace();
				}
				} else {
					System.out.println("[" + this.myAgent.getLocalName() + "] Filename can't be empty!\n");
					go = false;
				}
			}
			
			else if(action.equals("L")) {
				// Set debug level
				System.out.printf("[" + this.myAgent.getLocalName() + "] Choose a debug level (0-3): ");
				Boolean validLevel = false;
				while(!validLevel) {
					String debuglevel = scanner.nextLine();
					try {
						int level = Integer.parseInt(debuglevel);
						validLevel = true;
						switch(level){
						case (0):
							logger.setLevel(Level.OFF);
						break;
						case (1):
							logger.setLevel(Level.SEVERE);
						break;
						case (2):
							logger.setLevel(Level.WARNING);
						break;
						case (3):
							logger.setLevel(Level.INFO);
						break;
						default:
							validLevel = false;
							System.out.printf("[" + this.myAgent.getLocalName() + "] Choose a valid debug level (0-3): ");
							break;
						}
					} catch (NumberFormatException e) {
						System.out.printf("[" + this.myAgent.getLocalName() + "] Choose a valid debug level (0-3): ");
					}
				}
				
				go = false;
			}
			
			else if(action.equals("H")) {
				// Print help
				System.out.println("[" + this.myAgent.getLocalName() + "] Help: ");
				System.out.println("\t   I: set configuration file ");
				System.out.println("\t   D: provide requests file");
				System.out.println("\t   L: set debug level (0-3)");
				System.out.println("\t   Q: quit program execution");
				System.out.println("\t   H: print this menu\n");
				go = false;
			}
			
			else if(action.equals("Q")) {
				// Quit
				System.out.println("[" + this.myAgent.getLocalName() + "] Quitting program...");
				System.exit(0);
			}
			
		} while(go==false);				
		
		
		ACLMessage msg= this.myAgent.blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		try {
			MessageToAgent msu = (MessageToAgent) msg.getContentObject();
			
			// Number of agents created
			if ("responseConfig".equals(msu.getType())) {
				System.out.println("\n[" + this.myAgent.getLocalName() + "] Manager agent created "+msu.getObject().toString()+" fuzzy agents.\n");
				
				// Small delay
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			// Response to the Request
			else if ("responseRequest".equals(msu.getType())) {
				System.out.println("\n[" + this.myAgent.getLocalName() + "] Request outputs:" + ((Answer) msu.getObject()).getOutputs());
				System.out.println("[" + this.myAgent.getLocalName() + "] Details of the results in path: " + ((Answer) msu.getObject()).getFilepath() + "\n");
			}

		} catch (UnreadableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
