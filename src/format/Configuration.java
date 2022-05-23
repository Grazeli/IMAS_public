package format;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;


public class Configuration implements Serializable {
	private String application;
	private int fuzzyagents; 
	private List<String> fuzzySettings = new ArrayList<String>();
	private String aggregation;
    
    public Configuration (String application, int fuzzyagents, List<String> fuzzySettings, String aggregation) {
    	super();
		this.application = application;
		this.fuzzyagents = fuzzyagents;
		this.fuzzySettings = fuzzySettings;
		this.aggregation = aggregation;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public int getFuzzyagents() {
		return fuzzyagents;
	}

	public void setFuzzyagents(int fuzzyagents) {
		this.fuzzyagents = fuzzyagents;
	}

	public List<String> getFuzzySettings() {
		return fuzzySettings;
	}

	public void setFuzzySettings(List<String> fuzzySettings) {
		this.fuzzySettings = fuzzySettings;
	}
	
	public String getAggregation() {
		return aggregation;
	}

	public void setAggregation(String aggregation) {
		this.aggregation = aggregation;
	}

}
