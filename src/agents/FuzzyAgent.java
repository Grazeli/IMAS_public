package agents;
import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.rule.Variable;
import others.UtilLoggingConfiguration;
import others.Utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import behaviours.FuzzyBehaviour;

public class FuzzyAgent extends Agent {
	private FIS fis;
	private ArrayList<String> inputvars;
	private String outputvar;
	private UtilLoggingConfiguration logger = new UtilLoggingConfiguration();
	
	public void setup() {	
		String name = "Not defined";
		String fclname = "Not defined";
		String application = "Not defined";

		// Get arguments:
		//   0: fuzzy agent name
		//   1: FCL name
		//   2: application name
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			name = (String) args[0];	
			fclname = (String) args[1];	
			application = (String) args[2];
		}
		
		// Register service (type is application name)
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName (getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(application + "FuzzyRequest");
		sd.setType(application);	// type of service is application name
		sd.addOntologies("ontologia");
		//Agents that want to use this service need to speak the fipa sl language
		sd.addLanguages(new SLCodec().getName());
		dfd.addServices(sd);
		 
		try {
			DFService.register(this,dfd);
		} catch(FIPAException e){
			System.err.println("Agent"+ getLocalName()+ ":" +e.getMessage()); 
		}
        
		// Load FCL, create FIS and get input and output variables
		fis = loadFcl(fclname);
		List vars = getFclVariables(fclname);
		inputvars = (ArrayList<String>) vars.get(0);
		outputvar = (String) vars.get(1);
		
		// Print information about new Fuzzy Agent
		logger.warning("\n[" + getLocalName() + "] Agent " + getAID().getName() + " created with name " + name + " passed by parameter."
				+ "\n\t\tApplication: " + application + " FCL: " + fclname);
				
		addBehaviour (new FuzzyBehaviour (this));
	}
	
    /**
     * From a given fcl name, obtain the corresponding FCL file
     * and create a FIS using it.
     * @param fclname Name of FCL used in configuration files
     */
	private FIS loadFcl(String fclname) {
		// Load from 'FCL' file
        FIS new_fis = FIS.load(Utils.getFCLfilepath(fclname), true);

        // Error while loading?
        if( new_fis == null ) { 
            System.err.println("\n[" + getLocalName() + "] Can't load FCL: '" + Utils.getFCLfilepath(fclname) + "'");
            return new_fis;
        }
        
        return new_fis;
	}
	
    /**
     * From a given fcl name, obtain the corresponding FCL file
     * and parse it to obtain the input and output variables
     * @param fclname Name of FCL used in configuration files
     */
	private List getFclVariables(String fclname) {
		List<String> inputVariables = new ArrayList<String>();
		String outputVariable = "";
		List returnList = new ArrayList();
		
		// Parse FCL using FileReader
		FileReader input;
		try {
			input = new FileReader(Utils.getFCLfilepath(fclname));
			BufferedReader bufRead = new BufferedReader(input);
			String myLine = null;
			
			while ((myLine = bufRead.readLine()) != null)
			{   
			    //System.out.println(myLine); 

				if(myLine.contains("VAR_INPUT")) {
					// Read all variables
					while (!(myLine = bufRead.readLine()).contains("END_VAR")) {
						inputVariables.add(myLine.split(":")[0].trim());
					}
				}
				if(myLine.contains("VAR_OUTPUT")) {
					// Read output variable
					outputVariable = bufRead.readLine().split(":")[0].trim();	
					}
			}
			
		} catch (FileNotFoundException e) {
            System.err.println("\n[" + getLocalName() + "] Can't load FCL: '" + Utils.getFCLfilepath(fclname) + "'");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		returnList.add(inputVariables);
		returnList.add(outputVariable);
		
		return returnList;
	}
	
    /**
     * Evaluate fis using a set of input variables and their values
     * and get the defuzzified output variable
     * and create a FIS using it.
     * @param inputVariables Names of the input variables
     * @param outputVariable Name of output variable
     * @param inputs List of numerical value of each input
     */
	public double evaluateFIS(ArrayList<Double> inputs) {

		String infoString ="Input:\t";
		
		// Set inputs
		for (int i = 0; i < this.inputvars.size(); i++) { 
			fis.setVariable(this.inputvars.get(i), inputs.get(i));
			infoString += String.format(" %s: %2.2f", this.inputvars.get(i), inputs.get(i));
		}

		// Evaluate
		fis.evaluate();
		
		// Get output
		Variable output = fis.getVariable(this.outputvar);
		
		// Print info
		infoString += String.format("\n\t     Output:\t %2.2f" , output.getLatestDefuzzifiedValue());
		logger.warning("[" + getLocalName() + "]" + infoString);

		return output.getLatestDefuzzifiedValue();
	}
}
