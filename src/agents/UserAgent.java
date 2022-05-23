package agents;

import jade.core.Agent;
import behaviours.UserBehaviour;

public class UserAgent extends Agent {

	public void setup() {
		addBehaviour(new UserBehaviour(this));
	}
}
