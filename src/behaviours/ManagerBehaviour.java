package behaviours;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import agents.ManagerAgent;
import format.Answer;
import format.Configuration;
import format.MessageToAgent;
import format.Request;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import others.UtilLoggingConfiguration;
import others.Utils;

public class ManagerBehaviour extends CyclicBehaviour {
	private ManagerAgent myAgent;
	private UtilLoggingConfiguration logger = new UtilLoggingConfiguration();

	public ManagerBehaviour(ManagerAgent a) {
		super(a);
		this.myAgent = a;
	}

	@Override
	public void action () {
		ACLMessage msg = this.myAgent.blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
		try {
			
			MessageToAgent msm = (MessageToAgent) msg.getContentObject();
			MessageToAgent answer = new MessageToAgent("", null);
			
			if ("configuration".equals(msm.getType())) {
				// Operation
				Configuration config = (Configuration) msm.getObject();
				
				// Create agents
				this.createFuzzyAgents(config.getFuzzyagents(), config.getFuzzySettings(), config.getApplication());
				
				// Create answer to send to the user agent
				answer.setType("responseConfig");
				answer.setObject(config.getFuzzyagents());
				
				this.myAgent.putConfig(config.getApplication(), config);
				
			}
			
			else if ("request".equals(msm.getType())) {
				// Operation
				Request req = (Request) msm.getObject();
				
				logger.severe("[" + this.myAgent.getLocalName() + "] Requests of " + req.getApplication() + " received from User Agent");
				
				// Pass the different request inside req to the fuzzy agents of the application of req
				// Send request to fuzzy agents that implement the application
				Utils.sendMessage(this.myAgent, req.getApplication(), req);
				
				// Get inference responses from the Fuzzy Agents
				ArrayList<AgentController> fz = this.myAgent.getFuzzyAgent(req.getApplication());
				List<ArrayList<Double>> outputsFuzzy = new ArrayList<ArrayList<Double>>();
				List<String> senders = new ArrayList<String>();
				
				for (int i=0; i<fz.size(); ++i) {
					ACLMessage msg_fis = this.myAgent.blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
					ArrayList<Double> outputFuzzy = (ArrayList<Double>) msg_fis.getContentObject();
					outputsFuzzy.add(outputFuzzy);
					String sender = "Sender: " + msg_fis.getSender().getLocalName();
					senders.add(sender);
					
				}
				
				// Aggregate the final solution for each request
				ArrayList<Double> outputs = new ArrayList<Double>();
				String aggregation = this.myAgent.getConfiguration(req.getApplication()).getAggregation();
				for (int i=0; i<req.getRequests().size(); ++i) {
						
					if (aggregation.equals("average")) {
						Double sum = 0.0;
						for (int j=0; j<fz.size(); ++j) {
							sum += outputsFuzzy.get(j).get(i);
							
						}
						Double average = sum / fz.size();
						outputs.add(new BigDecimal(average).setScale(3, RoundingMode.HALF_UP).doubleValue());
					}
					else if (aggregation.equals("min")) {
						Double min = Double.POSITIVE_INFINITY;
						for (int j=0; j<fz.size(); ++j) {
							if (outputsFuzzy.get(j).get(i) < min) {
								min = outputsFuzzy.get(j).get(i);
							}
						}
						outputs.add(new BigDecimal(min).setScale(3, RoundingMode.HALF_UP).doubleValue());
					}
					else if (aggregation.equals("max")) {
						Double max = Double.NEGATIVE_INFINITY;
						for (int j=0; j<fz.size(); ++j) {
							if (outputsFuzzy.get(j).get(i) > max) {
								max = outputsFuzzy.get(j).get(i);
							}
						}
						outputs.add(new BigDecimal(max).setScale(3, RoundingMode.HALF_UP).doubleValue());
					}
				}
				
				for (int i=0; i<outputs.size(); ++i) { 
					logger.severe("[" + this.myAgent.getLocalName() + "] Output: " + outputs.get(i).toString());
				}
				
				String path = "";
				
				try {
					new File(Utils.getOutputPath()).mkdirs();
					path = Utils.getOutputPath() + "/output_" + req.getApplication() + ".txt"; 
					PrintWriter out = new PrintWriter(path);
					out.println("Application: " + req.getApplication());
					out.println("Aggregation: " + aggregation);
					out.println();
					
					for (int i = 0; i < outputs.size(); ++i) {
						out.println("-------------- Request " + (i+1) + " --------------");
						for (int j = 0; j < senders.size(); ++j) {
							out.println(senders.get(j) + "-> Output: " + outputsFuzzy.get(j).get(i));
						}
						out.println("Final Output: " + outputs.get(i) + "\n");
					}
					out.close();
				} catch (java.io.FileNotFoundException e) {
					System.out.println(e);
				}
				catch (java.lang.IndexOutOfBoundsException e) {
					System.out.println(e);
				}
				
				// Create answer to send to the user agent
				Answer a = new Answer(outputs, path);
				answer.setType("responseRequest");
				answer.setObject(a);
				
			}
			
			// Send answer to UserAgent
			Utils.informAgent(this.myAgent, msg.getSender(), answer);
			 
			
		} catch (java.lang.ClassCastException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (UnreadableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}

	private void createFuzzyAgents(int n, List<String> fcls, String application) {
		// Get a hold on JADE runtime
		try {
			Runtime rt = Runtime.instance();
			// Exit the JVM when there are no more containers around
			rt.setCloseVM(true);
			Profile p = new ProfileImpl(true);
			AgentContainer ac = rt.createAgentContainer(p);

			// Create another peripheral container within the same JVM
			// NB. Two containers CAN'T share the same Profile object!!! -->
			// Create a new one.
			Profile p2 = new ProfileImpl(false);
			AgentController another = null;
			
			String className = "agents.FuzzyAgent";
			
			logger.severe("[" + this.myAgent.getLocalName() + "] Creating agents in createFuzzyAgents");
			
			ArrayList<AgentController> fz = new ArrayList<AgentController>();
			
			for (int i = 0; i < n; i++) {
				String name = "FuzzyAgent".concat(String.valueOf(this.myAgent.getNumFuzzyAgents()));
				
				// Arguments used by the FuzzyAgent setup
				Object[] arguments = new Object[3];
				arguments[0] = name;
				arguments[1] = fcls.get(i);
				arguments[2] = application;
				another = ac.createNewAgent(name, className, arguments);
				fz.add(another);
				this.myAgent.incrementNumFuzzyAgent();
				another.start();
			}
			
			this.myAgent.putFuzzyAgents(application, fz);
			
		} catch(jade.wrapper.StaleProxyException e) {
			System.err.println("[" + this.myAgent.getLocalName() + "] ERROR for creating the agent Reason: "+e.toString());
			e.printStackTrace();
		} catch(Exception e4) {
			System.err.println("[" + this.myAgent.getLocalName() + "] Error in reading config file"+e4.toString());
		}
	}
}
