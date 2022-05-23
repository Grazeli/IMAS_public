package others;
import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import format.Configuration;
import format.Request;

public class Utils 
{
	final static String FILES_PATH = "files";
	final static String OUTPUT_PATH = "output";
	private static UtilLoggingConfiguration logger = new UtilLoggingConfiguration();
	
	/**
	 * Allows to find all the agents that implement a service of a given type
	 * @param agent Agent used to perform the search
	 * @param type  Searched type of service
	 * @return List of agents that provide the given service
	 */
    protected static DFAgentDescription [] findAgents(Agent agent, String type)
    {
    	//define the characteristics of the type of service to find
        DFAgentDescription template=new DFAgentDescription();
        ServiceDescription templateSd=new ServiceDescription();
        templateSd.setType(type);
        template.addServices(templateSd);
        
        SearchConstraints sc = new SearchConstraints();
        sc.setMaxResults(Long.MAX_VALUE);
        try
        {
            DFAgentDescription [] results = DFService.search(agent, template, sc);
            return results;
        }
        catch(FIPAException e)
        {
            //JOptionPane.showMessageDialog(null, "Agent "+getLocalName()+": "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        	e.printStackTrace();
        }
        
        return null;
    }
    
    
    /**
     * Send an object from the given agent to all agents that provide a
     * service of the given type using REQUEST.
     * @param agent Sender agent
     * @param type Type of service
     * @param object Message to send
     */
    public static void sendMessage(Agent agent, String type, Object object)
    {
        DFAgentDescription[] dfd;
        dfd=findAgents(agent, type);
        
        try
        {
            if(dfd!=null)
            {
            	ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
            	
            	for(int i=0;i<dfd.length;i++)
	        		aclMessage.addReceiver(dfd[i].getName());
            	
                aclMessage.setOntology("ontologia");
                //set defined language for the service
                aclMessage.setLanguage(new SLCodec().getName());
                //message transmitted using XML
                aclMessage.setEnvelope(new Envelope());
				//change coding of the envelope
				aclMessage.getEnvelope().setPayloadEncoding("ISO8859_1");
                //aclMessage.getEnvelope().setAclRepresentation(FIPANames.ACLCodec.XML); 
        		aclMessage.setContentObject((Serializable)object);
        		agent.send(aclMessage);       		
            }
        }
        catch(IOException e)
        {
            //JOptionPane.showMessageDialog(null, "Agent "+getLocalName()+": "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Send an object from the given agent to all agents that provide a
     * service of the given type using INFORM.
     * @param agent Sender agent
     * @param type Type of service
     * @param object Message to send
     */
    public static void inform(Agent agent, String type, Object object)
    {
        DFAgentDescription[] dfd;
        dfd=findAgents(agent, type);
        
        try
        {
            if(dfd!=null)
            {
            	ACLMessage aclMessage = new ACLMessage(ACLMessage.INFORM);
            	
            	for(int i=0;i<dfd.length;i++)
	        		aclMessage.addReceiver(dfd[i].getName());
            	
                aclMessage.setOntology("ontologia");
                //set defined language for the service
                aclMessage.setLanguage(new SLCodec().getName());
                //message transmitted using XML
                aclMessage.setEnvelope(new Envelope());
				//change coding of the envelope
				aclMessage.getEnvelope().setPayloadEncoding("ISO8859_1");
                //aclMessage.getEnvelope().setAclRepresentation(FIPANames.ACLCodec.XML); 
        		aclMessage.setContentObject((Serializable)object);
        		agent.send(aclMessage);       		
            }
        }
        catch(IOException e)
        {
            //JOptionPane.showMessageDialog(null, "Agent "+getLocalName()+": "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Send an object from the sender agent to the receiver agent.
     * @param senderAgent Sender agent
     * @param receiverAgentAID Receiver agent AID
     * @param object Message to send
     */
    public static void informAgent(Agent senderAgent, AID receiverAgentAID, Object object)
    {        
        try
        {
          	ACLMessage aclMessage = new ACLMessage(ACLMessage.INFORM);
        	
    		aclMessage.addReceiver(receiverAgentAID);
            aclMessage.setOntology("ontologia");
            //set defined language for the service
            aclMessage.setLanguage(new SLCodec().getName());
            //message transmitted using XML
            aclMessage.setEnvelope(new Envelope());
			//change coding of the envelope
			aclMessage.getEnvelope().setPayloadEncoding("ISO8859_1");
            //aclMessage.getEnvelope().setAclRepresentation(FIPANames.ACLCodec.XML); 
    		aclMessage.setContentObject((Serializable)object);
    		senderAgent.send(aclMessage); 
        }
        catch(IOException e)
        {
            //JOptionPane.showMessageDialog(null, "Agent "+getLocalName()+": "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Allows to find the agents that implement a service of the given type and returns the first of them. 
	 * @param agent Agent used to perform the search
	 * @param type  Searched type of service
     * @return FirstAgent Agent that provides the service
     */
    protected static DFAgentDescription findAgent(Agent agent, String tipo)
    {
    	//define the characteristics of the type of service to find
        DFAgentDescription template=new DFAgentDescription();
        ServiceDescription templateSd=new ServiceDescription();
        templateSd.setType(tipo);
        template.addServices(templateSd);
        
        SearchConstraints sc = new SearchConstraints();
        sc.setMaxResults(new Long(1));
        
        try
        {
            DFAgentDescription [] results = DFService.search(agent, template, sc);
            if (results.length > 0) 
            {
                //System.out.println("Agent "+agent.getLocalName()+" found the given agents:");
                for (int i = 0; i < results.length; ++i) 
                {
                    DFAgentDescription dfd = results[i];
                    AID provider = dfd.getName();
                    
                    //one agent can provide different services but we are only interested in one "type"
                    Iterator it = dfd.getAllServices();
                    while (it.hasNext())
                    {
                        ServiceDescription sd = (ServiceDescription) it.next();
                        if (sd.getType().equals(tipo))
                        {
                            System.out.println("- Service \""+sd.getName()+"\" provided by agent "+provider.getName());
                            
                            return dfd;
                        }
                    }
                }
            }	
            else
            {
                //JOptionPane.showMessageDialog(null, "Agent "+getLocalName()+" couldn't find a searcher service", "Error", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch(FIPAException e)
        {
            //JOptionPane.showMessageDialog(null, "Agent "+getLocalName()+": "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        	e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Parse configuration from a given input file (xml format)
     * @param input_file Name of the configuration file (only name, not filepath)
     */
	public static Configuration parseConfig(String input_file) {
		Configuration config = null;
		
		try {
			File inputFile = new File(FILES_PATH, input_file);
			//System.out.println("\nFilename: " + input_file);
			
			// Parse input_file as xml using DocumentBuilder
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			 
			Node nNode = doc.getElementsByTagName("SimulationSettings").item(0);
			Element eElement = (Element) nNode;
			 
			// Create Configuration object and get each configuration parameter 
			String cfg_title = eElement.getElementsByTagName("title").item(0).getTextContent();
			String cfg_application = eElement.getElementsByTagName("application").item(0).getTextContent();
			int cfg_num_agents = Integer.parseInt(eElement.getElementsByTagName("fuzzyagents").item(0).getTextContent());
			String cfg_fuzzy_settigns = eElement.getElementsByTagName("fuzzySettings").item(0).getTextContent();
			String cfg_aggregation = eElement.getElementsByTagName("aggregation").item(0).getTextContent();
			
			List<String> cfg_fsettings_list = Arrays.asList(cfg_fuzzy_settigns.split("\\s*,\\s*"));
			
			config = new Configuration(cfg_application, cfg_num_agents, cfg_fsettings_list, cfg_aggregation);

			logger.info("\nNew configuration parsed");
			logger.info("------------------------");
			logger.info("title: " + cfg_title); 
			logger.info("application: " + cfg_application); 
			logger.info("fuzzyagents: " + cfg_num_agents); 
			logger.info("fuzzySettings: " + cfg_fsettings_list); 
			logger.info("aggregation: " + cfg_aggregation); 
			logger.info("------------------------\n");


			} catch (java.io.FileNotFoundException e1) {
				  System.out.println("File not found!\n");
			} catch (Exception e) {
				  e.printStackTrace();}
		
		return config;
	}
	
    /**
     * Parse request from a given input file
     * @param input_file Name of the configuration file (only name, not filepath)
     */
	public static Request parseRequest(String input_file) {
		Request req = null;
		
		try {
			File inputFile = new File(FILES_PATH, input_file);
			//System.out.println("\nFilename: " + input_file);
			
			String application = null;
			List<ArrayList<Double>> requests = new ArrayList<ArrayList<Double>>();
			
			// Parse input_file as txt using FileReader
			FileReader input = new FileReader(inputFile);
			BufferedReader bufRead = new BufferedReader(input);
			String myLine = null;
			
			if ((myLine = bufRead.readLine()) != null) {
				application = myLine;
			}
			
			while ( (myLine = bufRead.readLine()) != null)
			{    
			    ArrayList<Double> array = new ArrayList<Double>();
			    String[] r = myLine.split(","); 
			    //double[] numbers = new double[r.length];
			    for (int i = 0; i < r.length; ++i) {
			        double number = Double.parseDouble(r[i]);
			        array.add(number);
			    }
			    requests.add(array);
			}
			 
			// Create Request object
			req = new Request(application, requests);
								
			logger.info("\nNew request parsed");
			logger.info("------------------------");
			logger.info("application: " + application); 
			for (int i = 0; i < requests.size(); ++i) {
				logger.info("Request " + i + ":" + requests.get(i));
			}
			logger.info("------------------------\n");


			} catch (java.io.FileNotFoundException e1) {
				  System.out.println("File not found!\n");
			}catch (Exception e) {
				  e.printStackTrace();}
		
		return req;
	}
	
    /**
     * Return the FCL filepath corresponding to a given fcl name
     * @param fclname Name of FCL used in configuration files
     */
	public static String getFCLfilepath(String fclname) {
	    // FCL filename a Hashtable
	    Hashtable<String, String> fcl_hashtable = new Hashtable<String, String>();
	    
	    // Get FCL file corresponding to fcl name
	    File fclFile = new File(FILES_PATH, fclname + ".fcl");
	    
	    // Return filepath
		return fclFile.toString();
	}


	public static String getFilesPath() {
		// TODO Auto-generated method stub
		return FILES_PATH;
	}
	
	public static String getOutputPath() {
		return OUTPUT_PATH;
	}
}
