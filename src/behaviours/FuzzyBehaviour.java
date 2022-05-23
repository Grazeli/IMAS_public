package behaviours;

import java.util.ArrayList;
import java.util.List;

import format.Request;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import others.UtilLoggingConfiguration;
import others.Utils;
import agents.FuzzyAgent;

public class FuzzyBehaviour extends CyclicBehaviour {
	private FuzzyAgent myAgent;
	private UtilLoggingConfiguration logger = new UtilLoggingConfiguration();
	
	public FuzzyBehaviour(FuzzyAgent a) {
		super(a);
		this.myAgent = a;
	}

	@Override
	public void action() {
		ACLMessage msg = this.myAgent.blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
		
		try {
			Request req = (Request) msg.getContentObject();
			List<ArrayList<Double>> requests = req.getRequests();
			logger.warning("[" + this.myAgent.getLocalName() + "] Received requests: " + requests);
			
			// Evaluate each request
			ArrayList<Double> response = new ArrayList<Double>();
			for (int i = 0; i < requests.size(); i++) { 
				// Evaluate FIS
				double result = (double) this.myAgent.evaluateFIS(requests.get(i));
				response.add(result);
			}

			// Send response back to manager agent
			Utils.informAgent(this.myAgent, msg.getSender(), response);
			
		} catch (UnreadableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
	}

}
