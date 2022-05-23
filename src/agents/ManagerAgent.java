package agents;

import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import jade.wrapper.AgentController;
import java.util.ArrayList;

import behaviours.ManagerBehaviour;

import java.util.Map;
import java.util.HashMap;

import format.Configuration;

public class ManagerAgent extends Agent{	
	private Map<String, ArrayList<AgentController> > fuzzyAgents = new HashMap<String, ArrayList<AgentController> >();
	private Map<String,Configuration> configs = new HashMap<String,Configuration>();
	private int numFuzzyAgents;

	
	public void setup() {
		// Register service
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName (getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName("ManageUserRequest");
		sd.setType("manage");
		sd.addOntologies("ontologia");
		//Agents that want to use this service need to speak the fipa sl language
		sd.addLanguages(new SLCodec().getName());
		dfd.addServices(sd);
		 
		try {
			DFService.register(this,dfd);
		} catch(FIPAException e){
			System.err.println("Agent"+ getLocalName()+ ":" +e.getMessage()); 
		}
		
		// Add behaviour
		addBehaviour (new ManagerBehaviour(this));
	
	}
	
	public ArrayList<AgentController> getFuzzyAgent(String application) {
		return fuzzyAgents.get(application);
	}
	
	public Configuration getConfiguration(String application) {
		return configs.get(application);
	}
	
	public int getNumFuzzyAgents() {
		return this.numFuzzyAgents;
	}
	
	public void incrementNumFuzzyAgent() {
		++this.numFuzzyAgents;
	}
	
	public void putFuzzyAgents(String application, ArrayList<AgentController> fz) {
		this.fuzzyAgents.put(application, fz);
	}
	
	public void putConfig(String application, Configuration c) {
		this.configs.put(application, c);
	}
	
}
